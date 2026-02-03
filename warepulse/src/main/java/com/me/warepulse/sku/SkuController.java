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
@RequestMapping("/skus")
public class SkuController {

    private final SkuService skuService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<SkuResponse>> createSku(@Valid @RequestBody SkuRequest request) {
        SkuResponse response = skuService.createSku(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SkuResponse>>> findSkus() {
        List<SkuResponse> skus = skuService.findSkus();
        return ResponseEntity.ok(ApiResponse.success(skus));
    }

    @GetMapping("/{skuId}")
    public ResponseEntity<ApiResponse<SkuResponse>> findSkuById(@PathVariable("skuId") Long skuId) {
        SkuResponse sku = skuService.findSkuById(skuId);
        return ResponseEntity.ok(ApiResponse.success(sku));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{skuId}")
    public ResponseEntity<ApiResponse> deleteSku(@PathVariable("skuId") Long skuId) {
        skuService.deleteSku(skuId);
        return ResponseEntity.ok(ApiResponse.successWithNoContent());
    }
}
