package com.me.warepulse.sku;

import com.me.warepulse.exception.ApiResponse;
import com.me.warepulse.sku.dto.SkuRequest;
import com.me.warepulse.sku.dto.SkuResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SkuController {

    private final SkuService skuService;

    @PostMapping("/skus")
    public ResponseEntity<ApiResponse<SkuResponse>> createSku(@Valid @RequestBody SkuRequest request) {
        SkuResponse response = skuService.createSku(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/skus")
    public ResponseEntity<ApiResponse<List<SkuResponse>>> findSkus() {
        List<SkuResponse> skus = skuService.findSkus();
        return ResponseEntity.ok(ApiResponse.success(skus));
    }

    @GetMapping("/skus/{sku_id}")
    public ResponseEntity<ApiResponse<SkuResponse>> findSkuById(@PathVariable("sku_id") Long skuId) {
        SkuResponse sku = skuService.findSkuById(skuId);
        return ResponseEntity.ok(ApiResponse.success(sku));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/skus/{sku_id}")
    public ResponseEntity<ApiResponse> deleteSku(@PathVariable("sku_id") Long skuId) {
        skuService.deleteSku(skuId);
        return ResponseEntity.ok(ApiResponse.successWithNoContent());
    }
}
