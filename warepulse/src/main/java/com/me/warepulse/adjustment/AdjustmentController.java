package com.me.warepulse.adjustment;

import com.me.warepulse.adjustment.dto.AdjustmentRequest;
import com.me.warepulse.adjustment.dto.AdjustmentResponse;
import com.me.warepulse.exception.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/adjustments")
public class AdjustmentController {

    private final AdjustmentService adjustmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdjustmentResponse>>> findAdjustments() {
        List<AdjustmentResponse> adjustments = adjustmentService.findAdjustments();
        return ResponseEntity.ok(ApiResponse.success(adjustments));
    }

    @GetMapping("/{adjustmentId}")
    public ResponseEntity<ApiResponse<AdjustmentResponse>> findAdjustment(@PathVariable("adjustmentId") Long adjustmentId) {
        AdjustmentResponse adjustment = adjustmentService.findAdjustment(adjustmentId);
        return ResponseEntity.ok(ApiResponse.success(adjustment));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<AdjustmentResponse>> createAdjustment(@RequestBody AdjustmentRequest request) {
        AdjustmentResponse adjustment = adjustmentService.create(request);
        return ResponseEntity.ok(ApiResponse.success(adjustment));
    }
}
