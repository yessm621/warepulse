package com.me.warepulse.inventory.dto;

import com.me.warepulse.inventory.entity.Inventory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InventoryAvailableResponse {

    private int quantity;
    private int reservedQty;
    private int availableQty;

    public static InventoryAvailableResponse from(Inventory inventory) {
        return new InventoryAvailableResponse(
                inventory.getQuantity(),
                inventory.getReservedQty(),
                inventory.getQuantity() - inventory.getReservedQty()
        );
    }
}
