package com.me.adjustmentservice.client;

import com.me.adjustmentservice.client.config.FeignClientConfig;
import com.me.adjustmentservice.client.dto.LocationResponse;
import com.me.adjustmentservice.client.dto.SkuResponse;
import com.me.adjustmentservice.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "warepulse", configuration = FeignClientConfig.class)
public interface WarepulseClient {

    @GetMapping("/skus/{skuId}")
    SkuResponse getSku(@PathVariable("skuId") Long skuId);

    @GetMapping("/locations/{locationId}")
    LocationResponse getLocation(@PathVariable("locationId") Long locationId);

    @GetMapping("/users/{userId}")
    UserResponse getUser(@PathVariable("userId") Long userId);
}
