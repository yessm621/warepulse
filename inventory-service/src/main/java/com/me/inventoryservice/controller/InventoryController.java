package com.me.inventoryservice.controller;

import com.me.inventoryservice.controller.response.InventoryResponse;
import com.me.inventoryservice.controller.response.QuantityResponse;
import com.me.inventoryservice.exception.ApiResponse;
import com.me.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<QuantityResponse>> totalQuantity(@PathVariable("locationId") Long locationId) {
        QuantityResponse response = inventoryService.totalQuantity(locationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/skus/{skuId}/locations/{locationId}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventory(@PathVariable("skuId") Long skuId, @PathVariable("locationId") Long locationId) {
        InventoryResponse response = inventoryService.getInventory(skuId, locationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
