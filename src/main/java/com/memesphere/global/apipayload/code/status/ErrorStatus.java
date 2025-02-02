package com.memesphere.global.apipayload.code.status;

import com.memesphere.global.apipayload.code.BaseCode;
import com.memesphere.global.apipayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

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

    // ChartData load 에러
    CANNOT_LOAD_CHARTDATA(HttpStatus.BAD_REQUEST, "CANNOT LOAD CHARTDATA", "ChartData를 Binance에서 로드할 수 없습니다."),

    // 유저 로그인 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER NOT FOUND", "유저를 찾을 수 없습니다."),
    PASSWORD_NOT_MATCH(HttpStatus.NOT_FOUND, "PASSWORD NOT MATCH", "비밀번호가 틀렸습니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER ALREADY EXIST ", "이미 존재하는 회원입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "NICKNAME ALREADY EXIST ", "이미 사용 중인 닉네임입니다."),

    // 이미지 에러
    EMPTY_FILE_EXCEPTION(HttpStatus.BAD_REQUEST, "EMPTY_FILE", "이미지가 빈 파일입니다."),
    INVALID_FILE_EXTENTION(HttpStatus.BAD_REQUEST, "INVALID FILE EXTENSION", "지원되지 않는 파일 형식입니다.");

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