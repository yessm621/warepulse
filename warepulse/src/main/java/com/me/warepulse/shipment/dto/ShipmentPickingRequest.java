package com.me.warepulse.shipment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentPickingRequest {
    private int pickedQty;
}
