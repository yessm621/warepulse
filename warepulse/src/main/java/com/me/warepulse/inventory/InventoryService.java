package com.me.warepulse.inventory;

import com.me.warepulse.inventory.dto.SkuInventoryResponse;
import com.me.warepulse.inventory.dto.InventoryResponse;

import java.util.List;

public interface InventoryService {

    // 1. 조회
    InventoryResponse findInventoryBySkuIdAndLocationId(Long skuId, Long locationId);

    SkuInventoryResponse findInventoriesBySkuId(Long skuId);

    List<InventoryResponse> findInventoriesByLocationId(Long locationId);

    int inventoryAvailable(Long skuId, Long locationId); //가용 재고 조회

    /* todo:: // 2. 생성
    InventoryResponse createInventory(InventoryRequest request);

    InventoryEventResponse createInventoryEvent();*/
}
