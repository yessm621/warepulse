package com.me.warepulse.receiving;

import com.me.warepulse.exception.ApiResponse;
import com.me.warepulse.receiving.dto.ReceivingInspectedRequest;
import com.me.warepulse.receiving.dto.ReceivingRequest;
import com.me.warepulse.receiving.dto.ReceivingResponse;
import com.me.warepulse.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReceivingController {

    private final ReceivingService receivingService;

    @PostMapping("/receivings")
    public ResponseEntity<ApiResponse<ReceivingResponse>> createReceive(@RequestBody ReceivingRequest request) {
        ReceivingResponse receive = receivingService.createReceive(request);
        return ResponseEntity.ok(ApiResponse.success(receive));
    }

    @PatchMapping("/receivings/{receivingId}/inspected")
    public ResponseEntity<ApiResponse<ReceivingResponse>> inspectedReceive(
            @PathVariable("receivingId") Long receivingId,
            @RequestBody ReceivingInspectedRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        ReceivingResponse receive = receivingService.inspectedReceive(receivingId, user.getUsername(), request.getReceivedQty());
        return ResponseEntity.ok(ApiResponse.success(receive));
    }

    @PatchMapping("/receivings/{receivingId}/completed")
    public ResponseEntity<ApiResponse<ReceivingResponse>> completedReceive(
            @PathVariable("receivingId") Long receivingId, @AuthenticationPrincipal CustomUserDetails user) {
        ReceivingResponse receive = receivingService.completedReceive(receivingId, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(receive));
    }
}
