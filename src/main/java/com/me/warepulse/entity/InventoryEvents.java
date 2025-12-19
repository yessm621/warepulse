package com.me.warepulse.entity;

import jakarta.persistence.*;

@Entity
public class InventoryEvents extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_event_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventories inventories;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Skus skus;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Locations locations;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private int quantity;

    //TODO:: payload - json 타입
}
