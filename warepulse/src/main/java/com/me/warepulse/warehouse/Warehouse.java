package com.me.warepulse.warehouse;

import com.me.warepulse.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "warehouse")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Warehouse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id")
    private Long id;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 1000, nullable = false)
    private String address;

    public static Warehouse create(String name, String address) {
        Warehouse warehouse = new Warehouse();
        warehouse.name = name;
        warehouse.address = address;
        return warehouse;
    }
}
