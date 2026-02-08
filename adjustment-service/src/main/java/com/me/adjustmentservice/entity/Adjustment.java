package com.me.adjustmentservice.entity;

import com.me.adjustmentservice.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    private Long skuId;
    private Long locationId;
    private Long userId;

    @Column(nullable = false)
    private int delta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdjustmentReason reason;

    private Long inventoryId;

    public static Adjustment create(Long skuId, Long locationId, Long userId, int delta, AdjustmentReason reason, Long inventoryId) {
        Adjustment adjustment = new Adjustment();
        adjustment.skuId = skuId;
        adjustment.locationId = locationId;
        adjustment.userId = userId;
        adjustment.delta = delta;
        adjustment.reason = reason;
        adjustment.inventoryId = inventoryId;
        return adjustment;
    }
}
