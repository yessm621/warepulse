package com.me.warepulse.sku;

import com.me.warepulse.sku.dto.SkuRequest;
import com.me.warepulse.sku.dto.SkuResponse;

import java.util.List;

public interface SkuService {

    SkuResponse createSku(SkuRequest request);

    List<SkuResponse> findSkus();

    SkuResponse findSkuById(Long skuId);

    void deleteSku(Long skuId);
}
