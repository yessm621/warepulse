package com.me.warepulse.entity;

import com.me.warepulse.entity.base.BaseEntity;
import com.me.warepulse.entity.base.EventType;
import jakarta.persistence.*;

@Entity
public class InventoryEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_event_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventories;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku skus;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location locations;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private int quantity;

    //TODO:: payload - json 타입
}
