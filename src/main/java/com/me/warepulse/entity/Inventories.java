package com.me.warepulse.entity;

import jakarta.persistence.*;

@Entity
public class Inventories extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Skus skus;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Locations locations;

    private int quantity = 0;
    private int reserved = 0;
    private int version = 0;
}
