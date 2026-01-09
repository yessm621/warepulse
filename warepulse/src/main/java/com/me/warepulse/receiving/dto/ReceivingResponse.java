package com.me.warepulse.receiving.dto;

import com.me.warepulse.receiving.Receiving;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReceivingResponse {

    private Long receivingId;
    private Long skuId;
    private Long locationId;
    private int expectedQty;
    private int receivedQty;
    private String receivingStatus;
    private Long inventoryId;
    private String createdBy;
    private String inspectedBy;
    private String completedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReceivingResponse from(Receiving receiving) {
        return new ReceivingResponse(
                receiving.getId(),
                receiving.getSku().getId(),
                receiving.getLocation().getId(),
                receiving.getExpectedQty(),
                receiving.getReceivedQty(),
                receiving.getStatus().name(),
                receiving.getInventoryId(),
                receiving.getCreatedBy(),
                receiving.getInspectedBy(),
                receiving.getCompletedBy(),
                receiving.getCreatedAt(),
                receiving.getUpdatedAt()
        );
    }
}
