package com.me.inventoryservice.service;

import com.me.inventoryservice.entity.Inventory;
import com.me.inventoryservice.entity.InventoryEvent;
import com.me.inventoryservice.entity.InventoryEventType;
import com.me.inventoryservice.exception.ErrorCode;
import com.me.inventoryservice.exception.InventoryServiceException;
import com.me.inventoryservice.repository.InventoryEventRepository;
import com.me.inventoryservice.repository.InventoryRepository;
import com.me.inventoryservice.service.dto.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryEventServiceImpl implements InventoryEventService {

    private final InventoryEventRepository inventoryEventRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public void receive(IncreaseInventoryDto dto) {
        Inventory inventory = getInventory(dto.getInventoryId());

        inventory.increase(dto.getQuantity());

        saveEvent(inventory, InventoryEventType.INCREASE, dto.getQuantity(), dto.getReason());
    }

    @Override
    public void shipment(DecreaseInventoryDto dto) {
        Inventory inventory = getInventory(dto.getInventoryId());

        inventory.decrease(dto.getQuantity());

        saveEvent(inventory, InventoryEventType.DECREASE, dto.getQuantity(), dto.getReason());
    }

    @Override
    public void reserve(ReserveInventoryDto dto) {
        Inventory inventory = getInventory(dto.getInventoryId());

        inventory.reserve(dto.getReservedQty());

        saveEvent(inventory, InventoryEventType.RESERVE, dto.getReservedQty(), dto.getReason());
    }

    @Override
    public void release(ReleaseInventoryDto dto) {
        Inventory inventory = getInventory(dto.getInventoryId());

        inventory.release(dto.getReservedQty());

        saveEvent(inventory, InventoryEventType.RELEASE, dto.getReservedQty(), dto.getReason());
    }

    @Override
    public void adjustment(AdjustmentInventoryDto dto) {
        Inventory inventory = getInventory(dto.getInventoryId());
        inventory.adjustment(dto.getDelta());
        saveEvent(inventory, InventoryEventType.ADJUSTED, dto.getDelta(), dto.getReason());
    }

    private Inventory getInventory(Long inventoryId) {
        return inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new InventoryServiceException(ErrorCode.INVENTORY_NOT_FOUND));
    }

    private void saveEvent(Inventory inventory, InventoryEventType type, int quantity, Object reason) {
        Map<String, Object> payload = createPayload(inventory, quantity, reason);

        InventoryEvent event = InventoryEvent.create(
                inventory,
                inventory.getSkuId(),
                inventory.getLocationId(),
                type,
                quantity,
                payload
        );

        inventoryEventRepository.save(event);
    }

    private Map<String, Object> createPayload(Inventory inventory, int quantity, Object reason) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sku_id", inventory.getSkuId());
        payload.put("location_id", inventory.getLocationId());
        payload.put("quantity", quantity);
        payload.put("reason", reason);
        return payload;
    }
}
