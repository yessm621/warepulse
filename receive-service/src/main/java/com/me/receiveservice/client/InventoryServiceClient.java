package com.me.receiveservice.client;

import com.me.receiveservice.client.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service", configuration = FeignClientConfig.class)
public interface InventoryServiceClient {

    @GetMapping("/inventories/locations/{locationId}/total-quantity")
    int getSumQuantityByLocation(@PathVariable("locationId") Long locationId);

    @GetMapping("/inventories/skus/{skuId}/locations/{locationId}")
    InventoryDto getInventory(@PathVariable("skuId") Long skuId, @PathVariable("locationId") Long locationId);
}
