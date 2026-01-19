package com.me.warepulse.adjustment;

import com.me.warepulse.adjustment.dto.AdjustmentRequest;
import com.me.warepulse.adjustment.dto.AdjustmentResponse;

import java.util.List;

public interface AdjustmentService {

    List<AdjustmentResponse> getAdjustments();

    AdjustmentResponse getAdjustment(Long adjustmentId);

    AdjustmentResponse create(AdjustmentRequest request);
}
