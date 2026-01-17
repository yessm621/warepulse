package com.me.warepulse.inventory.service.dto;

import com.me.warepulse.inventory.entity.EventEnum.IncreaseReason;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseInventoryDto {

    private Long inventoryId;
    private Long shipmentId;
    private IncreaseReason reason;
    
    @Min(1)
    private int reservedQty;

    public static ReleaseInventoryDto of(Long inventoryId, Long shipmentId, IncreaseReason reason, int reservedQty) {
        return new ReleaseInventoryDto(inventoryId, shipmentId, reason, reservedQty);
    }
}
