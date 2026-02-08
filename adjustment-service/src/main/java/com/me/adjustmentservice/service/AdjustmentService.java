package com.me.adjustmentservice.service;

import com.me.adjustmentservice.controller.dto.AdjustmentRequest;
import com.me.adjustmentservice.controller.dto.AdjustmentResponse;

import java.util.List;

public interface AdjustmentService {

    List<AdjustmentResponse> findAdjustments();

    AdjustmentResponse findAdjustment(Long adjustmentId);

    AdjustmentResponse create(AdjustmentRequest request);
}
