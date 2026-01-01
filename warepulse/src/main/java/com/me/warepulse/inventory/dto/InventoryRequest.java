package com.me.warepulse.inventory.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {

    private Long skuId;
    private Long locationId;

    @Max(200)
    @Min(0)
    private int quantity;
}
