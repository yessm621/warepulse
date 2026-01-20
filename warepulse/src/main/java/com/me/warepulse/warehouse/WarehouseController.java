package com.me.warepulse.warehouse;

import com.me.warepulse.exception.ApiResponse;
import com.me.warepulse.location.LocationService;
import com.me.warepulse.location.dto.LocationResponse;
import com.me.warepulse.warehouse.dto.WarehouseRequest;
import com.me.warepulse.warehouse.dto.WarehouseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final LocationService locationService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseResponse>> createWarehouse(@RequestBody WarehouseRequest request) {
        WarehouseResponse response = warehouseService.create(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WarehouseResponse>>> getWarehouses() {
        List<WarehouseResponse> warehouses = warehouseService.findWarehouses();
        return ResponseEntity.ok(ApiResponse.success(warehouses));
    }

    @GetMapping("/{warehouseId}")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getWarehouse(@PathVariable("warehouseId") Long warehouseId) {
        WarehouseResponse warehouse = warehouseService.findWarehouse(warehouseId);
        return ResponseEntity.ok(ApiResponse.success(warehouse));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{warehouseId}")
    public ResponseEntity<ApiResponse> deleteWarehouse(@PathVariable("warehouseId") Long warehouseId) {
        warehouseService.deleteWarehouse(warehouseId);
        return ResponseEntity.ok(ApiResponse.successWithNoContent());
    }

    @GetMapping("/{warehouseId}/locations")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> findLocationByWarehouseId(
            @PathVariable("warehouseId") Long warehouseId) {
        List<LocationResponse> locations = locationService.findLocationByWarehouseId(warehouseId);
        return ResponseEntity.ok(ApiResponse.success(locations));
    }
}
