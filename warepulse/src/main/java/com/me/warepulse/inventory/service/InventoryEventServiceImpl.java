package com.me.warepulse.inventory.service;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.inventory.entity.Inventory;
import com.me.warepulse.inventory.entity.InventoryEvent;
import com.me.warepulse.inventory.entity.InventoryEventType;
import com.me.warepulse.inventory.repository.InventoryEventRepository;
import com.me.warepulse.inventory.repository.InventoryRepository;
import com.me.warepulse.inventory.service.dto.DecreaseInventoryDto;
import com.me.warepulse.inventory.service.dto.IncreaseInventoryDto;
import com.me.warepulse.inventory.service.dto.ReleaseInventoryDto;
import com.me.warepulse.inventory.service.dto.ReserveInventoryDto;
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
    public void receive(IncreaseInventoryDto request) {
        Inventory inventory = getInventory(request.getInventoryId());

        inventory.increase(request.getQuantity());

        saveEvent(inventory, InventoryEventType.INCREASE, request.getQuantity(), request.getReason());
    }

    @Override
    public void shipment(DecreaseInventoryDto request) {
        Inventory inventory = getInventory(request.getInventoryId());

        inventory.decrease(request.getQuantity());

        saveEvent(inventory, InventoryEventType.DECREASE, request.getQuantity(), request.getReason());
    }

    @Override
    public void reserve(ReserveInventoryDto request) {
        Inventory inventory = getInventory(request.getInventoryId());

        inventory.reserve(request.getReservedQty());

        saveEvent(inventory, InventoryEventType.DECREASE, request.getReservedQty(), request.getReason());
    }

    @Override
    public void release(ReleaseInventoryDto request) {
        Inventory inventory = getInventory(request.getInventoryId());

        inventory.release(request.getReservedQty());

        saveEvent(inventory, InventoryEventType.RELEASE, request.getReservedQty(), request.getReason());
    }

    private Inventory getInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.INVENTORY_NOT_FOUND));
        return inventory;
    }

    private void saveEvent(Inventory inventory, InventoryEventType type, int quantity, Object reason) {
        Map<String, Object> payload = createPayload(inventory, quantity, reason);

        InventoryEvent event = InventoryEvent.create(
                inventory,
                inventory.getSku().getId(),
                inventory.getLocation().getId(),
                type,
                quantity,
                payload
        );

        inventoryEventRepository.save(event);
    }

    private Map<String, Object> createPayload(Inventory inventory, int quantity, Object reason) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sku_id", inventory.getSku().getId());
        payload.put("location_id", inventory.getLocation().getId());
        payload.put("quantity", quantity);
        payload.put("reason", reason);
        return payload;
    }
}
