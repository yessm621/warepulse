package com.me.inventoryservice.service;

public interface InventoryService {

    int totalQuantity(Long locationId);

    Long getInventoryId(Long skuId, Long locationId);
}
