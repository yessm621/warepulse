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

    /* 재고 관련 */
    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "I001", "재고를 찾을 수 없습니다."),
    INVENTORY_CONCURRENT_MODIFICATION(HttpStatus.CONFLICT, "I002", "재고가 다른 요청에 의해 변경되었습니다. 다시 시도해주세요."),
    DUPLICATE_INVENTORY(HttpStatus.CONFLICT, "I003", "해당 SKU와 Location에 대한 재고가 이미 존재합니다."),
    DUPLICATE_INVENTORY_REQUEST(HttpStatus.CONFLICT, "I011", "이미 처리된 재고 요청입니다."),

    /* 재고 수량 관련 */
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "I004", "요청한 재고 수량이 유효하지 않습니다."),
    NEGATIVE_INVENTORY_QUANTITY(HttpStatus.BAD_REQUEST, "I005", "재고 수량은 0 이상이어야 합니다."),
    INSUFFICIENT_INVENTORY_QUANTITY(HttpStatus.CONFLICT, "I006", "재고 수량이 부족하여 처리할 수 없습니다."),
    INSUFFICIENT_INVENTORY_AVAILABLE(HttpStatus.CONFLICT, "I007", "가용 재고가 부족하여 처리할 수 없습니다."),

    /* 예약 관련 */
    RESERVED_INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "I008", "예약된 재고가 존재하지 않습니다."),
    INSUFFICIENT_RESERVED_QUANTITY(HttpStatus.CONFLICT, "I009", "예약된 재고 수량이 부족합니다."),

    /* 예약 해제 관련 */
    INSUFFICIENT_RELEASE_QUANTITY(HttpStatus.CONFLICT, "I010", "해제할 예약 재고 수량이 부족합니다."),

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
