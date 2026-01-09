package com.me.warepulse.receiving;

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
@Table(name = "receiving")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Receiving extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receiving_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    private int expectedQty = 0;
    private int receivedQty = 0;

    @Enumerated(value = EnumType.STRING)
    private ReceivingStatus status;

    private Long inventoryId;

    private String inspectedBy; // 검수 담당자
    private String completedBy; // 완료 처리 담당자

    public static Receiving create(Sku sku, Location location, int expectedQty) {
        validateQty(expectedQty);
        Receiving receiving = new Receiving();
        receiving.sku = sku;
        receiving.location = location;
        receiving.expectedQty = expectedQty;
        receiving.receivedQty = 0;
        receiving.status = ReceivingStatus.CREATED;
        return receiving;
    }

    public void inspect(int receivedQty, String username) {
        validateInspect(receivedQty);
        this.receivedQty = receivedQty;
        this.status = ReceivingStatus.INSPECTED;
        this.inspectedBy = username;
    }

    private static void validateQty(int qty) {
        if (qty <= 0) {
            throw new WarePulseException(ErrorCode.NEGATIVE_INVENTORY_QUANTITY);
        }
    }

    private void validateInspect(int receivedQty) {
        if (receivedQty < 0) {
            throw new WarePulseException(ErrorCode.NEGATIVE_INVENTORY_QUANTITY);
        }
        if (receivedQty > this.expectedQty) {
            throw new WarePulseException(ErrorCode.RECEIVING_QTY_EXCEEDED);
        }
    }

    public void complete(String username) {
        this.status = ReceivingStatus.COMPLETED;
        this.completedBy = username;
    }
}
