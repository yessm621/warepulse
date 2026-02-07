package com.me.shipmentservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    /* 출고 관련 */
    SHIPMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "SH001", "출고를 찾을 수 없습니다."),
    INVALID_SHIPMENT_QUANTITY(HttpStatus.BAD_REQUEST, "SH002", "요청한 출고 수량이 유효하지 않습니다."),
    SHIPMENT_ALREADY_SHIPPED(HttpStatus.CONFLICT, "SH003", "이미 출고 처리된 재고이므로 취소 처리할 수 없습니다."),
    SHIPMENT_QTY_EXCEEDED(HttpStatus.CONFLICT, "SH004", "피킹 수량이 예상 수량을 초과하여 처리할 수 없습니다."),
    SHIPMENT_INSPECTED_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "SH005", "출고 검수 처리 권한이 없습니다."),
    SHIPMENT_COMPLETED_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "SH006", "출고 완료 처리 권한이 없습니다."),
    SHIPMENT_INSPECTION_NOT_COMPLETED(HttpStatus.CONFLICT, "SH007", "피킹이 완료되지 않아 출고를 완료할 수 없습니다."),
    SHIPMENT_NO_AVAILABLE_STOCK(HttpStatus.NOT_FOUND, "SH008", "입고된 재고가 없어 출고할 수 없습니다."),
    SHIPMENT_CREATED_QTY_EXCEEDED(HttpStatus.CONFLICT, "SH009", "출고 지시 수량이 재고 수량을 초과하여 처리할 수 없습니다."),
    SHIPMENT_INSPECTION_INVALID_STATUS_CREATED(HttpStatus.CONFLICT, "SH010", "출고 상태가 CREATED가 아니어서 피킹을 진행할 수 없습니다."),

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
