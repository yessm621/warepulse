package com.me.receiveservice.client.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.receiveservice.exception.ApiResponse;
import com.me.receiveservice.exception.ErrorCode;
import com.me.receiveservice.exception.ReceiveServiceException;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;

import java.io.IOException;
import java.lang.reflect.Type;

public class FeignResponseDecoder implements Decoder {

    private final Decoder delegate;
    private final ObjectMapper objectMapper;

    public FeignResponseDecoder(Decoder delegate, ObjectMapper objectMapper) {
        this.delegate = delegate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        // 실제 API 응답 타입이 ApiResponse<T>인 경우 처리
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, (Class<?>) type);
        ApiResponse<?> apiResponse = (ApiResponse<?>) delegate.decode(response, javaType);

        if ("fail".equals(apiResponse.getStatus())) {
            throw new ReceiveServiceException(ErrorCode.EXTERNAL_API_ERROR);
        }

        return apiResponse.getData(); // T (SkuResponse 등)만 반환
    }
}
