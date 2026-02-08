package com.me.adjustmentservice.controller.dto;

import com.me.adjustmentservice.entity.Adjustment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdjustmentResponse {
    private Long adjustmentId;
    private Long skuId;
    private Long locationId;
    //private String operator;
    private Long userId;
    private int delta;
    private String reason;
    private Long inventoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AdjustmentResponse from(Adjustment adjustment) {
        return new AdjustmentResponse(
                adjustment.getId(),
                adjustment.getSkuId(),
                adjustment.getLocationId(),
                adjustment.getUserId(),
                adjustment.getDelta(),
                adjustment.getReason().name(),
                adjustment.getInventoryId(),
                adjustment.getCreatedAt(),
                adjustment.getUpdatedAt()
        );
    }
}
