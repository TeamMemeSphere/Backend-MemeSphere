package com.memesphere.global.apipayload.code.status;

import com.memesphere.global.apipayload.code.BaseCode;
import com.memesphere.global.apipayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    // 에러 응답 예시
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // unsupported enum type
    UNSUPPORTED_SORT_TYPE(HttpStatus.BAD_REQUEST,"ENUM400","가능한 SortType은 MKT_CAP, VOLUME_24H, PRICE 입니다."),
    UNSUPPORTED_VIEW_TYPE(HttpStatus.BAD_REQUEST,"ENUM400","가능한 ViewType은 GRID, LIST 입니다."),

    // 밈코인 에러
    MEMECOIN_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMECOIN NOT FOUND", "밈코인을 찾을 수 없습니다."),

    // 콜렉션 에러
    COLLECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "COLLECTION NOT FOUND", "콜렉션을 찾을 수 없습니다."),
    COLLECTION_ALREADY_EXISTS(HttpStatus.NOT_FOUND, "COLLECTION ALREADY EXIST", "콜렉션에 해당 밈코인을 이미 등록했습니다."),

    // ChartData load 에러
    CANNOT_LOAD_CHARTDATA(HttpStatus.BAD_REQUEST, "CANNOT LOAD CHARTDATA", "ChartData를 Binance에서 로드할 수 없습니다."),
    CHARTDATA_NOT_FOUND(HttpStatus.NOT_FOUND, "CHARTDATA NOT FOUND", "차트 데이터를 찾을 수 없습니다."),

    // notification 에러
    CANNOT_CHECK_VOLATILITY(HttpStatus.NOT_FOUND, "CANNOT CHECK VOLATILITY", "변동성을 확인할 수 없습니다."),
    CANNOT_PUSH_NOTIFICATION(HttpStatus.BAD_REQUEST, "CANNOT PUSH NOTIFICATION", "알림 전송을 실패했습니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND", "알림을 찾을 수 없습니다."),

    // Auth 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER NOT FOUND", "유저를 찾을 수 없습니다."),
    PASSWORD_NOT_MATCH(HttpStatus.NOT_FOUND, "PASSWORD NOT MATCH", "비밀번호가 틀렸습니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER ALREADY EXIST ", "이미 존재하는 회원입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "NICKNAME ALREADY EXIST ", "이미 사용 중인 닉네임입니다."),
    REDIS_KEY_NOT_FOUND(HttpStatus.NOT_FOUND, "REDIS KEY NOT FOUND", "요청한 키가 Redis에 존재하지 않습니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "TOKEN NOT FOUND", "토큰을 찾을 수 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID TOKEN", "토큰이 유효하지 않습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED TOKEN", "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "UNSUPPORTED TOKEN", "지원하지 않는 토큰입니다."),
    INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "INVALID SIGNATURE", "잘못된 JWT 서명입니다"),
    SOCIAL_LOGIN_NOT_ALLOWED(HttpStatus.FORBIDDEN, "SOCIAL LOGIN NOT ALLOWED", "소셜 로그인 계정으로 비밀번호를 찾을 수 없습니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL SEND FAILED", "이메일 전송에 실패했습니다."),

    // 이미지 에러
    INVALID_FILE_EXTENTION(HttpStatus.BAD_REQUEST, "INVALID FILE EXTENSION", "지원되지 않는 파일 형식입니다."),
    PRESIGNED_URL_FAILED(HttpStatus.BAD_REQUEST, "PRESIGNED URL GENERATION FAILED", "presigned URL 생성에 실패했습니다."),

    // 채팅 에러
    CHAT_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT NOT FOUND", "채팅을 찾을 수 없습니다."),

    // 네이버 api 에러
    KEY_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"NAVER 401 ERROR", "인증 실패. 클라이언트 ID 또는 시크릿이 올바르지 않습니다."),
    API_FORBIDDEN(HttpStatus.FORBIDDEN, "NAVER 403 ERROR","API 호출 횟수를 초과했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}