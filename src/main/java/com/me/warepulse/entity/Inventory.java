package com.me.warepulse.entity;

import com.me.warepulse.entity.base.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku skus;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location locations;

    private int quantity = 0;
    private int reserved = 0;
    private int version = 0;
}
