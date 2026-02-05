package com.me.shipmentservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SkuResponse {

    private Long skuId;
    private String code;
    private String name;
    private String type;
    private LocalDateTime createdAt;
}
