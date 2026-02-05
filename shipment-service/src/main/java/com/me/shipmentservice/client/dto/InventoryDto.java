package com.me.shipmentservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDto {

    private Long inventoryId;
    private int quantity;
    private int reservedQty;
}
