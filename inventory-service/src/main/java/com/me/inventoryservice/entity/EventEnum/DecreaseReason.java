package com.me.inventoryservice.entity.EventEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DecreaseReason {
    SHIP_OUT("출고"),
    DISPOSE("폐기"),
    MOVE_OUT("재고 이동 출고"),
    RESERVED("재고 예약"),
    ADJUSTMENT("재고 보정 감소");

    private final String message;
}
