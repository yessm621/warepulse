package com.me.inventoryservice.service;

import com.me.inventoryservice.controller.dto.InventoryDto;

public interface InventoryService {

    int totalQuantity(Long locationId);

    InventoryDto getInventory(Long skuId, Long locationId);
}
