package com.me.warepulse.adjustment.dto;

import com.me.warepulse.adjustment.AdjustmentReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentRequest {
    private Long skuId;
    private Long locationId;
    private Long operatorId;
    private int delta;
    private AdjustmentReason reason;
}
