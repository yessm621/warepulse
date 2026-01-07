package com.me.warepulse.inventory.service.dto;

import com.me.warepulse.inventory.entity.EventEnum.DecreaseReason;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveInventoryDto {

    private Long inventoryId;
    private Long shipmentId;
    private DecreaseReason reason;

    @Min(1)
    private int reservedQty;
}
