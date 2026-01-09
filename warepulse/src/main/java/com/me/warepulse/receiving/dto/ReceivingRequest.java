package com.me.warepulse.receiving.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReceivingRequest {

    private Long locationId;
    private Long skuId;
    private int expectedQty;
}
