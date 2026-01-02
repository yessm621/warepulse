package com.me.warepulse.inventory.controller;

import com.me.warepulse.exception.ApiResponse;
import com.me.warepulse.inventory.dto.InventoryAvailableResponse;
import com.me.warepulse.inventory.dto.InventoryRequest;
import com.me.warepulse.inventory.dto.InventoryResponse;
import com.me.warepulse.inventory.dto.SkuInventoryResponse;
import com.me.warepulse.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/inventories/sku/{skuId}/location/{locationId}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventory(@PathVariable("skuId") Long skuId,
                                                                       @PathVariable("locationId") Long locationId) {
        InventoryResponse inventory = inventoryService.getInventory(skuId, locationId);
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }

    @GetMapping("/inventories/sku/{skuId}")
    public ResponseEntity<ApiResponse<SkuInventoryResponse>> getInventoriesBySku(@PathVariable("skuId") Long skuId) {
        SkuInventoryResponse inventories = inventoryService.getInventoriesBySku(skuId);
        return ResponseEntity.ok(ApiResponse.success(inventories));
    }

    @GetMapping("/inventories/location/{locationId}")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventoriesByInventory(@PathVariable("locationId") Long locationId) {
        List<InventoryResponse> inventories = inventoryService.getInventoriesByLocation(locationId);
        return ResponseEntity.ok(ApiResponse.success(inventories));
    }

    @GetMapping("/skus/{skuId}/locations/{locationId}/availability")
    public ResponseEntity<ApiResponse<InventoryAvailableResponse>> getAvailableInventory(@PathVariable("skuId") Long skuId,
                                                                                         @PathVariable("locationId") Long locationId) {
        InventoryAvailableResponse inventory = inventoryService.inventoryAvailable(skuId, locationId);
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }

    @PostMapping("/inventories")
    public ResponseEntity<ApiResponse<InventoryResponse>> createInventory(@Valid @RequestBody InventoryRequest request) {
        InventoryResponse inventory = inventoryService.createInventory(request);
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }
}
