package com.me.warepulse.receiving;

import com.me.warepulse.utils.BaseEntity;
import com.me.warepulse.sku.Sku;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "receiving_line")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReceivingLine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receiving_line_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "receiving_id")
    private Receiving receiving;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sku_id")
    private Sku sku;

    private int expectedQty = 0;
    private int receivedQty = 0;

    @Enumerated(value = EnumType.STRING)
    private ReceivingStatus status;

    // todo:: 검수 담당자, 완료 처리 담당자
    private Long inspectedBy;
    private Long completedBy;
}
