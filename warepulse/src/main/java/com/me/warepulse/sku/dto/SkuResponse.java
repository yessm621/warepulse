package com.me.warepulse.sku.dto;

import com.me.warepulse.sku.Sku;
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

    public static SkuResponse from(Sku sku) {
        return new SkuResponse(
                sku.getId(),
                sku.getCode(),
                sku.getName(),
                sku.getType().name(),
                sku.getCreatedAt()
        );
    }
}
