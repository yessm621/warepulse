package com.me.warepulse.entity;

import jakarta.persistence.*;

@Entity
public class Locations extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Column(length = 20, nullable = false)
    private String code;

    private int capacity = 0;
}
