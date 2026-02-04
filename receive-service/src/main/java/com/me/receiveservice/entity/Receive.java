package com.me.receiveservice.entity;

import com.me.receiveservice.exception.ErrorCode;
import com.me.receiveservice.exception.ReceiveServiceException;
import com.me.receiveservice.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    private Long skuId;
    private Long locationId;

    private int expectedQty = 0;
    private int receivedQty = 0;

    @Enumerated(value = EnumType.STRING)
    private ReceiveStatus status;

    private String inspectedBy; // 검수 담당자
    private String completedBy; // 완료 처리 담당자

    public static Receive create(Long skuId, Long locationId, int expectedQty) {
        validateQty(expectedQty);
        Receive receive = new Receive();
        receive.skuId = skuId;
        receive.locationId = locationId;
        receive.expectedQty = expectedQty;
        receive.receivedQty = 0;
        receive.status = ReceiveStatus.CREATED;
        return receive;
    }

    public void inspect(int receivedQty, String username) {
        validateReceivedQty(receivedQty);
        this.receivedQty = receivedQty;
        this.status = ReceiveStatus.INSPECTED;
        this.inspectedBy = username;
    }

    public void complete(String username) {
        this.status = ReceiveStatus.COMPLETED;
        this.completedBy = username;
    }

    public void cancel() {
        this.status = ReceiveStatus.CANCELED;
        this.expectedQty = 0;
        this.receivedQty = 0;
    }

    private static void validateQty(int qty) {
        if (qty <= 0) {
            throw new ReceiveServiceException(ErrorCode.INVALID_RECEIVE_QUANTITY);
        }
    }

    private void validateReceivedQty(int receivedQty) {
        validateQty(receivedQty);
        if (receivedQty > this.expectedQty) {
            throw new ReceiveServiceException(ErrorCode.RECEIVE_QTY_EXCEEDED);
        }
    }

    // Using test code
    public void changeStatus(ReceiveStatus status) {
        this.status = status;
    }
}
