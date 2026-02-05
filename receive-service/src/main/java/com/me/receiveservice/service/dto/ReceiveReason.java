package com.me.receiveservice.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReceiveReason {
    PURCHASE("재고 입고"),
    TRANSFER("재고 이동 입고"),
    RESERVED("재고 예약"),
    RESERVED_CANCEL("재고 예약 취소");

    private final String message;
}
