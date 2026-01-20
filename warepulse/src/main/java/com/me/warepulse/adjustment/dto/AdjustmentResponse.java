package com.me.warepulse.adjustment.dto;

import com.me.warepulse.adjustment.Adjustment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdjustmentResponse {
    private Long adjustmentId;
    private Long skuId;
    private Long locationId;
    private String operator;
    private int delta;
    private String reason;
    private Long inventoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AdjustmentResponse from(Adjustment adjustment) {
        return new AdjustmentResponse(
                adjustment.getId(),
                adjustment.getSku().getId(),
                adjustment.getLocation().getId(),
                adjustment.getOperator().getUsername(),
                adjustment.getDelta(),
                adjustment.getReason().name(),
                adjustment.getInventoryId(),
                adjustment.getCreatedAt(),
                adjustment.getUpdatedAt()
        );
    }
}
