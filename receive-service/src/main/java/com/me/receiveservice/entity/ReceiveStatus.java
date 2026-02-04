package com.me.receiveservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReceiveStatus {
    CREATED("생성"), INSPECTED("검수"), COMPLETED("완료"), CANCELED("취소");

    private final String name;
}
