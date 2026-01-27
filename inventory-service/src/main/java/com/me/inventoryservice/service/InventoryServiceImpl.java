package com.me.inventoryservice.service;

import com.me.inventoryservice.controller.dto.InventoryAvailableResponse;
import com.me.inventoryservice.controller.dto.InventoryDto;
import com.me.inventoryservice.controller.dto.InventoryResponse;
import com.me.inventoryservice.controller.dto.SkuInventoryResponse;
import com.me.inventoryservice.entity.Inventory;
import com.me.inventoryservice.exception.ErrorCode;
import com.me.inventoryservice.exception.InventoryServiceException;
import com.me.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
//    private final SkuRepository skuRepository;
//    private final LocationRepository locationRepository;

    @Override
    public InventoryResponse getInventory(Long skuId, Long locationId) {
        Inventory inventory = inventoryRepository.findBySkuIdAndLocationId(skuId, locationId)
                .orElseThrow(() -> new InventoryServiceException(ErrorCode.INVENTORY_NOT_FOUND));
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
                .orElseThrow(() -> new InventoryServiceException(ErrorCode.INVENTORY_NOT_FOUND));
        return InventoryAvailableResponse.from(inventory);
    }

    @Transactional
    @Override
    public Long createInventory(InventoryDto dto) {
        if (inventoryRepository.existsBySkuIdAndLocationId(dto.getSkuId(), dto.getLocationId())) {
            throw new InventoryServiceException(ErrorCode.DUPLICATE_INVENTORY);
        }

        Inventory inventory = Inventory.create(dto.getSkuId(), dto.getLocationId(), dto.getQuantity());
        inventoryRepository.save(inventory);
        return inventory.getId();
    }
}
