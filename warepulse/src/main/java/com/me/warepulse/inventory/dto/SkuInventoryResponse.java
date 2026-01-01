package com.me.warepulse.inventory.dto;

import com.me.warepulse.inventory.entity.Inventory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SkuInventoryResponse {

    private Long skuId;
    private int totalQty;
    private List<InventoryResponse> locations;

    public static SkuInventoryResponse from(Long skuId, List<Inventory> inventories, int totalQty) {
        return new SkuInventoryResponse(
                skuId,
                totalQty,
                inventories.stream()
                        .map(InventoryResponse::from)
                        .toList()
        );
    }
}
