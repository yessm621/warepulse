package com.me.inventoryservice.service;

import com.me.inventoryservice.entity.Inventory;
import com.me.inventoryservice.entity.InventoryEvent;
import com.me.inventoryservice.entity.InventoryEventType;
import com.me.inventoryservice.exception.ErrorCode;
import com.me.inventoryservice.exception.InventoryServiceException;
import com.me.inventoryservice.messagequeue.dto.ReceiveDto;
import com.me.inventoryservice.messagequeue.dto.ShipmentDto;
import com.me.inventoryservice.repository.InventoryEventRepository;
import com.me.inventoryservice.repository.InventoryRepository;
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
    public void receive(ReceiveDto dto) {
        Inventory inventory = inventoryRepository.findBySkuIdAndLocationId(dto.getSkuId(), dto.getLocationId())
                .orElseGet(() -> {
                    Inventory newInventory = Inventory.create(dto.getSkuId(), dto.getLocationId(), dto.getReceivedQty());
                    return inventoryRepository.save(newInventory);
                });
        inventory.increase(dto.getReceivedQty());
        saveEvent(inventory, InventoryEventType.INCREASE, dto.getReceivedQty(), dto.getReason());
    }

    @Override
    public void shipment(ShipmentDto dto) {
        Inventory inventory = getInventory(dto.getSkuId(), dto.getLocationId());
        inventory.decrease(dto.getReservedQty());
        saveEvent(inventory, InventoryEventType.DECREASE, dto.getReservedQty(), dto.getReason());
    }

    @Override
    public void reserve(ShipmentDto dto) {
        Inventory inventory = getInventory(dto.getSkuId(), dto.getLocationId());
        inventory.reserve(dto.getReservedQty());
        saveEvent(inventory, InventoryEventType.RESERVE, dto.getReservedQty(), dto.getReason());
    }

    @Override
    public void release(ShipmentDto dto) {
        Inventory inventory = getInventory(dto.getSkuId(), dto.getLocationId());
        inventory.release(dto.getReservedQty());
        saveEvent(inventory, InventoryEventType.RELEASE, dto.getReservedQty(), dto.getReason());
    }

    /*@Override
    public void adjustment(AdjustmentInventoryDto dto) {
        Inventory inventory = getInventory(dto.getInventoryId());
        inventory.adjustment(dto.getDelta());
        saveEvent(inventory, InventoryEventType.ADJUSTED, dto.getDelta(), dto.getReason());
    }*/

    private Inventory getInventory(Long skuId, Long locationId) {
        return inventoryRepository.findBySkuIdAndLocationId(skuId, locationId)
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
