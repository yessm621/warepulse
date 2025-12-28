package com.me.warepulse.warehouse;

import com.me.warepulse.exception.ApiResponse;
import com.me.warepulse.warehouse.dto.WarehouseRequest;
import com.me.warepulse.warehouse.dto.WarehouseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping("/warehouses")
    public ResponseEntity<ApiResponse<WarehouseResponse>> createWarehouse(@RequestBody WarehouseRequest request) {
        WarehouseResponse response = warehouseService.create(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/warehouses")
    public ResponseEntity<ApiResponse<List<WarehouseResponse>>> getWarehouses() {
        List<WarehouseResponse> warehouses = warehouseService.findWarehouses();
        return ResponseEntity.ok(ApiResponse.success(warehouses));
    }

    @GetMapping("/warehouses/{warehouseId}")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getWarehouse(@PathVariable("warehouseId") Long warehouseId) {
        WarehouseResponse warehouse = warehouseService.findWarehouse(warehouseId);
        return ResponseEntity.ok(ApiResponse.success(warehouse));
    }

    @DeleteMapping("/warehouses/{warehouseId}")
    public ResponseEntity<ApiResponse> deleteWarehouse(@PathVariable("warehouseId") Long warehouseId) {
        warehouseService.deleteWarehouse(warehouseId);
        return ResponseEntity.ok(ApiResponse.successWithNoContent());
    }
}
