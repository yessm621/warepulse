package com.me.warepulse.inventory.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InventoryEventType {
    INCREASE("증가"),
    DECREASE("감소"),
    MOVED("이동"),
    ADJUSTED("검수"),
    RESERVE("예약"),
    RELEASE("예약취소");

    private final String name;
}
