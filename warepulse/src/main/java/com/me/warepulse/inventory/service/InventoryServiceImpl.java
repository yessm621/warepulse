package com.me.warepulse.inventory.service;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.inventory.controller.dto.InventoryAvailableResponse;
import com.me.warepulse.inventory.controller.dto.InventoryDto;
import com.me.warepulse.inventory.controller.dto.InventoryResponse;
import com.me.warepulse.inventory.controller.dto.SkuInventoryResponse;
import com.me.warepulse.inventory.entity.Inventory;
import com.me.warepulse.inventory.repository.InventoryRepository;
import com.me.warepulse.location.Location;
import com.me.warepulse.location.LocationRepository;
import com.me.warepulse.sku.Sku;
import com.me.warepulse.sku.SkuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final SkuRepository skuRepository;
    private final LocationRepository locationRepository;

    @Override
    public InventoryResponse getInventory(Long skuId, Long locationId) {
        Inventory inventory = inventoryRepository.findBySkuIdAndLocationId(skuId, locationId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.INVENTORY_NOT_FOUND));
        return InventoryResponse.from(inventory);
    }

    @Override
    public SkuInventoryResponse getInventoriesBySku(Long skuId) {
        List<Inventory> inventories = inventoryRepository.findBySkuId(skuId);
        int totalQuantity = inventoryRepository.sumQuantityBySkuId(skuId);
        return SkuInventoryResponse.from(skuId, inventories, totalQuantity);
    }

    @Override
    public List<InventoryResponse> getInventoriesByLocation(Long locationId) {
        return inventoryRepository.findByLocationId(locationId)
                .stream()
                .map(InventoryResponse::from)
                .toList();
    }

    @Override
    public InventoryAvailableResponse inventoryAvailable(Long skuId, Long locationId) {
        Inventory inventory = inventoryRepository.findBySkuIdAndLocationId(skuId, locationId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.INVENTORY_NOT_FOUND));
        return InventoryAvailableResponse.from(inventory);
    }

    @Transactional
    @Override
    public Long createInventory(InventoryDto dto) {
        if (inventoryRepository.existsBySkuIdAndLocationId(dto.getSkuId(), dto.getLocationId())) {
            throw new WarePulseException(ErrorCode.DUPLICATE_INVENTORY);
        }

        Sku sku = skuRepository.findById(dto.getSkuId())
                .orElseThrow(() -> new WarePulseException(ErrorCode.SKU_NOT_FOUND));
        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new WarePulseException(ErrorCode.LOCATION_NOT_FOUND));

        Inventory inventory = Inventory.create(sku, location, dto.getQuantity());
        inventoryRepository.save(inventory);
        return inventory.getId();
    }
}
