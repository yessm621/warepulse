package com.me.warepulse.receive.messagequeue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReceiveDto {
    private Long skuId;
    private Long locationId;
    private Long receiveId;
    private int receivedQty;

    public static InventoryReceiveDto of(Long skuId, Long locationId, Long receiveId, int receivedQty) {
        return new InventoryReceiveDto(skuId, locationId, receiveId, receivedQty);
    }
}
