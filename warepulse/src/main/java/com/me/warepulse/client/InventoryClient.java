package com.me.warepulse.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/inventories/locations/{locationId}/total-quantity")
    int getSumQuantityByLocation(@PathVariable("locationId") Long locationId);
}
