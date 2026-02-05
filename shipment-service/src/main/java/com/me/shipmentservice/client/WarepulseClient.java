package com.me.shipmentservice.client;

import com.me.shipmentservice.client.config.FeignClientConfig;
import com.me.shipmentservice.client.dto.LocationResponse;
import com.me.shipmentservice.client.dto.SkuResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "warepulse", configuration = FeignClientConfig.class)
public interface WarepulseClient {

    @GetMapping("/skus/{skuId}")
    SkuResponse getSku(@PathVariable("skuId") Long skuId);

    @GetMapping("/locations/{locationId}")
    LocationResponse getLocation(@PathVariable("locationId") Long locationId);
}
