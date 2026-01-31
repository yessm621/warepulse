package com.me.inventoryservice.controller;

import com.me.inventoryservice.controller.response.InventoryResponse;
import com.me.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventories")
public class InventoryController {

    private final Environment env;
    private final InventoryService inventoryService;

    @GetMapping("/health-check")
    public String status() {
        return String.format("It's Working in Inventory Service on Local PORT %s (SERVER PORT %s)",
                env.getProperty("local.server.port"), env.getProperty("server.port"));
    }

    @GetMapping("/locations/{locationId}/total-quantity")
    public int totalQuantity(@PathVariable("locationId") Long locationId) {
        return inventoryService.totalQuantity(locationId);
    }

    @GetMapping("/skus/{skuId}/locations/{locationId}")
    public InventoryResponse getInventory(@PathVariable("skuId") Long skuId, @PathVariable("locationId") Long locationId) {
        return inventoryService.getInventory(skuId, locationId);
    }
}
