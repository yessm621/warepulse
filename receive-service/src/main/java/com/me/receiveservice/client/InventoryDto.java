package com.me.receiveservice.client;

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
