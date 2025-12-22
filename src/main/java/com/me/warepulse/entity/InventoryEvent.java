package com.me.warepulse.entity;

import com.me.warepulse.entity.base.BaseEntity;
import com.me.warepulse.entity.base.InventoryEventType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "inventory_event")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_event_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventories;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sku_id")
    private Sku skus;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "location_id")
    private Location locations;

    @Enumerated(EnumType.STRING)
    private InventoryEventType type;

    private int quantity;

    //TODO:: payload, json 타입
}
