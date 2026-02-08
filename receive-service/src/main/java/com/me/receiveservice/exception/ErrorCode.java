package com.me.receiveservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    /* 창고, 로케이션 관련 */
    WAREHOUSE_NOT_FOUND(HttpStatus.NOT_FOUND, "W001", "창고를 찾을 수 없습니다."),
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "L001", "로케이션을 찾을 수 없습니다."),
    LOCATION_CAPACITY_EXCEEDED(HttpStatus.CONFLICT, "L002", "로케이션 수용 용량을 초과하여 처리할 수 없습니다."),

    /* 입고 관련 */
    RECEIVE_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "입고를 찾을 수 없습니다."),
    INVALID_RECEIVE_QUANTITY(HttpStatus.BAD_REQUEST, "R002", "입고 수량이 유효하지 않습니다."),
    RECEIVE_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "R003", "이미 완료된 입고입니다."),
    RECEIVE_QTY_EXCEEDED(HttpStatus.CONFLICT, "R004", "입고 수량이 예상 수량을 초과하여 처리할 수 없습니다."),
    RECEIVE_INSPECTED_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "R005", "입고 검수 처리 권한이 없습니다."),
    RECEIVE_COMPLETED_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "R006", "입고 완료 처리 권한이 없습니다."),
    RECEIVE_INSPECTION_NOT_COMPLETED(HttpStatus.CONFLICT, "R007", "입고 검수가 완료되지 않아 입고를 완료할 수 없습니다."),
    RECEIVE_INSPECTION_NOT_CREATED(HttpStatus.CONFLICT, "R008", "입고가 생성 상태가 아니어서 검수를 진행할 수 없습니다."),
    RECEIVE_CANNOT_CANCEL_COMPLETED(HttpStatus.CONFLICT, "R009", "입고가 완료 상태이므로 취소할 수 없습니다."),

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
