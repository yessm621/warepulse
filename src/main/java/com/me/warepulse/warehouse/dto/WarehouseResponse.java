package com.me.warepulse.warehouse.dto;

import com.me.warepulse.warehouse.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WarehouseResponse {

    private Long warehouseId;
    private String name;
    private String address;

    public static WarehouseResponse from(Warehouse warehouse) {
        return new WarehouseResponse(warehouse.getId(), warehouse.getName(), warehouse.getAddress());
    }
}
