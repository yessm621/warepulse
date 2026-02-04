package com.me.inventoryservice.service.dto;

import com.me.inventoryservice.entity.EventEnum.AdjustmentReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentInventoryDto {

    private Long inventoryId;
    private AdjustmentReason reason;
    private int delta;

    public static AdjustmentInventoryDto of(Long inventoryId, AdjustmentReason reason, int delta) {
        return new AdjustmentInventoryDto(inventoryId, reason, delta);
    }
}
