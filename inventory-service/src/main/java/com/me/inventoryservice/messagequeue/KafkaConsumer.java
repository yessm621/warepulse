package com.me.inventoryservice.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.inventoryservice.entity.EventEnum.IncreaseReason;
import com.me.inventoryservice.entity.Inventory;
import com.me.inventoryservice.messagequeue.dto.InventoryReceiveDto;
import com.me.inventoryservice.repository.InventoryRepository;
import com.me.inventoryservice.service.InventoryEventService;
import com.me.inventoryservice.service.dto.IncreaseInventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventService inventoryEventService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inventory-receive-topic")
    @Transactional
    public void updateInventory(String kafkaMessage) {
        log.info("Kafka Message: ->" + kafkaMessage);

        try {
            InventoryReceiveDto dto = objectMapper.readValue(kafkaMessage, InventoryReceiveDto.class);

            // inventory create or update
            Inventory inventory = inventoryRepository.findBySkuIdAndLocationId(dto.getSkuId(), dto.getLocationId())
                    .orElseGet(() -> {
                        Inventory newInventory = Inventory.create(dto.getSkuId(), dto.getLocationId(), dto.getReceivedQty());
                        return inventoryRepository.save(newInventory);
                    });
            //inventory.increase(dto.getReceivedQty());

            // inventoryEvent create
            IncreaseInventoryDto increaseDto = IncreaseInventoryDto.of(
                    inventory.getId(),
                    dto.getReceiveId(),
                    IncreaseReason.PURCHASE_INBOUND,
                    dto.getReceivedQty()
            );
            inventoryEventService.receive(increaseDto);
        } catch (JsonProcessingException ex) {
            log.error("Failed to deserialize message: {}", kafkaMessage, ex);
        }
    }
}
