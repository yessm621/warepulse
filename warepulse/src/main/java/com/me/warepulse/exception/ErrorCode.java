package com.me.warepulse.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    /* 사용자 관련 */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_USER_NAME(HttpStatus.CONFLICT, "U002", "이미 존재하는 사용자 아이디입니다."),
    INVALID_USER_ROLE(HttpStatus.FORBIDDEN, "U003", "해당 작업을 수행할 권한이 없습니다."),
    INVALID_CREDENTIALS(HttpStatus.CONFLICT, "U004", "사용자 아이디 또는 비밀번호가 일치하지 않습니다."),
    /* 창고, 로케이션 관련 */
    WAREHOUSE_NOT_FOUND(HttpStatus.NOT_FOUND, "W001", "창고를 찾을 수 없습니다."),
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "L001", "로케이션을 찾을 수 없습니다."),
    LOCATION_CAPACITY_EXCEEDED(HttpStatus.CONFLICT, "L002", "로케이션에 수용할 수 있는 물건 수를 초과했습니다."),
    INVALID_LOCATION(HttpStatus.BAD_REQUEST, "L003", "유효하지 않은 로케이션입니다."),
    /* SKU 관련 */
    SKU_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "SKU를 찾을 수 없습니다."),
    DUPLICATE_SKU_CODE(HttpStatus.CONFLICT, "S002", "이미 존재하는 SKU 코드입니다."),
    INVALID_SKU_TYPE(HttpStatus.BAD_REQUEST, "S003", "유효하지 않은 SKU 타입입니다."),
    /* 재고 관련 */
    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "I001", "재고가 존재하지 않습니다."),
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "I002", "가용 재고가 부족합니다."),
    INSUFFICIENT_RESERVED_STOCK(HttpStatus.CONFLICT, "I003", "예약된 재고가 부족합니다."),
    NEGATIVE_INVENTORY_NOT_ALLOWED(HttpStatus.CONFLICT, "I004", "재고는 음수가 될 수 없습니다."),
    INVENTORY_CONCURRENT_MODIFICATION(HttpStatus.CONFLICT, "I005", "재고가 동시에 수정되었습니다."),
    INVALID_INVENTORY_TYPE(HttpStatus.BAD_REQUEST, "I006", "유효하지 않은 재고 타입입니다."),
    DUPLICATE_INVENTORY(HttpStatus.CONFLICT, "I007", "이미 존재하는 재고입니다."),
    INVALID_QUANTITY(HttpStatus.CONFLICT, "I008", "잘못된 재고 수량입니다."),
    NEGATIVE_INVENTORY_QUANTITY(HttpStatus.CONFLICT, "I008", "재고 수량은 음수일 수 없습니다."),
    INSUFFICIENT_INVENTORY_QUANTITY(HttpStatus.CONFLICT, "I009", "재고 수량이 부족합니다."),
    INSUFFICIENT_AVAILABLE_INVENTORY(HttpStatus.CONFLICT, "I010", "가용 재고가 부족합니다."),
    NO_RESERVED_QUANTITY(HttpStatus.CONFLICT, "I011", "예약된 재고가 없습니다."),
    INSUFFICIENT_RELEASE_QUANTITY(HttpStatus.CONFLICT, "I003", "예약 해제할 재고가 부족합니다."),
    // todo:: 예약, 예약 해제 관련 에러 따로 정리
    /* 입고 관련 */
    RECEIVING_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "입고 요청을 찾을 수 없습니다."),
    INVALID_RECEIVING_STATUS(HttpStatus.CONFLICT, "R002", "입고 상태가 올바르지 않습니다."),
    RECEIVING_ALREADY_COMPLETED(HttpStatus.CONFLICT, "R003", "이미 완료된 입고입니다."),
    RECEIVING_QTY_EXCEEDED(HttpStatus.CONFLICT, "R004", "입고 수량이 예상 수량을 초과했습니다."),
    RECEIVING_INSPECTED_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "R005", "입고 검수 담당자의 권한이 없습니다."),
    RECEIVING_COMPLETED_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "R006", "완료 처리 담당자의 권한이 없습니다."),
    RECEIVING_INSPECTION_NOT_COMPLETED(HttpStatus.CONFLICT, "R007", "검수하기 전에 완료 처리를 할 수 없습니다."),
    /* 출고 관련 */
    SHIPMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "SH001", "출고 정보를 찾을 수 없습니다."),
    INVALID_SHIPMENT_STATUS(HttpStatus.CONFLICT, "SH002", "출고 상태가 올바르지 않습니다."),
    SHIPMENT_ALREADY_SHIPPED(HttpStatus.CONFLICT, "SH003", "이미 출고 완료된 상태입니다."),
    PICKING_QTY_EXCEEDED(HttpStatus.CONFLICT, "SH004", "피킹 수량이 요청 수량을 초과했습니다."),
    SHIPMENT_INSPECTED_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "SH005", "입고 검수 담당자의 권한이 없습니다."),
    SHIPMENT_COMPLETED_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "SH006", "완료 처리 담당자의 권한이 없습니다."),
    SHIPMENT_INSPECTION_NOT_COMPLETED(HttpStatus.CONFLICT, "SH007", "검수하기 전에 완료 처리를 할 수 없습니다."),
    /* 조정, 실사 관련 */
    INVALID_ADJUSTMENT(HttpStatus.BAD_REQUEST, "A001", "유효하지 않은 재고 조정입니다."),
    ADJUSTMENT_REASON_REQUIRED(HttpStatus.BAD_REQUEST, "A002", "재고 조정 사유는 필수입니다."),
    ADJUSTMENT_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "A003", "재고 조정 권한이 없습니다."),
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
