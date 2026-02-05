package com.me.receiveservice.controller.dto;

import com.me.receiveservice.entity.Receive;
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
    private String createdBy;
    private String inspectedBy;
    private String completedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReceiveResponse from(Receive receive) {
        return new ReceiveResponse(
                receive.getId(),
                receive.getSkuId(),
                receive.getLocationId(),
                receive.getExpectedQty(),
                receive.getReceivedQty(),
                receive.getStatus().name(),
                receive.getCreatedBy(),
                receive.getInspectedBy(),
                receive.getCompletedBy(),
                receive.getCreatedAt(),
                receive.getUpdatedAt()
        );
    }
}
