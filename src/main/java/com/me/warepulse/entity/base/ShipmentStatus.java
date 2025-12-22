package com.me.warepulse.entity.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ShipmentStatus {
    PICKING("픽킹"), SHIPPED("출고"), CANCELED("취소");

    private final String name;
}
