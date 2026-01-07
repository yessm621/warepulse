package com.me.warepulse.inventory.service.dto;

import com.me.warepulse.inventory.entity.EventEnum.IncreaseReason;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IncreaseInventoryDto {

    private Long inventoryId;
    private Long receivingId;
    private IncreaseReason reason;

    @Min(1)
    private int quantity;
}
