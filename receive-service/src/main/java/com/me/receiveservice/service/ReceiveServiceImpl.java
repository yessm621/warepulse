package com.me.receiveservice.service;

import com.me.receiveservice.client.InventoryServiceClient;
import com.me.receiveservice.client.WarepulseClient;
import com.me.receiveservice.dto.ReceiveRequest;
import com.me.receiveservice.dto.ReceiveResponse;
import com.me.receiveservice.entity.Receive;
import com.me.receiveservice.entity.ReceiveStatus;
import com.me.receiveservice.exception.ErrorCode;
import com.me.receiveservice.exception.ReceiveServiceException;
import com.me.receiveservice.messagequeue.KafkaProducer;
import com.me.receiveservice.messagequeue.receive.ReceiveDto;
import com.me.receiveservice.messagequeue.receive.ReceiveReason;
import com.me.receiveservice.repository.ReceiveRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class ReceiveServiceImpl implements ReceiveService {

    private static final String INVENTORY_TOPIC = "inventory-receive-topic";

    private final ReceiveRepository receiveRepository;
    private final WarepulseClient warepulseClient;
    private final InventoryServiceClient inventoryServiceClient;
    private final KafkaProducer kafkaProducer;

    @Transactional(readOnly = true)
    @Override
    public List<ReceiveResponse> findReceives() {
        return receiveRepository.findAll()
                .stream()
                .map(ReceiveResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ReceiveResponse findReceive(Long receiveId) {
        Receive receive = getReceive(receiveId);
        return ReceiveResponse.from(receive);
    }

    @Override
    public ReceiveResponse createReceive(ReceiveRequest request) {
        Long skuId = warepulseClient.getSku(request.getSkuId()).getSkuId();
        Long locationId = warepulseClient.getLocation(request.getLocationId()).getLocationId();

        Receive receive = Receive.create(skuId, locationId, request.getExpectedQty());
        receiveRepository.save(receive);

        return ReceiveResponse.from(receive);
    }

    @Override
    public ReceiveResponse inspectedReceive(Long receiveId, String username, int receivedQty) {
        Receive receive = getReceive(receiveId);

        if (receive.getStatus() != ReceiveStatus.CREATED) {
            throw new ReceiveServiceException(ErrorCode.RECEIVE_INSPECTION_NOT_CREATED);
        }

        int locationCapacity = warepulseClient.getLocation(receive.getLocationId()).getCapacity();

        int sumQuantity = inventoryServiceClient.getSumQuantityByLocation(receive.getLocationId()).getQuantity();
        if (locationCapacity < (sumQuantity + receivedQty)) {
            throw new ReceiveServiceException(ErrorCode.LOCATION_CAPACITY_EXCEEDED);
        }

        receive.inspect(receivedQty, username);
        return ReceiveResponse.from(receive);
    }

    @Override
    public ReceiveResponse completedReceive(Long receiveId, String username) {
        Receive receive = getReceive(receiveId);

        if (receive.getStatus() != ReceiveStatus.INSPECTED) {
            throw new ReceiveServiceException(ErrorCode.RECEIVE_INSPECTION_NOT_COMPLETED);
        }

        ReceiveDto dto = ReceiveDto.of(
                receive.getSkuId(),
                receive.getLocationId(),
                ReceiveReason.PURCHASE,
                receive.getReceivedQty()
        );
        kafkaProducer.send(INVENTORY_TOPIC, dto);
        receive.complete(username);

        return ReceiveResponse.from(receive);
    }

    @Override
    public void canceledReceive(Long receiveId) {
        Receive receive = getReceive(receiveId);

        if (receive.getStatus() == ReceiveStatus.COMPLETED) {
            throw new ReceiveServiceException(ErrorCode.RECEIVE_CANNOT_CANCEL_COMPLETED);
        }

        receive.cancel();
    }

    private Receive getReceive(Long receiveId) {
        return receiveRepository.findById(receiveId)
                .orElseThrow(() -> new ReceiveServiceException(ErrorCode.RECEIVE_NOT_FOUND));
    }
}
