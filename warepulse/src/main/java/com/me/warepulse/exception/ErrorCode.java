package com.me.warepulse.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    /* 사용자 관련 */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "U002", "이미 사용 중인 사용자 아이디입니다."),
    INVALID_USER_ROLE(HttpStatus.FORBIDDEN, "U003", "해당 작업을 수행할 권한이 없습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "U004", "아이디 또는 비밀번호가 일치하지 않습니다."),

    /* 창고, 로케이션 관련 */
    WAREHOUSE_NOT_FOUND(HttpStatus.NOT_FOUND, "W001", "창고를 찾을 수 없습니다."),
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "L001", "로케이션을 찾을 수 없습니다."),
    LOCATION_CAPACITY_EXCEEDED(HttpStatus.CONFLICT, "L002", "로케이션 수용 용량을 초과하여 처리할 수 없습니다."),

    /* SKU 관련 */
    SKU_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "SKU를 찾을 수 없습니다."),
    DUPLICATE_SKU_CODE(HttpStatus.CONFLICT, "S002", "이미 존재하는 SKU 코드입니다."),

    /* 공통 */
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "E001", "잘못된 요청입니다."),
    ACCESS_DENIED_EXCEPTION(HttpStatus.UNAUTHORIZED, "E002", "접근 권한이 없습니다."),
    NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "E003", "데이터를 찾을 수 없습니다."),
    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST, "E004", "처리 중 오류가 발생했습니다."),
    PARAMETER_INCORRECT(HttpStatus.BAD_REQUEST, "E005", "파라미터(인자)가 잘못되었습니다."),
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
