package com.me.warepulse.location;

import com.me.warepulse.utils.BaseEntity;
import com.me.warepulse.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "locations")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Column(unique = true, length = 20, nullable = false)
    private String code; // zone-rack-slot(ex. "A-01-03")

    private int capacity = 0; // 창고에 수용할 수 있는 물건 수
}
