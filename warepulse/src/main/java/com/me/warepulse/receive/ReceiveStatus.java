package com.me.warepulse.receive;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReceiveStatus {
    CREATED("생성"), INSPECTED("검수"), COMPLETED("완료");

    private final String name;
}
