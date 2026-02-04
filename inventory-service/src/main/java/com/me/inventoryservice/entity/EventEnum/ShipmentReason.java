package com.me.inventoryservice.entity.EventEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ShipmentReason {
    SHIP_OUT("고객 주문 출고"),
    INTERNAL_MOVE("창고 간 이동 출고"),
    RESERVED("재고 예약"),
    RESERVED_CANCEL("재고 예약 취소");

    private final String message;
}
