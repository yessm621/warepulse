package com.me.warepulse.shipment.dto;

import com.me.warepulse.shipment.Shipment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ShipmentResponse {

    private Long shipmentId;
    private Long skuId;
    private Long locationId;
    private int quantity;
    private int pickedQty;
    private String shipmentStatus;
    private Long inventoryId;
    private String createdBy;
    private String pickedBy;
    private String shippedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ShipmentResponse from(Shipment shipment) {
        return new ShipmentResponse(
                shipment.getId(),
                shipment.getSku().getId(),
                shipment.getLocation().getId(),
                shipment.getQuantity(),
                shipment.getPickedQty(),
                shipment.getStatus().name(),
                shipment.getInventoryId(),
                shipment.getCreatedBy(),
                shipment.getPickedBy(),
                shipment.getShippedBy(),
                shipment.getCreatedAt(),
                shipment.getUpdatedAt()
        );
    }
}
