package com.me.warepulse.entity;

import com.me.warepulse.entity.base.BaseEntity;
import com.me.warepulse.entity.base.UnitType;
import jakarta.persistence.*;

@Entity
public class Sku extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sku_id")
    private Long id;

    @Column(unique = true, length = 100, nullable = false)
    private String skuCode;

    @Column(length = 500, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private UnitType unit;
}
