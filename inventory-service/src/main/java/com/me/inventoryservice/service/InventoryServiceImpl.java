package com.me.inventoryservice.service;

import com.me.inventoryservice.controller.response.InventoryResponse;
import com.me.inventoryservice.exception.ErrorCode;
import com.me.inventoryservice.exception.InventoryServiceException;
import com.me.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public int totalQuantity(Long locationId) {
        return inventoryRepository.sumQuantityByLocation(locationId);
    }

    @Override
    public InventoryResponse getInventory(Long skuId, Long locationId) {
        return inventoryRepository.findBySkuIdAndLocationId(skuId, locationId)
                .map(InventoryResponse::from)
                .orElseThrow(() -> new InventoryServiceException(ErrorCode.INVENTORY_NOT_FOUND));
    }
}
