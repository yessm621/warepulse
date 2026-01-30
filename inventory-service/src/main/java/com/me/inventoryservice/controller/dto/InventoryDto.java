package com.me.inventoryservice.controller.dto;

import com.me.inventoryservice.entity.Inventory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InventoryDto {

    private Long inventoryId;
    private int quantity;
    private int reservedQty;

    public static InventoryDto from(Inventory inventory) {
        return new InventoryDto(
                inventory.getId(),
                inventory.getQuantity(),
                inventory.getReservedQty()
        );
    }
}
