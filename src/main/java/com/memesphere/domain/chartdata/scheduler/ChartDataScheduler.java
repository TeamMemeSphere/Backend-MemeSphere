package com.memesphere.domain.chartdata.scheduler;

import com.memesphere.domain.binance.dto.response.BinanceTickerResponse;
import com.memesphere.domain.binance.service.BinanceQueryService;
import com.memesphere.domain.chartdata.entity.ChartData;
import com.memesphere.domain.chartdata.repository.ChartDataRepository;
import com.memesphere.domain.dashboard.dto.response.DashboardTrendResponse;
import com.memesphere.domain.notification.service.PushNotificationService;
import com.memesphere.global.apipayload.code.status.ErrorStatus;
import com.memesphere.global.apipayload.exception.GeneralException;
import com.memesphere.domain.memecoin.entity.MemeCoin;
import com.memesphere.domain.memecoin.repository.MemeCoinRepository;
import com.memesphere.domain.memecoin.service.MemeCoinQueryService;
import com.memesphere.global.jwt.LoggedInUserStore;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.memesphere.domain.chartdata.converter.ChartDataConverter.toChartData;

@Component
@RequiredArgsConstructor
public class ChartDataScheduler {
    private final MemeCoinRepository memeCoinRepository;
    private final BinanceQueryService binanceQueryService;
    private final MemeCoinQueryService memeCoinQueryService;
    private final LoggedInUserStore loggedInUserStore;
    private final PushNotificationService pushNotificationService;
    private final ChartDataRepository chartDataRepository;

    @Scheduled(cron = "0 0/10 * * * ?") // 0, 10, 20, 30, 40, 50분에 실행
    @Transactional
    public void updateChartData() {

        Set<Long> loggedInUsers = loggedInUserStore.getLoggedInUsers();

        List<MemeCoin> memeCoins = memeCoinRepository.findAll();

        // 이전에 기록된 top5 밈코인
        List<MemeCoin> prevCoinList = memeCoinRepository.findTop5OrderByRank();

        for (MemeCoin memeCoin : memeCoins) {
            try {
                String symbol = memeCoin.getSymbol() + "USDT";
                BinanceTickerResponse response = binanceQueryService.getTickerData(symbol);

                ChartData chartData = toChartData(memeCoin, response);

                memeCoinQueryService.updateChartData(memeCoin.getId(), chartData);

            } catch (Exception e) {
                throw new GeneralException(ErrorStatus.CANNOT_LOAD_CHARTDATA);
            }
        }

        for (Long userId : loggedInUsers) {
            pushNotificationService.send(userId);
        }

        // 밈코인 랭킹 업데이트
        updateRank(prevCoinList);
    }

    public void updateRank(List<MemeCoin> prevCoinList) {
        // 최신 거래량 top5 밈코인-차트데이터
        List<ChartData> updatedChartDataList = chartDataRepository.findTop5OrderByVolumeDesc();

        if (prevCoinList != null) {
            // 현재 top5에 들지 않는 밈코인 순위 처리
            List<MemeCoin> currCoinList = updatedChartDataList.stream().map(ChartData::getMemeCoin).toList();
            for (MemeCoin prevCoin : prevCoinList) {
                if (!currCoinList.contains(prevCoin)) prevCoin.updateRank(null);
            }
        }

        // 현재 top5 밈코인
        for (int i = 0; i < 5; i++) {
            MemeCoin memeCoin = updatedChartDataList.get(i).getMemeCoin();

            Integer currRank = i + 1;
            memeCoin.updateRank(currRank);
        }
    }
}


