package com.me.warepulse.receiving;

import com.me.warepulse.utils.BaseEntity;
import com.me.warepulse.inventory.Inventory;
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
    @JoinColumn(name = "inventory_id")
    private Inventory inventories;

    @Enumerated(value = EnumType.STRING)
    private ReceivingStatus status;

    // todo:: 검수 담당자, 완료 처리 담당자
    private Long inspectedBy;
    private Long completedBy;
}
