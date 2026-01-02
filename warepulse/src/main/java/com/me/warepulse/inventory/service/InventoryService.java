package com.me.warepulse.inventory.service;

import com.me.warepulse.inventory.dto.InventoryAvailableResponse;
import com.me.warepulse.inventory.dto.InventoryRequest;
import com.me.warepulse.inventory.dto.InventoryResponse;
import com.me.warepulse.inventory.dto.SkuInventoryResponse;

import java.util.List;

public interface InventoryService {

    InventoryResponse getInventory(Long skuId, Long locationId);

    SkuInventoryResponse getInventoriesBySku(Long skuId);

    List<InventoryResponse> getInventoriesByLocation(Long locationId);

    InventoryAvailableResponse inventoryAvailable(Long skuId, Long locationId); //가용 재고 조회

    InventoryResponse createInventory(InventoryRequest request); // 재고 초기화
}
