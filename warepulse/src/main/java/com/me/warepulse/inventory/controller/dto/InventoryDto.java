package com.me.warepulse.inventory.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {

    private Long skuId;
    private Long locationId;

    @Max(200)
    @Min(0)
    private int quantity;

    public static InventoryDto of(Long skuId, Long locationId, int quantity) {
        return new InventoryDto(skuId, locationId, quantity);
    }
}
