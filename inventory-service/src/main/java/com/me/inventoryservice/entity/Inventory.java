package com.me.inventoryservice.entity;

import com.me.inventoryservice.exception.ErrorCode;
import com.me.inventoryservice.exception.InventoryServiceException;
import com.me.inventoryservice.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_inventory_sku_locations", columnNames = {"sku_Id", "location_Id"})
        })
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    private Long skuId;

    private Long locationId;

    private int quantity = 0; // location 위치에 물리적으로 존재하는 총 수량
    private int reservedQty = 0; // 픽킹 등으로 예약된 수량 (실제 가용 수량 = quantity - reserved)

    @Version
    private int version = 0;

    public static Inventory create(Long skuId, Long locationId, int quantity) {
        validateQty(quantity);
        Inventory inventory = new Inventory();
        inventory.skuId = skuId;
        inventory.locationId = locationId;
        inventory.quantity = 0;
        inventory.reservedQty = 0;
        inventory.version = 0;
        return inventory;
    }

    public void increase(int qty) {
        validateQty(qty);
        this.quantity += qty;
        this.reservedQty = 0;
    }

    public void decrease(int qty) {
        validateQty(qty);
        this.quantity -= qty;
        this.reservedQty = 0;
    }

    public void reserve(int reservedQty) {
        validateQty(reservedQty);
        int availableQty = this.quantity - this.reservedQty;
        if (reservedQty > availableQty) {
            throw new InventoryServiceException(ErrorCode.INSUFFICIENT_RESERVED_QUANTITY);
        }
        this.reservedQty += reservedQty;
    }

    public void release(int reservedQty) {
        validateQty(reservedQty);
        if (reservedQty > this.reservedQty) {
            throw new InventoryServiceException(ErrorCode.INSUFFICIENT_RELEASE_QUANTITY);
        }
        this.reservedQty -= reservedQty;
    }

    private static void validateQty(int qty) {
        if (qty <= 0) {
            throw new InventoryServiceException(ErrorCode.NEGATIVE_INVENTORY_QUANTITY);
        }
    }

    public void adjustment(int delta) {
        this.quantity += delta;
    }
}
