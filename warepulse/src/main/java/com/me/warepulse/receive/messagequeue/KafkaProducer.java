package com.me.warepulse.receive.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(String topic, InventoryReceiveDto dto) {
        try {
            String jsonInString = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send(topic, jsonInString);
            log.info("Kafka Producer sent data from th Inventory microservice: {}", jsonInString);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }
}
