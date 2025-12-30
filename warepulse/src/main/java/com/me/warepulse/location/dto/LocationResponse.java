package com.me.warepulse.location.dto;

import com.me.warepulse.location.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LocationResponse {

    private Long locationId;
    //todo:: warehouseId 추가
    private String warehouseName;
    private String code;
    private int capacity;
    private LocalDateTime createdAt;

    public static LocationResponse from(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getWarehouse().getName(),
                location.getCode(),
                location.getCapacity(),
                location.getCreatedAt()
        );
    }
}
