package com.me.warepulse.receive.dto;

import com.me.warepulse.receive.Receive;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReceiveResponse {

    private Long receiveId;
    private Long skuId;
    private Long locationId;
    private int expectedQty;
    private int receivedQty;
    private String receiveStatus;
    private Long inventoryId;
    private String createdBy;
    private String inspectedBy;
    private String completedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReceiveResponse from(Receive receive) {
        return new ReceiveResponse(
                receive.getId(),
                receive.getSku().getId(),
                receive.getLocation().getId(),
                receive.getExpectedQty(),
                receive.getReceivedQty(),
                receive.getStatus().name(),
                receive.getInventoryId(),
                receive.getCreatedBy(),
                receive.getInspectedBy(),
                receive.getCompletedBy(),
                receive.getCreatedAt(),
                receive.getUpdatedAt()
        );
    }
}
