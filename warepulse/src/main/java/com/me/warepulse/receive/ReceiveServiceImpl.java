package com.me.warepulse.receive;

import com.me.warepulse.client.InventoryClient;
import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.location.Location;
import com.me.warepulse.location.LocationRepository;
import com.me.warepulse.messagequeue.KafkaProducer;
import com.me.warepulse.messagequeue.receive.ReceiveDto;
import com.me.warepulse.messagequeue.receive.ReceiveReason;
import com.me.warepulse.receive.dto.ReceiveRequest;
import com.me.warepulse.receive.dto.ReceiveResponse;
import com.me.warepulse.sku.Sku;
import com.me.warepulse.sku.SkuRepository;
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
    private final SkuRepository skuRepository;
    private final LocationRepository locationRepository;
    private final InventoryClient inventoryClient;
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
        Sku sku = skuRepository.findById(request.getSkuId())
                .orElseThrow(() -> new WarePulseException(ErrorCode.SKU_NOT_FOUND));
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new WarePulseException(ErrorCode.LOCATION_NOT_FOUND));

        Receive receive = Receive.create(sku, location, request.getExpectedQty());
        receiveRepository.save(receive);

        return ReceiveResponse.from(receive);
    }

    @Override
    public ReceiveResponse inspectedReceive(Long receiveId, String username, int receivedQty) {
        Receive receive = getReceive(receiveId);

        if (receive.getStatus() != ReceiveStatus.CREATED) {
            throw new WarePulseException(ErrorCode.RECEIVE_INSPECTION_NOT_CREATED);
        }

        int sumQuantity = inventoryClient.getSumQuantityByLocation(receive.getLocation().getId());
        if (receive.getLocation().getCapacity() < (sumQuantity + receivedQty)) {
            throw new WarePulseException(ErrorCode.LOCATION_CAPACITY_EXCEEDED);
        }

        receive.inspect(receivedQty, username);
        return ReceiveResponse.from(receive);
    }

    @Override
    public ReceiveResponse completedReceive(Long receiveId, String username) {
        Receive receive = getReceive(receiveId);

        if (receive.getStatus() != ReceiveStatus.INSPECTED) {
            throw new WarePulseException(ErrorCode.RECEIVE_INSPECTION_NOT_COMPLETED);
        }

        ReceiveDto dto = ReceiveDto.of(
                receive.getSku().getId(),
                receive.getLocation().getId(),
                ReceiveReason.PURCHASE_INBOUND,
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
            throw new WarePulseException(ErrorCode.RECEIVE_CANNOT_CANCEL_COMPLETED);
        }

        receive.cancel();
    }

    private Receive getReceive(Long receiveId) {
        return receiveRepository.findById(receiveId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.RECEIVE_NOT_FOUND));
    }
}
