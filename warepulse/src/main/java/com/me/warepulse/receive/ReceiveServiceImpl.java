package com.me.warepulse.receive;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.inventory.controller.dto.InventoryDto;
import com.me.warepulse.inventory.entity.EventEnum.IncreaseReason;
import com.me.warepulse.inventory.entity.Inventory;
import com.me.warepulse.inventory.repository.InventoryRepository;
import com.me.warepulse.inventory.service.InventoryEventService;
import com.me.warepulse.inventory.service.InventoryService;
import com.me.warepulse.inventory.service.dto.IncreaseInventoryDto;
import com.me.warepulse.location.Location;
import com.me.warepulse.location.LocationRepository;
import com.me.warepulse.receive.dto.ReceiveRequest;
import com.me.warepulse.receive.dto.ReceiveResponse;
import com.me.warepulse.sku.Sku;
import com.me.warepulse.sku.SkuRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class ReceiveServiceImpl implements ReceiveService {

    private final ReceiveRepository receiveRepository;
    private final SkuRepository skuRepository;
    private final LocationRepository locationRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;
    private final InventoryEventService inventoryEventService;

    @Transactional(readOnly = true)
    @Override
    public List<ReceiveResponse> findReceives() {
        return receiveRepository.findAll()
                .stream()
                .map(ReceiveResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ReceiveResponse findReceive(Long receiveId) {
        Receive receive = receiveRepository.findById(receiveId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.RECEIVE_NOT_FOUND));
        return ReceiveResponse.from(receive);
    }

    @Override
    public ReceiveResponse createReceive(ReceiveRequest request) {
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new WarePulseException(ErrorCode.LOCATION_NOT_FOUND));
        Sku sku = skuRepository.findById(request.getSkuId())
                .orElseThrow(() -> new WarePulseException(ErrorCode.SKU_NOT_FOUND));

        Receive receive = Receive.create(sku, location, request.getExpectedQty());
        receiveRepository.save(receive);

        return ReceiveResponse.from(receive);
    }

    @Override
    public ReceiveResponse inspectedReceive(Long receiveId, String username, int receivedQty) {
        Receive receive = receiveRepository.findById(receiveId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.RECEIVE_NOT_FOUND));
        receive.inspect(receivedQty, username);
        return ReceiveResponse.from(receive);
    }

    @Override
    public ReceiveResponse completedReceive(Long receiveId, String username) {
        Receive receive = receiveRepository.findById(receiveId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.RECEIVE_NOT_FOUND));

        if (!receive.getStatus().equals(ReceiveStatus.INSPECTED)) {
            throw new WarePulseException(ErrorCode.RECEIVE_INSPECTION_NOT_COMPLETED);
        }

        Long inventoryId = getInventory(receive);
        increaseInventoryEvent(inventoryId, receive);

        receive.complete(username, inventoryId);

        return ReceiveResponse.from(receive);
    }

    private Long getInventory(Receive receive) {
        return inventoryRepository.findBySkuIdAndLocationId(receive.getSku().getId(), receive.getLocation().getId())
                .map(Inventory::getId)
                .orElseGet(() -> createInventory(receive));
    }

    private Long createInventory(Receive receive) {
        InventoryDto dto = InventoryDto.of(
                receive.getSku().getId(),
                receive.getLocation().getId(),
                receive.getReceivedQty()
        );
        return inventoryService.createInventory(dto);
    }

    private void increaseInventoryEvent(Long inventoryId, Receive receive) {
        IncreaseInventoryDto dto = IncreaseInventoryDto.of(
                inventoryId,
                receive.getId(),
                IncreaseReason.PURCHASE_INBOUND,
                receive.getReceivedQty()
        );
        inventoryEventService.receive(dto);
    }
}
