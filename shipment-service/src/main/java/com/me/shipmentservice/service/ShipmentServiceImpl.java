package com.me.shipmentservice.service;


import com.me.shipmentservice.client.InventoryServiceClient;
import com.me.shipmentservice.client.WarepulseClient;
import com.me.shipmentservice.client.dto.InventoryDto;
import com.me.shipmentservice.controller.dto.ShipmentRequest;
import com.me.shipmentservice.controller.dto.ShipmentResponse;
import com.me.shipmentservice.service.dto.ShipmentDto;
import com.me.shipmentservice.service.dto.ShipmentReason;
import com.me.shipmentservice.entity.Shipment;
import com.me.shipmentservice.entity.ShipmentStatus;
import com.me.shipmentservice.exception.ErrorCode;
import com.me.shipmentservice.exception.ShipmentServiceException;
import com.me.shipmentservice.messagequeue.KafkaProducer;
import com.me.shipmentservice.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

    private static final String INVENTORY_TOPIC = "inventory-shipment-topic";

    private final ShipmentRepository shipmentRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final WarepulseClient warepulseClient;
    private final KafkaProducer kafkaProducer;

    @Transactional(readOnly = true)
    @Override
    public List<ShipmentResponse> findShipments() {
        return shipmentRepository.findAll()
                .stream()
                .map(ShipmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ShipmentResponse findShipment(Long shipmentId) {
        Shipment shipment = getShipment(shipmentId);
        return ShipmentResponse.from(shipment);
    }

    @Override
    public ShipmentResponse createShipment(ShipmentRequest request) {
        Long skuId = warepulseClient.getSku(request.getSkuId()).getSkuId();
        Long locationId = warepulseClient.getLocation(request.getLocationId()).getLocationId();

        InventoryDto inventory = inventoryServiceClient.getInventory(skuId, locationId);
        if (request.getQuantity() > (inventory.getQuantity() - inventory.getReservedQty())) {
            throw new ShipmentServiceException(ErrorCode.SHIPMENT_CREATED_QTY_EXCEEDED);
        }

        Shipment shipment = Shipment.create(skuId, locationId, request.getQuantity(), inventory.getInventoryId());
        shipmentRepository.save(shipment);

        return ShipmentResponse.from(shipment);
    }

    @Override
    public ShipmentResponse pickingShipment(Long shipmentId, int pickedQty, String username) {
        Shipment shipment = getShipment(shipmentId);

        if (shipment.getStatus() != ShipmentStatus.CREATED) {
            throw new ShipmentServiceException(ErrorCode.SHIPMENT_INSPECTION_INVALID_STATUS_CREATED);
        }

        sendInventoryEvent(shipment, ShipmentReason.RESERVED, pickedQty);

        shipment.picking(pickedQty, username);
        return ShipmentResponse.from(shipment);
    }

    @Override
    public ShipmentResponse shippedShipment(Long shipmentId, String username) {
        Shipment shipment = getShipment(shipmentId);

        if (shipment.getStatus() != ShipmentStatus.PICKING) {
            throw new ShipmentServiceException(ErrorCode.SHIPMENT_INSPECTION_NOT_COMPLETED);
        }

        sendInventoryEvent(shipment, ShipmentReason.SHIP_OUT, shipment.getPickedQty());

        shipment.shipped(username);

        return ShipmentResponse.from(shipment);
    }

    @Override
    public ShipmentResponse canceledShipment(Long shipmentId) {
        Shipment shipment = getShipment(shipmentId);

        if (shipment.getStatus() == ShipmentStatus.SHIPPED) {
            throw new ShipmentServiceException(ErrorCode.SHIPMENT_ALREADY_SHIPPED);
        }

        if (shipment.getStatus() == ShipmentStatus.PICKING) {
            sendInventoryEvent(shipment, ShipmentReason.RESERVED_CANCEL, shipment.getPickedQty());
        }

        shipment.canceled();
        return ShipmentResponse.from(shipment);
    }

    private Shipment getShipment(Long shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ShipmentServiceException(ErrorCode.SHIPMENT_NOT_FOUND));
    }

    private void sendInventoryEvent(Shipment shipment, ShipmentReason reason, int quantity) {
        ShipmentDto dto = ShipmentDto.of(
                shipment.getSkuId(),
                shipment.getLocationId(),
                reason,
                quantity
        );
        kafkaProducer.send(INVENTORY_TOPIC, dto);
    }
}
