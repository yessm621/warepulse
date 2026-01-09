package com.me.warepulse.receive;

import com.me.warepulse.exception.ApiResponse;
import com.me.warepulse.receive.dto.ReceiveInspectedRequest;
import com.me.warepulse.receive.dto.ReceiveRequest;
import com.me.warepulse.receive.dto.ReceiveResponse;
import com.me.warepulse.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReceiveController {

    private final ReceiveService receiveService;

    @GetMapping("/receives")
    public ResponseEntity<ApiResponse<List<ReceiveResponse>>> findReceives() {
        List<ReceiveResponse> receives = receiveService.findReceives();
        return ResponseEntity.ok(ApiResponse.success(receives));
    }

    @GetMapping("/receives/{receiveId}")
    public ResponseEntity<ApiResponse<ReceiveResponse>> findReceive(@PathVariable("receiveId") Long receiveId) {
        ReceiveResponse receive = receiveService.findReceive(receiveId);
        return ResponseEntity.ok(ApiResponse.success(receive));
    }

    @PostMapping("/receives")
    public ResponseEntity<ApiResponse<ReceiveResponse>> createReceive(@RequestBody ReceiveRequest request) {
        ReceiveResponse receive = receiveService.createReceive(request);
        return ResponseEntity.ok(ApiResponse.success(receive));
    }

    @PatchMapping("/receives/{receiveId}/inspected")
    public ResponseEntity<ApiResponse<ReceiveResponse>> inspectedReceive(
            @PathVariable("receiveId") Long receiveId,
            @RequestBody ReceiveInspectedRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        ReceiveResponse receive = receiveService.inspectedReceive(receiveId, user.getUsername(), request.getReceivedQty());
        return ResponseEntity.ok(ApiResponse.success(receive));
    }

    @PatchMapping("/receives/{receiveId}/completed")
    public ResponseEntity<ApiResponse<ReceiveResponse>> completedReceive(
            @PathVariable("receiveId") Long receiveId, @AuthenticationPrincipal CustomUserDetails user) {
        ReceiveResponse receive = receiveService.completedReceive(receiveId, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(receive));
    }
}
