package com.me.warepulse.inventory.entity;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.location.Location;
import com.me.warepulse.sku.Sku;
import com.me.warepulse.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(
        name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_inventory_sku_locations", columnNames = {"sku_Id", "location_Id"})
        })
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    private int quantity = 0; // location 위치에 물리적으로 존재하는 총 수량
    private int reservedQty = 0; // 픽킹 등으로 예약된 수량 (실제 가용 수량 = quantity - reserved)

    @Version
    private int version = 0; // todo:: 낙관적 락, optimistic lock

    // todo:: quantity에 대한 검증
    public static Inventory create(Sku sku, Location location, int quantity) {
        Inventory inventory = new Inventory();
        inventory.sku = sku;
        inventory.location = location;
        inventory.quantity = quantity;
        inventory.reservedQty = 0;
        inventory.version = 0;
        return inventory;
    }

    public void increase(int qty) {
        validateQty(qty);
        this.quantity += qty;
    }

    public void decrease(int qty) {
        validateQty(qty);
        this.quantity -= qty;
    }

    private static void validateQty(int qty) {
        if (qty <= 0) {
            throw new WarePulseException(ErrorCode.INVALID_QUANTITY);
        }
    }
}
