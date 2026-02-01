package com.me.warepulse.messagequeue.receive;

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

    public static ReceiveDto of(Long skuId, Long locationId, ReceiveReason reason, int receivedQty) {
        return new ReceiveDto(skuId, locationId, reason, receivedQty);
    }
}
