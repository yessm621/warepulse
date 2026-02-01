package com.me.inventoryservice.service;

import com.me.inventoryservice.controller.response.InventoryResponse;

public interface InventoryService {

    int totalQuantity(Long locationId);

    InventoryResponse getInventory(Long skuId, Long locationId);
}
