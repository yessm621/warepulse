package com.me.warepulse.shipment;

import com.me.warepulse.utils.BaseEntity;
import com.me.warepulse.sku.Sku;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "shipment")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shipment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @Enumerated(value = EnumType.STRING)
    private ShipmentStatus status;

    // todo:: 검수 담당자, 완료 처리 담당자
    private Long inspectedBy;
    private Long completedBy;
}
