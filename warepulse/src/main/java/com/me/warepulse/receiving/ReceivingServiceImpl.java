package com.me.warepulse.receiving;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.inventory.controller.dto.InventoryDto;
import com.me.warepulse.inventory.entity.EventEnum.IncreaseReason;
import com.me.warepulse.inventory.entity.Inventory;
import com.me.warepulse.inventory.repository.InventoryRepository;
import com.me.warepulse.inventory.service.InventoryEventService;
import com.me.warepulse.inventory.service.InventoryService;
import com.me.warepulse.inventory.service.dto.IncreaseInventoryDto;
import com.me.warepulse.location.Location;
import com.me.warepulse.location.LocationRepository;
import com.me.warepulse.receiving.dto.ReceivingRequest;
import com.me.warepulse.receiving.dto.ReceivingResponse;
import com.me.warepulse.sku.Sku;
import com.me.warepulse.sku.SkuRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class ReceivingServiceImpl implements ReceivingService {

    private final ReceivingRepository receivingRepository;
    private final SkuRepository skuRepository;
    private final LocationRepository locationRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;
    private final InventoryEventService inventoryEventService;

    @Override
    public ReceivingResponse createReceive(ReceivingRequest request) {
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new WarePulseException(ErrorCode.LOCATION_NOT_FOUND));
        Sku sku = skuRepository.findById(request.getSkuId())
                .orElseThrow(() -> new WarePulseException(ErrorCode.SKU_NOT_FOUND));

        Receiving receiving = Receiving.create(sku, location, request.getExpectedQty());
        receivingRepository.save(receiving);

        return ReceivingResponse.from(receiving);
    }

    @Override
    public ReceivingResponse inspectedReceive(Long receivingId, String username, int receivedQty) {
        Receiving receiving = receivingRepository.findById(receivingId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.RECEIVING_NOT_FOUND));
        receiving.inspect(receivedQty, username);
        return ReceivingResponse.from(receiving);
    }

    @Override
    public ReceivingResponse completedReceive(Long receivingId, String username) {
        Receiving receiving = receivingRepository.findById(receivingId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.RECEIVING_NOT_FOUND));
        receiving.complete(username);

        Long inventoryId = getInventory(receiving);
        increaseInventoryEvent(inventoryId, receiving);

        return ReceivingResponse.from(receiving);
    }

    private Long getInventory(Receiving receiving) {
        return inventoryRepository.findBySkuIdAndLocationId(receiving.getSku().getId(), receiving.getLocation().getId())
                .map(Inventory::getId)
                .orElseGet(() -> createInventory(receiving));
    }

    private Long createInventory(Receiving receiving) {
        InventoryDto dto = InventoryDto.of(
                receiving.getSku().getId(),
                receiving.getLocation().getId(),
                receiving.getReceivedQty()
        );
        return inventoryService.createInventory(dto);
    }

    private void increaseInventoryEvent(Long inventoryId, Receiving receiving) {
        IncreaseInventoryDto dto = IncreaseInventoryDto.of(
                inventoryId,
                receiving.getId(),
                IncreaseReason.PURCHASE_INBOUND,
                receiving.getReceivedQty()
        );
        inventoryEventService.receive(dto);
    }
}
