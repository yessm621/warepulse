package com.me.warepulse.warehouse;

import com.me.warepulse.warehouse.dto.WarehouseRequest;
import com.me.warepulse.warehouse.dto.WarehouseResponse;

import java.util.List;

public interface WarehouseService {

    WarehouseResponse create(WarehouseRequest request);

    List<WarehouseResponse> findWarehouses();

    WarehouseResponse findWarehouse(Long warehouseId);

    void deleteWarehouse(Long warehouseId);
}
