package com.me.inventoryservice.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.inventoryservice.messagequeue.dto.ReceiveDto;
import com.me.inventoryservice.messagequeue.dto.ShipmentDto;
import com.me.inventoryservice.service.InventoryEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final InventoryEventService inventoryEventService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inventory-receive-topic")
    @Transactional
    public void updateInventory(String kafkaMessage) {
        ReceiveDto dto = deserialize(kafkaMessage, ReceiveDto.class);
        if (dto == null) return;

        switch (dto.getReason()) {
            case PURCHASE -> inventoryEventService.receive(dto);
            default -> log.warn("Unknown shipment reason: {}", dto.getReason());
        }
    }

    @KafkaListener(topics = "inventory-shipment-topic")
    @Transactional
    public void shipment(String kafkaMessage) {
        ShipmentDto dto = deserialize(kafkaMessage, ShipmentDto.class);
        if (dto == null) return;

        switch (dto.getReason()) {
            case SHIP_OUT -> inventoryEventService.shipment(dto);
            case RESERVED -> inventoryEventService.reserve(dto);
            case RESERVED_CANCEL -> inventoryEventService.release(dto);
            default -> log.warn("Unknown shipment reason: {}", dto.getReason());
        }
    }

    private <T> T deserialize(String kafkaMessage, Class<T> clazz) {
        try {
            return objectMapper.readValue(kafkaMessage, clazz);
        } catch (JsonProcessingException ex) {
            log.error("Failed to deserialize message to {}: {}", clazz.getSimpleName(), kafkaMessage, ex);
            return null;
        }
    }
}
