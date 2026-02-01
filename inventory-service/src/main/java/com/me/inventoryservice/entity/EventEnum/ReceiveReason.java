package com.me.inventoryservice.entity.EventEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReceiveReason {
    PURCHASE_INBOUND("재고 입고"),
    MOVE_INBOUND("재고 이동 입고"),
    RESERVED("재고 예약"),
    RESERVED_CANCEL("재고 예약 취소");

    private final String message;
}
