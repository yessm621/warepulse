package com.me.warepulse.sku;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.sku.dto.SkuRequest;
import com.me.warepulse.sku.dto.SkuResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkuServiceImpl implements SkuService {

    private final SkuRepository skuRepository;

    @Transactional
    @Override
    public SkuResponse createSku(SkuRequest request) {
        Sku sku = Sku.create(request.getCode(), request.getName(), request.getType());
        skuRepository.save(sku);
        return SkuResponse.from(sku);
    }

    @Override
    public List<SkuResponse> findSkus() {
        return skuRepository.findAll()
                .stream()
                .map(SkuResponse::from)
                .toList();
    }

    @Override
    public SkuResponse findSkuById(Long skuId) {
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.SKU_NOT_FOUND));
        return SkuResponse.from(sku);
    }

    @Transactional
    @Override
    public void deleteSku(Long skuId) {
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.SKU_NOT_FOUND));
        skuRepository.delete(sku);
    }
}
