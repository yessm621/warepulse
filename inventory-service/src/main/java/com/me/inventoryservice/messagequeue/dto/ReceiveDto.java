package com.me.inventoryservice.messagequeue.dto;

import com.me.inventoryservice.entity.EventEnum.ReceiveReason;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveDto {
    private Long skuId;
    private Long locationId;
    private ReceiveReason reason;

    @Min(1)
    private int receivedQty;
}
