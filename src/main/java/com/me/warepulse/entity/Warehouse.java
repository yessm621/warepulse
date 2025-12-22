package com.me.warepulse.entity;

import com.me.warepulse.entity.base.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Warehouse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id")
    private Long id;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 1000, nullable = false)
    private String address;
}
