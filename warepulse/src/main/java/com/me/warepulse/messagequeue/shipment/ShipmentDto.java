package com.me.warepulse.messagequeue.shipment;

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

    public static ShipmentDto of(Long skuId, Long locationId, ShipmentReason reason, int reservedQty) {
        return new ShipmentDto(skuId, locationId, reason, reservedQty);
    }
}
