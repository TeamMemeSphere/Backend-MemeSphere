package com.memesphere.domain.notification.service;

import com.memesphere.domain.chartdata.entity.ChartData;
import com.memesphere.domain.chartdata.repository.ChartDataRepository;
import com.memesphere.domain.memecoin.entity.MemeCoin;
import com.memesphere.domain.notification.converter.NotificationConverter;
import com.memesphere.domain.notification.entity.Notification;
import com.memesphere.domain.notification.repository.EmitterRepository;
import com.memesphere.domain.notification.repository.NotificationRepository;
import com.memesphere.domain.user.entity.User;
import com.memesphere.global.apipayload.ApiResponse;
import com.memesphere.global.apipayload.code.status.ErrorStatus;
import com.memesphere.global.apipayload.exception.GeneralException;
import com.memesphere.global.jwt.CustomUserDetails;
import com.memesphere.global.jwt.LoggedInUserStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class PushNotificationServiceImpl implements PushNotificationService {

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final ChartDataRepository chartDataRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final LoggedInUserStore loggedInUserStore;

    // 연결 지속 시간 설정 : 한시간
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    @Override
    public SseEmitter subscribe(Long userId, String lastEventId) {

        // 고유한 아이디 생성
        String emitterId = userId + "_" + System.currentTimeMillis(); // 사용자 id + 현재 시간을 밀리초 단위의 long값
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        loggedInUserStore.addUser(userId);

        // 클라이언트가 SSE 연결을 종료하면 실행됨
        emitter.onCompletion(() -> {
            emitterRepository.deleteById(emitterId);
            loggedInUserStore.removeUser(userId);

        });
        // 지정된 시간이 지나거나 클라이언트가 요청을 안하면 실행됨
        emitter.onTimeout(() -> {
            emitterRepository.deleteById(emitterId);
            loggedInUserStore.removeUser(userId);
        });

        // 최초 연결 더미데이터가 없으면 503 에러가 나므로 더미 데이터 생성
        sendToClient(emitter, emitterId, "EventStream Created. [userId=" + userId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            if (emitter != null) {
                emitter.send(SseEmitter.event()
                        .id(emitterId)
                        .data(data));
            }
        } catch (IOException exception) {
            log.error("SSE 연결 오류: {}", exception.getMessage());
            emitterRepository.deleteById(emitterId);
            throw new GeneralException(ErrorStatus.CANNOT_PUSH_NOTIFICATION);
        }
    }

    @Override
    public void send(Long userId) {
        List<Notification> notifications = notificationRepository.findAllByUserId(userId); // 사용자가 등록한 알림 전부 가져오기

        // 변동성을 초과하는 알림 필터링
        List<Notification> filteredNotifications = notifications.stream()
                .filter(notification -> isVolatilityExceeded(notification))
                .collect(Collectors.toList());

        if (!filteredNotifications.isEmpty()) {
            // 실시간 알림 전송 - 로그인 한 유저의 SseEmitter 모두 가져오기
            Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByUserId(String.valueOf(userId));

            sseEmitters.forEach((key, emitter) -> {
                executorService.submit(() -> {
                    filteredNotifications.forEach(notification -> {

                        emitterRepository.saveEventCache(key, notification);
                        sendToClient(emitter, key, NotificationConverter.toNotificationCreateResponse(notification, notification.getMemeCoin()));

                        try {
                            Thread.sleep(500); // 0.5초 간격으로 전송
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
                });
            });
        }
    }

    private boolean isVolatilityExceeded(Notification notification) {
        MemeCoin memeCoin = notification.getMemeCoin();
        if (memeCoin == null) {
            throw new GeneralException(ErrorStatus.MEMECOIN_NOT_FOUND);
        }

        List<ChartData> chartDataList = memeCoin.getChartDataList();
        if (chartDataList == null || chartDataList.isEmpty()) {
            throw new GeneralException(ErrorStatus.CANNOT_LOAD_CHARTDATA);
        }

        LocalDateTime notificationTime = notification.getCreatedAt();

        Integer count = notification.getStTime() / 10; //몇 번 가져올 것인지 결정
        Pageable pageable = (Pageable) PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<ChartData> lastNData = chartDataRepository.findByMemeCoinAndRecordedTimeAfterOrderByRecordedTimeDesc(memeCoin, notificationTime, pageable);

        if (lastNData.size() < count) {
            return false; // 비교할 데이터가 부족하면 알림을 보내지 않음
        }

//        BigDecimal sum = lastNData.stream()
//                .map(ChartData::getPriceChangeRate)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        BigDecimal average = sum.divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP);
//        BigDecimal definedVolatility = new BigDecimal(notification.getVolatility());
        Double sum = lastNData.stream()
                .mapToDouble(ChartData::getPriceChangeRate)
                .sum();
        Double average = sum / count;
        Double definedVolatility = notification.getVolatility().doubleValue();

        if (notification.getIsRising()) { // 상승인 경우
            return average.compareTo(definedVolatility) > 0;
        } else {
            return average.compareTo(definedVolatility) < 0;
        }
    }
}
