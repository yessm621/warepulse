package com.me.shipmentservice.client.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.shipmentservice.exception.ApiResponse;
import com.me.shipmentservice.exception.ErrorCode;
import com.me.shipmentservice.exception.ShipmentServiceException;
import feign.Response;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

@Slf4j
public class FeignResponseDecoder implements Decoder {

    private final ObjectMapper objectMapper;

    public FeignResponseDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException {
        if (response.body() == null) {
            return null;
        }

        try (InputStream is = response.body().asInputStream()) {
            JavaType dataType = objectMapper.getTypeFactory().constructType(type);
            JavaType apiResponseType = objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, dataType);

            ApiResponse<?> apiResponse = objectMapper.readValue(is, apiResponseType);
            if ("fail".equals(apiResponse.getStatus())) {
                throw new ShipmentServiceException(ErrorCode.EXTERNAL_API_ERROR);
            }

            return apiResponse.getData();
        }
    }
}
