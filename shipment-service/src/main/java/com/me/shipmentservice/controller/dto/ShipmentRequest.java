package com.me.shipmentservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentRequest {

    private Long locationId;
    private Long skuId;
    private int quantity;
}
