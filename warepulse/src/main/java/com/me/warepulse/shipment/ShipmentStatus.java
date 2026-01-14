package com.me.warepulse.shipment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ShipmentStatus {
    CREATED("생성"), PICKING("픽킹"), SHIPPED("출고"), CANCELED("취소");

    private final String name;
}
