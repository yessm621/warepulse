package com.me.shipmentservice.client.dto;

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
}
