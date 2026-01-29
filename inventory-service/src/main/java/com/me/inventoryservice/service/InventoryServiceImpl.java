package com.me.inventoryservice.service;

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
}
