package com.me.warepulse.inventory.dto;

import com.me.warepulse.inventory.entity.Inventory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InventoryResponse {

    private Long inventoryId;
    private Long skuId;
    private Long locationId;
    private int quantity; // 실재고
    private int reservedQty; // 예약 수량
    private int availableQty; // 가용 재고
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InventoryResponse from(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getSku().getId(),
                inventory.getLocation().getId(),
                inventory.getQuantity(),
                inventory.getReservedQty(),
                inventory.getQuantity() - inventory.getReservedQty(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt()
        );
    }
}
