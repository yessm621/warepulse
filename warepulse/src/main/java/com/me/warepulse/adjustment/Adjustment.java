package com.me.warepulse.adjustment;

import com.me.warepulse.location.Location;
import com.me.warepulse.sku.Sku;
import com.me.warepulse.user.User;
import com.me.warepulse.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "adjustment")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Adjustment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adjustment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User operator;

    @Column(nullable = false)
    private int delta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdjustmentReason reason;

    private Long inventoryId;

    public static Adjustment create(Sku sku, Location location, User operator, int delta, AdjustmentReason reason, Long inventoryId) {
        Adjustment adjustment = new Adjustment();
        adjustment.sku = sku;
        adjustment.location = location;
        adjustment.operator = operator;
        adjustment.delta = delta;
        adjustment.reason = reason;
        adjustment.inventoryId = inventoryId;
        return adjustment;
    }
}
