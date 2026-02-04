package com.me.inventoryservice.service;

import com.me.inventoryservice.controller.response.InventoryResponse;
import com.me.inventoryservice.controller.response.QuantityResponse;

public interface InventoryService {

    QuantityResponse totalQuantity(Long locationId);

    InventoryResponse getInventory(Long skuId, Long locationId);
}
