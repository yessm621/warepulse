package com.me.warepulse.shipment;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.location.Location;
import com.me.warepulse.sku.Sku;
import com.me.warepulse.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    private int quantity = 0;
    private int pickedQty = 0;

    @Enumerated(value = EnumType.STRING)
    private ShipmentStatus status;

    private Long inventoryId;

    private String pickedBy;
    private String shippedBy;

    public static Shipment create(Sku sku, Location location, int quantity) {
        validateQty(quantity);
        Shipment shipment = new Shipment();
        shipment.sku = sku;
        shipment.location = location;
        shipment.quantity = quantity;
        shipment.pickedQty = 0;
        shipment.status = ShipmentStatus.CREATED;
        return shipment;
    }

    public void picking(int pickedQty, String username) {
        validatePickingQty(pickedQty);
        this.pickedQty = pickedQty;
        this.status = ShipmentStatus.PICKING;
        this.pickedBy = username;
    }

    public void shipped(Long inventoryId, String username) {
        this.status = ShipmentStatus.SHIPPED;
        this.inventoryId = inventoryId;
        this.shippedBy = username;
    }

    public void canceled() {
        this.status = ShipmentStatus.CANCELED;
        this.quantity = 0;
        this.pickedQty = 0;
    }

    private static void validateQty(int qty) {
        if (qty <= 0) {
            throw new WarePulseException(ErrorCode.INVALID_SHIPMENT_QUANTITY);
        }
    }

    private void validatePickingQty(int pickedQty) {
        validateQty(pickedQty);
        if (pickedQty > this.quantity) {
            throw new WarePulseException(ErrorCode.PICKING_QTY_EXCEEDED);
        }
    }

    // 테스트 코드에서 사용
    public void changeStatus(ShipmentStatus status) {
        this.status = status;
    }
}
