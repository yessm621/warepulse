package com.me.adjustmentservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    /* 조정, 실사 관련 */
    ADJUSTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "A001", "재고 조정 내용을 찾을 수 없습니다."),
    INVALID_ADJUSTMENT(HttpStatus.BAD_REQUEST, "A002", "유효하지 않은 재고 조정입니다."),
    ADJUSTMENT_REASON_REQUIRED(HttpStatus.BAD_REQUEST, "A003", "재고 조정 사유는 필수입니다."),
    ADJUSTMENT_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "A004", "재고 조정 권한이 없습니다."),

    /* 공통 */
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "E001", "잘못된 요청입니다."),
    ACCESS_DENIED_EXCEPTION(HttpStatus.UNAUTHORIZED, "E002", "접근 권한이 없습니다."),
    NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "E003", "데이터를 찾을 수 없습니다."),
    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST, "E004", "처리 중 오류가 발생했습니다."),
    PARAMETER_INCORRECT(HttpStatus.BAD_REQUEST, "E005", "파라미터(인자)가 잘못되었습니다."),
    EXTERNAL_API_ERROR(HttpStatus.BAD_REQUEST, "E006", "FeignClient 변환 중 값이 잘못되었습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E100", "서버 에러");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
