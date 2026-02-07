package com.me.adjustmentservice.client;

import com.me.adjustmentservice.client.config.FeignClientConfig;
import com.me.adjustmentservice.client.dto.InventoryDto;
import com.me.adjustmentservice.client.dto.QuantityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service", configuration = FeignClientConfig.class)
public interface InventoryServiceClient {

    @GetMapping("/inventories/locations/{locationId}/total-quantity")
    QuantityResponse getSumQuantityByLocation(@PathVariable("locationId") Long locationId);

    @GetMapping("/inventories/skus/{skuId}/locations/{locationId}")
    InventoryDto getInventory(@PathVariable("skuId") Long skuId, @PathVariable("locationId") Long locationId);
}
