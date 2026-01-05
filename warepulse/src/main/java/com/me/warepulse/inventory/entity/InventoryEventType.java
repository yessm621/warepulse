package com.me.warepulse.inventory.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InventoryEventType {
    INCREASE("수량 증가"),
    DECREASE("수량 감소"),
    MOVED("위치 이동"),
    ADJUSTED("재고 검수"),
    RESERVE("재고 예약"),
    RELEASE("예약 취소");

    private final String name;
}
