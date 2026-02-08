package com.me.inventoryservice.messagequeue.dto;

import com.me.inventoryservice.entity.EventEnum.AdjustmentReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentDto {
    private Long inventoryId;
    private AdjustmentReason reason;
    private int delta;
}
