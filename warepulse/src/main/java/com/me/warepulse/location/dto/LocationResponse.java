package com.me.warepulse.location.dto;

import com.me.warepulse.location.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LocationResponse {

    private Long warehouseId;
    private String warehouseName;
    private Long locationId;
    private String code;
    private int capacity;
    private LocalDateTime createdAt;

    public static LocationResponse from(Location location) {
        return new LocationResponse(
                location.getWarehouse().getId(),
                location.getWarehouse().getName(),
                location.getId(),
                location.getCode(),
                location.getCapacity(),
                location.getCreatedAt()
        );
    }
}
