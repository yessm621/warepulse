package com.me.warepulse.shipment;

import com.me.warepulse.exception.ApiResponse;
import com.me.warepulse.shipment.dto.ShipmentPickingRequest;
import com.me.warepulse.shipment.dto.ShipmentRequest;
import com.me.warepulse.shipment.dto.ShipmentResponse;
import com.me.warepulse.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping("/shipments")
    public ResponseEntity<ApiResponse<List<ShipmentResponse>>> findShipments() {
        List<ShipmentResponse> shipments = shipmentService.findShipments();
        return ResponseEntity.ok(ApiResponse.success(shipments));
    }

    @GetMapping("/shipments/{shipmentId}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> findShipments(@PathVariable("shipmentId") Long shipmentId) {
        ShipmentResponse shipment = shipmentService.findShipment(shipmentId);
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }

    @PostMapping("/shipments")
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(@RequestBody ShipmentRequest request) {
        ShipmentResponse shipment = shipmentService.createShipment(request);
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }

    @PatchMapping("/shipments/{shipmentId}/picking")
    public ResponseEntity<ApiResponse<ShipmentResponse>> pickingShipment(
            @PathVariable("shipmentId") Long shipmentId,
            @RequestBody ShipmentPickingRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        ShipmentResponse shipment = shipmentService.pickingShipment(shipmentId, request.getPickedQty(), user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }

    @PatchMapping("/shipments/{shipmentId}/shipped")
    public ResponseEntity<ApiResponse<ShipmentResponse>> shippedShipment(
            @PathVariable("shipmentId") Long shipmentId, @AuthenticationPrincipal CustomUserDetails user) {
        ShipmentResponse shipment = shipmentService.shippedShipment(shipmentId, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }

    @PatchMapping("/shipments/{shipmentId}/canceled")
    public ResponseEntity<ApiResponse<ShipmentResponse>> canceledShipment(@PathVariable("shipmentId") Long shipmentId) {
        ShipmentResponse shipment = shipmentService.canceledShipment(shipmentId);
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }
}
