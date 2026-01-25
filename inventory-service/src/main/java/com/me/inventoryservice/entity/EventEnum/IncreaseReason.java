package com.me.inventoryservice.entity.EventEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IncreaseReason {
    PURCHASE_INBOUND("구매 입고"),
    RETURN_INBOUND("반품 입고"),
    MOVE_IN("재고 이동 입고"),
    RESERVED_CANCEL("재고 예약 해제"),
    ADJUSTMENT("재고 보정 증가");

    private final String message;
}
