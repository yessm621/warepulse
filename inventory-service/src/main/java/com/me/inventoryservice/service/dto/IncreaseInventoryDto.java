package com.me.inventoryservice.service.dto;

import com.me.inventoryservice.entity.EventEnum.IncreaseReason;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IncreaseInventoryDto {

    private Long inventoryId;
    private Long receiveId;
    private IncreaseReason reason;

    @Min(1)
    private int quantity;

    public static IncreaseInventoryDto of(Long inventoryId, Long receiveId, IncreaseReason reason, int quantity) {
        return new IncreaseInventoryDto(inventoryId, receiveId, reason, quantity);
    }
}
