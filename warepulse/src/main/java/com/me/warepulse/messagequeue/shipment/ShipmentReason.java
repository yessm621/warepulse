package com.me.warepulse.messagequeue.shipment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ShipmentReason {
    SHIP_OUT("재고 출고"),
    MOVE_OUT("재고 이동 출고"),
    RESERVED("재고 예약"),
    RESERVED_CANCEL("재고 예약 취소");

    private final String message;
}
