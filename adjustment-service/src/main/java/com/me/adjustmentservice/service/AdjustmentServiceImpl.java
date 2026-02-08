package com.me.adjustmentservice.service;

import com.me.adjustmentservice.client.InventoryServiceClient;
import com.me.adjustmentservice.client.WarepulseClient;
import com.me.adjustmentservice.client.dto.InventoryDto;
import com.me.adjustmentservice.client.dto.LocationResponse;
import com.me.adjustmentservice.client.dto.SkuResponse;
import com.me.adjustmentservice.client.dto.UserResponse;
import com.me.adjustmentservice.controller.dto.AdjustmentRequest;
import com.me.adjustmentservice.controller.dto.AdjustmentResponse;
import com.me.adjustmentservice.entity.Adjustment;
import com.me.adjustmentservice.exception.AdjustmentServiceException;
import com.me.adjustmentservice.exception.ErrorCode;
import com.me.adjustmentservice.messagequeue.KafkaProducer;
import com.me.adjustmentservice.repository.AdjustmentRepository;
import com.me.adjustmentservice.service.dto.AdjustmentInventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdjustmentServiceImpl implements AdjustmentService {

    private static final String INVENTORY_TOPIC = "inventory-adjustment-topic";

    private final AdjustmentRepository adjustmentRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final WarepulseClient warepulseClient;
    private final KafkaProducer kafkaProducer;

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
                .orElseThrow(() -> new AdjustmentServiceException(ErrorCode.ADJUSTMENT_NOT_FOUND));
    }

    @Transactional
    @Override
    public AdjustmentResponse create(AdjustmentRequest request) {
        Long skuId = warepulseClient.getSku(request.getSkuId()).getSkuId();
        Long locationId = warepulseClient.getLocation(request.getLocationId()).getLocationId();
        Long userId = warepulseClient.getUser(request.getOperatorId()).getUserId();
        Long inventoryId = inventoryServiceClient.getInventory(skuId, locationId).getInventoryId();

        adjustmentInventoryEvent(request, inventoryId);

        Adjustment adjustment = Adjustment.create(skuId, locationId, userId, request.getDelta(), request.getReason(), inventoryId);
        adjustmentRepository.save(adjustment);

        return AdjustmentResponse.from(adjustment);
    }

    private void adjustmentInventoryEvent(AdjustmentRequest request, Long inventoryId) {
        AdjustmentInventoryDto dto = AdjustmentInventoryDto.of(
                inventoryId,
                request.getReason(),
                request.getDelta()
        );
        kafkaProducer.send(INVENTORY_TOPIC, dto);
    }
}
