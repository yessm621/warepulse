package com.me.warepulse.receive;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.location.Location;
import com.me.warepulse.sku.Sku;
import com.me.warepulse.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "receive")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Receive extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receive_id")
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
    private ReceiveStatus status;

    private Long inventoryId;

    private String inspectedBy; // 검수 담당자
    private String completedBy; // 완료 처리 담당자

    public static Receive create(Sku sku, Location location, int expectedQty) {
        validateQty(expectedQty);
        Receive receive = new Receive();
        receive.sku = sku;
        receive.location = location;
        receive.expectedQty = expectedQty;
        receive.receivedQty = 0;
        receive.status = ReceiveStatus.CREATED;
        return receive;
    }

    public void inspect(int receivedQty, String username) {
        validateInspect(receivedQty);
        this.receivedQty = receivedQty;
        this.status = ReceiveStatus.INSPECTED;
        this.inspectedBy = username;
    }

    private static void validateQty(int qty) {
        if (qty <= 0) {
            throw new WarePulseException(ErrorCode.NEGATIVE_INVENTORY_QUANTITY);
        }
    }

    private void validateInspect(int receivedQty) {
        if (receivedQty <= 0) {
            throw new WarePulseException(ErrorCode.NEGATIVE_INVENTORY_QUANTITY);
        }
        if (receivedQty > this.expectedQty) {
            throw new WarePulseException(ErrorCode.RECEIVE_QTY_EXCEEDED);
        }
    }

    public void complete(String username, Long inventoryId) {
        this.status = ReceiveStatus.COMPLETED;
        this.completedBy = username;
        this.inventoryId = inventoryId;
    }

    // 테스트 코드에서 사용
    public void changeStatus(ReceiveStatus status) {
        this.status = status;
    }
}
