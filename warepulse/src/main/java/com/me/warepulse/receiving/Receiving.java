package com.me.warepulse.receiving;

import com.me.warepulse.location.Location;
import com.me.warepulse.sku.Sku;
import com.me.warepulse.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "receiving")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Receiving extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receiving_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    private int expectedQty = 0;
    private int receivedQty = 0;

    @Enumerated(value = EnumType.STRING)
    private ReceivingStatus status;

    private Long inventoryId;

    private String inspectedBy; // 검수 담당자
    private String completedBy; // 완료 처리 담당자

    public static Receiving create(Sku sku, Location location, int expectedQty) {
        Receiving receiving = new Receiving();
        receiving.sku = sku;
        receiving.location = location;
        receiving.expectedQty = expectedQty;
        receiving.receivedQty = 0;
        receiving.status = ReceivingStatus.CREATED;
        return receiving;
    }
}
