package com.me.warepulse.adjustment;

import com.me.warepulse.adjustment.dto.AdjustmentRequest;
import com.me.warepulse.adjustment.dto.AdjustmentResponse;
import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.inventory.entity.Inventory;
import com.me.warepulse.inventory.repository.InventoryRepository;
import com.me.warepulse.inventory.service.InventoryEventService;
import com.me.warepulse.location.Location;
import com.me.warepulse.location.LocationRepository;
import com.me.warepulse.sku.Sku;
import com.me.warepulse.sku.SkuRepository;
import com.me.warepulse.user.User;
import com.me.warepulse.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdjustmentServiceImpl implements AdjustmentService {

    private final AdjustmentRepository adjustmentRepository;
    private final SkuRepository skuRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryEventService inventoryEventService;

    @Override
    public List<AdjustmentResponse> findAdjustments() {
        return adjustmentRepository.findAll()
                .stream()
                .map(AdjustmentResponse::from)
                .toList();
    }

    @Override
    public AdjustmentResponse findAdjustment(Long adjustmentId) {
        return adjustmentRepository.findById(adjustmentId)
                .map(AdjustmentResponse::from)
                .orElseThrow(() -> new WarePulseException(ErrorCode.ADJUSTMENT_NOT_FOUND));
    }

    @Transactional
    @Override
    public AdjustmentResponse create(AdjustmentRequest request) {
        Sku sku = findSku(request.getSkuId());
        Location location = findLocation(request.getLocationId());
        User operator = findUser(request.getOperatorId());
        Inventory inventory = findInventory(sku.getId(), location.getId());

        adjustmentInventoryEvent(request, inventory.getId());

        Adjustment adjustment = Adjustment.create(sku, location, operator, request.getDelta(), request.getReason(), inventory.getId());
        adjustmentRepository.save(adjustment);

        return AdjustmentResponse.from(adjustment);
    }

    private Sku findSku(Long skuId) {
        return skuRepository.findById(skuId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.SKU_NOT_FOUND));
    }

    private Location findLocation(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.LOCATION_NOT_FOUND));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.USER_NOT_FOUND));
    }

    private Inventory findInventory(Long skuId, Long locationId) {
        return inventoryRepository.findBySkuIdAndLocationId(skuId, locationId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.INVENTORY_NOT_FOUND));
    }

    private void adjustmentInventoryEvent(AdjustmentRequest request, Long inventoryId) {
        AdjustmentInventoryDto dto = AdjustmentInventoryDto.of(
                inventoryId,
                request.getReason(),
                request.getDelta()
        );
        inventoryEventService.adjustment(dto);
    }
}
