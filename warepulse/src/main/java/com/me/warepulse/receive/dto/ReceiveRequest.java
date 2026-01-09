package com.me.warepulse.receive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveRequest {

    private Long locationId;
    private Long skuId;
    private int expectedQty;
}
