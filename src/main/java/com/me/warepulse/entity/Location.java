package com.me.warepulse.entity;

import com.me.warepulse.entity.base.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Location extends BaseEntity {

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
