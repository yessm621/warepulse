package com.me.inventoryservice.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuantityResponse {

    private int quantity;

    public static QuantityResponse from(int quantity) {
        return new QuantityResponse(quantity);
    }
}
