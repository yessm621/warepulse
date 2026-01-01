package com.me.warepulse.inventory;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.inventory.dto.SkuInventoryResponse;
import com.me.warepulse.inventory.dto.InventoryResponse;
import com.me.warepulse.inventory.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public InventoryResponse findInventoryBySkuIdAndLocationId(Long skuId, Long locationId) {
        Inventory inventory = inventoryRepository.findBySkuIdAndLocationId(skuId, locationId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.INVENTORY_NOT_FOUND));
        return InventoryResponse.from(inventory);
    }

    @Override
    public SkuInventoryResponse findInventoriesBySkuId(Long skuId) {
        List<Inventory> inventories = inventoryRepository.findBySkuId(skuId);
        int totalQuantity = inventoryRepository.sumQuantityBySkuId(skuId);
        return SkuInventoryResponse.from(skuId, inventories, totalQuantity);
    }

    @Override
    public List<InventoryResponse> findInventoriesByLocationId(Long locationId) {
        return inventoryRepository.findByLocationId(locationId)
                .stream()
                .map(InventoryResponse::from)
                .toList();
    }

    @Override
    public int inventoryAvailable(Long skuId, Long locationId) {
        return inventoryRepository.availableQty(skuId, locationId);
    }
}
