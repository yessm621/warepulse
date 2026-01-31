package com.me.warepulse.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
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

    public <T> void send(String topic, T dto) {
        try {
            String jsonInString = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send(topic, jsonInString);
            log.info("Kafka Producer sent data from th Inventory microservice: {}", jsonInString);
        } catch (JsonProcessingException ex) {
            throw new WarePulseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
