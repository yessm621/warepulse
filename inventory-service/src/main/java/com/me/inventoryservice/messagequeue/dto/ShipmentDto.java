package com.me.inventoryservice.messagequeue.dto;

import com.me.inventoryservice.entity.EventEnum.ShipmentReason;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentDto {

    private Long skuId;
    private Long locationId;
    private ShipmentReason reason;

    @Min(1)
    private int reservedQty;
}
