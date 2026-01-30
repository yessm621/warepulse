package com.me.warepulse.client;

import com.me.warepulse.client.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service", configuration = FeignClientConfig.class)
public interface InventoryClient {

    @GetMapping("/inventories/locations/{locationId}/total-quantity")
    int getSumQuantityByLocation(@PathVariable("locationId") Long locationId);

    @GetMapping("/inventories/skus/{skuId}/locations/{locationId}")
    Long getInventoryId(@PathVariable("skuId") Long skuId, @PathVariable("locationId") Long locationId);
}
