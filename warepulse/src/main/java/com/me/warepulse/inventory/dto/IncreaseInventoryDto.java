package com.me.warepulse.inventory.dto;

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

    @Min(1)
    private int quantity;
}
