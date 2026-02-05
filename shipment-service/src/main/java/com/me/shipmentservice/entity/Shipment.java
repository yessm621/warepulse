package com.me.shipmentservice.entity;

import com.me.shipmentservice.exception.ErrorCode;
import com.me.shipmentservice.exception.ShipmentServiceException;
import com.me.shipmentservice.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipment")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Shipment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_id")
    private Long id;

    private Long skuId;
    private Long locationId;

    private int quantity = 0;
    private int pickedQty = 0;

    @Enumerated(value = EnumType.STRING)
    private ShipmentStatus status;

    private Long inventoryId;

    private String pickedBy;
    private String shippedBy;

    public static Shipment create(Long skuId, Long locationId, int quantity, Long inventoryId) {
        validateQty(quantity);
        Shipment shipment = new Shipment();
        shipment.skuId = skuId;
        shipment.locationId = locationId;
        shipment.quantity = quantity;
        shipment.pickedQty = 0;
        shipment.status = ShipmentStatus.CREATED;
        shipment.inventoryId = inventoryId;
        return shipment;
    }

    public void picking(int pickedQty, String username) {
        validatePickingQty(pickedQty);
        this.pickedQty = pickedQty;
        this.status = ShipmentStatus.PICKING;
        this.pickedBy = username;
    }

    public void shipped(String username) {
        this.status = ShipmentStatus.SHIPPED;
        this.shippedBy = username;
    }

    public void canceled() {
        this.status = ShipmentStatus.CANCELED;
        this.quantity = 0;
        this.pickedQty = 0;
    }

    private static void validateQty(int qty) {
        if (qty <= 0) {
            throw new ShipmentServiceException(ErrorCode.INVALID_SHIPMENT_QUANTITY);
        }
    }

    private void validatePickingQty(int pickedQty) {
        validateQty(pickedQty);
        if (pickedQty > this.quantity) {
            throw new ShipmentServiceException(ErrorCode.SHIPMENT_QTY_EXCEEDED);
        }
    }

    // Using test code
    public void changeStatus(ShipmentStatus status) {
        this.status = status;
    }
}
