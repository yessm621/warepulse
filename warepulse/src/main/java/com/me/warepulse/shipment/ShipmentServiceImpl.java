package com.me.warepulse.shipment;

import com.me.warepulse.client.InventoryClient;
import com.me.warepulse.client.InventoryDto;
import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.location.Location;
import com.me.warepulse.location.LocationRepository;
import com.me.warepulse.messagequeue.KafkaProducer;
import com.me.warepulse.messagequeue.shipment.ShipmentDto;
import com.me.warepulse.messagequeue.shipment.ShipmentReason;
import com.me.warepulse.shipment.dto.ShipmentRequest;
import com.me.warepulse.shipment.dto.ShipmentResponse;
import com.me.warepulse.sku.Sku;
import com.me.warepulse.sku.SkuRepository;
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
    private final SkuRepository skuRepository;
    private final LocationRepository locationRepository;
    private final InventoryClient inventoryClient;
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
        Sku sku = skuRepository.findById(request.getSkuId())
                .orElseThrow(() -> new WarePulseException(ErrorCode.SKU_NOT_FOUND));
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new WarePulseException(ErrorCode.LOCATION_NOT_FOUND));

        InventoryDto inventory = inventoryClient.getInventory(request.getSkuId(), request.getLocationId());
        if (request.getQuantity() > (inventory.getQuantity() - inventory.getReservedQty())) {
            throw new WarePulseException(ErrorCode.SHIPMENT_CREATED_QTY_EXCEEDED);
        }

        Shipment shipment = Shipment.create(sku, location, request.getQuantity(), inventory.getInventoryId());
        shipmentRepository.save(shipment);

        return ShipmentResponse.from(shipment);
    }

    @Override
    public ShipmentResponse pickingShipment(Long shipmentId, int pickedQty, String username) {
        Shipment shipment = getShipment(shipmentId);

        if (shipment.getStatus() != ShipmentStatus.CREATED) {
            throw new WarePulseException(ErrorCode.SHIPMENT_INSPECTION_INVALID_STATUS_CREATED);
        }

        sendInventoryEvent(shipment, ShipmentReason.RESERVED, pickedQty);

        shipment.picking(pickedQty, username);
        return ShipmentResponse.from(shipment);
    }

    @Override
    public ShipmentResponse shippedShipment(Long shipmentId, String username) {
        Shipment shipment = getShipment(shipmentId);

        if (shipment.getStatus() != ShipmentStatus.PICKING) {
            throw new WarePulseException(ErrorCode.SHIPMENT_INSPECTION_NOT_COMPLETED);
        }

        sendInventoryEvent(shipment, ShipmentReason.SHIP_OUT, shipment.getPickedQty());

        shipment.shipped(username);

        return ShipmentResponse.from(shipment);
    }

    @Override
    public ShipmentResponse canceledShipment(Long shipmentId) {
        Shipment shipment = getShipment(shipmentId);

        if (shipment.getStatus() == ShipmentStatus.SHIPPED) {
            throw new WarePulseException(ErrorCode.SHIPMENT_ALREADY_SHIPPED);
        }

        if (shipment.getStatus() == ShipmentStatus.PICKING) {
            sendInventoryEvent(shipment, ShipmentReason.RESERVED_CANCEL, shipment.getPickedQty());
        }

        shipment.canceled();
        return ShipmentResponse.from(shipment);
    }

    private Shipment getShipment(Long shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.SHIPMENT_NOT_FOUND));
    }

    private void sendInventoryEvent(Shipment shipment, ShipmentReason reason, int quantity) {
        ShipmentDto dto = ShipmentDto.of(
                shipment.getSku().getId(),
                shipment.getLocation().getId(),
                reason,
                quantity
        );
        kafkaProducer.send(INVENTORY_TOPIC, dto);
    }
}
