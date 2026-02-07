package com.me.adjustmentservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AdjustmentReason {
    STOCK_TAKING_SHORTAGE("실사 부족"),
    STOCK_TAKING_OVERAGE("실사 초과"),
    SYSTEM_ERROR("시스템 오류"),
    MANUAL_CORRECTION("운영자 수동 조정"),
    DAMAGE("파손"),
    EXPIRED("유통기한 만료");

    private final String name;
}
