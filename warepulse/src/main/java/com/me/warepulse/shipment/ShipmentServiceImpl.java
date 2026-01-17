package com.me.warepulse.shipment;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.inventory.entity.EventEnum.DecreaseReason;
import com.me.warepulse.inventory.entity.EventEnum.IncreaseReason;
import com.me.warepulse.inventory.entity.Inventory;
import com.me.warepulse.inventory.repository.InventoryRepository;
import com.me.warepulse.inventory.service.InventoryEventService;
import com.me.warepulse.inventory.service.dto.DecreaseInventoryDto;
import com.me.warepulse.inventory.service.dto.ReleaseInventoryDto;
import com.me.warepulse.inventory.service.dto.ReserveInventoryDto;
import com.me.warepulse.location.Location;
import com.me.warepulse.location.LocationRepository;
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

    private final ShipmentRepository shipmentRepository;
    private final SkuRepository skuRepository;
    private final LocationRepository locationRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryEventService inventoryEventService;

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

        Inventory inventory = inventoryRepository.findBySkuIdAndLocationId(request.getSkuId(), request.getLocationId())
                .orElseThrow(() -> new WarePulseException(ErrorCode.SHIPMENT_NO_AVAILABLE_STOCK));

        if (request.getQuantity() > (inventory.getQuantity() - inventory.getReservedQty())) {
            throw new WarePulseException(ErrorCode.SHIPMENT_CREATED_QTY_EXCEEDED);
        }

        Shipment shipment = Shipment.create(sku, location, request.getQuantity(), inventory.getId());
        shipmentRepository.save(shipment);

        return ShipmentResponse.from(shipment);
    }

    @Override
    public ShipmentResponse pickingShipment(Long shipmentId, int pickedQty, String username) {
        Shipment shipment = getShipment(shipmentId);

        if (shipment.getStatus() != ShipmentStatus.CREATED) {
            throw new WarePulseException(ErrorCode.SHIPMENT_INSPECTION_INVALID_STATUS_CREATED);
        }

        Long inventoryId = getInventory(shipment);
        reserveInventoryEvent(pickedQty, shipment, inventoryId);

        shipment.picking(pickedQty, username);
        return ShipmentResponse.from(shipment);
    }

    @Override
    public ShipmentResponse shippedShipment(Long shipmentId, String username) {
        Shipment shipment = getShipment(shipmentId);

        if (shipment.getStatus() != ShipmentStatus.PICKING) {
            throw new WarePulseException(ErrorCode.SHIPMENT_INSPECTION_NOT_COMPLETED);
        }

        Long inventoryId = getInventory(shipment);
        decreaseInventoryEvent(inventoryId, shipment);

        shipment.shipped(inventoryId, username);

        return ShipmentResponse.from(shipment);
    }

    @Override
    public ShipmentResponse canceledShipment(Long shipmentId) {
        Shipment shipment = getShipment(shipmentId);

        if (shipment.getStatus() == ShipmentStatus.SHIPPED) {
            throw new WarePulseException(ErrorCode.SHIPMENT_ALREADY_SHIPPED);
        }

        if (shipment.getStatus() == ShipmentStatus.PICKING) {
            Long inventoryId = getInventory(shipment);
            releaseInventoryEvent(shipment, inventoryId);
        }

        shipment.canceled();
        return ShipmentResponse.from(shipment);
    }

    private Shipment getShipment(Long shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.SHIPMENT_NOT_FOUND));
    }

    private Long getInventory(Shipment shipment) {
        return inventoryRepository.findBySkuIdAndLocationId(shipment.getSku().getId(), shipment.getLocation().getId())
                .map(Inventory::getId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.INVENTORY_NOT_FOUND));
    }

    private void decreaseInventoryEvent(Long inventoryId, Shipment shipment) {
        DecreaseInventoryDto dto = DecreaseInventoryDto.of(
                inventoryId,
                shipment.getId(),
                DecreaseReason.SHIP_OUT,
                shipment.getPickedQty()
        );
        inventoryEventService.shipment(dto);
    }

    private void reserveInventoryEvent(int pickedQty, Shipment shipment, Long inventoryId) {
        ReserveInventoryDto dto = ReserveInventoryDto.of(
                inventoryId,
                shipment.getId(),
                DecreaseReason.RESERVED,
                pickedQty
        );
        inventoryEventService.reserve(dto);
    }

    private void releaseInventoryEvent(Shipment shipment, Long inventoryId) {
        ReleaseInventoryDto dto = ReleaseInventoryDto.of(
                inventoryId,
                shipment.getId(),
                IncreaseReason.RESERVED_CANCEL,
                shipment.getPickedQty()
        );
        inventoryEventService.release(dto);
    }
}
