package com.me.warepulse.inventory.service;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.inventory.dto.IncreaseInventoryDto;
import com.me.warepulse.inventory.entity.Inventory;
import com.me.warepulse.inventory.entity.InventoryEvent;
import com.me.warepulse.inventory.entity.InventoryEventType;
import com.me.warepulse.inventory.repository.InventoryEventRepository;
import com.me.warepulse.inventory.repository.InventoryRepository;
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
        Inventory inventory = inventoryRepository.findById(request.getInventoryId())
                .orElseThrow(() -> new WarePulseException(ErrorCode.INVENTORY_NOT_FOUND));

        inventory.increase(request.getQuantity());

        Map<String, Object> payload = new HashMap<>();
        payload.put("sku_id", inventory.getSku().getId());
        payload.put("location_id", inventory.getLocation().getId());
        payload.put("quantity", request.getQuantity());

        InventoryEvent inventoryEvent = InventoryEvent.create(
                inventory,
                inventory.getSku().getId(),
                inventory.getLocation().getId(),
                InventoryEventType.INCREASE,
                request.getQuantity(),
                payload
        );
        inventoryEventRepository.save(inventoryEvent);
    }
}
