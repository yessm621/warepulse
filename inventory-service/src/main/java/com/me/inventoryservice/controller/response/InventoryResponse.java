package com.me.inventoryservice.controller.response;

import com.me.inventoryservice.entity.Inventory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InventoryResponse {

    private Long inventoryId;
    private int quantity;
    private int reservedQty;

    public static InventoryResponse from(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getQuantity(),
                inventory.getReservedQty()
        );
    }
}
