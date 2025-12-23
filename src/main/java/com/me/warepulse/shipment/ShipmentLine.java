package com.me.warepulse.shipment;

import com.me.warepulse.utils.BaseEntity;
import com.me.warepulse.sku.Sku;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;

@Entity
@Table(name = "shipment_line")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipmentLine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_line_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    private int quantity = 0;
    private int pickedQty = 0; // 실제 픽킹된 수량

    // todo:: 검수 담당자, 완료 처리 담당자
    private Long inspectedBy;
    private Long completedBy;
}
