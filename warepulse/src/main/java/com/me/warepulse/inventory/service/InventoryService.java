package com.me.warepulse.inventory.service;

import com.me.warepulse.inventory.controller.dto.InventoryAvailableResponse;
import com.me.warepulse.inventory.controller.dto.InventoryDto;
import com.me.warepulse.inventory.controller.dto.InventoryResponse;
import com.me.warepulse.inventory.controller.dto.SkuInventoryResponse;

import java.util.List;

public interface InventoryService {

    InventoryResponse getInventory(Long skuId, Long locationId);

    SkuInventoryResponse getInventoriesBySku(Long skuId);

    List<InventoryResponse> getInventoriesByLocation(Long locationId);

    InventoryAvailableResponse inventoryAvailable(Long skuId, Long locationId); //가용 재고 조회

    Long createInventory(InventoryDto dto); // 재고 초기화
}
