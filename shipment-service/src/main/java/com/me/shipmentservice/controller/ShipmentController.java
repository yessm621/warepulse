package com.me.shipmentservice.controller;

import com.me.shipmentservice.config.CustomUserDetails;
import com.me.shipmentservice.controller.dto.ShipmentPickingRequest;
import com.me.shipmentservice.controller.dto.ShipmentRequest;
import com.me.shipmentservice.controller.dto.ShipmentResponse;
import com.me.shipmentservice.exception.ApiResponse;
import com.me.shipmentservice.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShipmentResponse>>> findShipments() {
        List<ShipmentResponse> shipments = shipmentService.findShipments();
        return ResponseEntity.ok(ApiResponse.success(shipments));
    }

    @GetMapping("/{shipmentId}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> findShipments(@PathVariable("shipmentId") Long shipmentId) {
        ShipmentResponse shipment = shipmentService.findShipment(shipmentId);
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(@RequestBody ShipmentRequest request) {
        ShipmentResponse shipment = shipmentService.createShipment(request);
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }

    @PatchMapping("/{shipmentId}/picking")
    public ResponseEntity<ApiResponse<ShipmentResponse>> pickingShipment(
            @PathVariable("shipmentId") Long shipmentId,
            @RequestBody ShipmentPickingRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        ShipmentResponse shipment = shipmentService.pickingShipment(shipmentId, request.getPickedQty(), user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }

    @PatchMapping("/{shipmentId}/shipped")
    public ResponseEntity<ApiResponse<ShipmentResponse>> shippedShipment(
            @PathVariable("shipmentId") Long shipmentId, @AuthenticationPrincipal CustomUserDetails user) {
        ShipmentResponse shipment = shipmentService.shippedShipment(shipmentId, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }

    @PatchMapping("/{shipmentId}/canceled")
    public ResponseEntity<ApiResponse<ShipmentResponse>> canceledShipment(@PathVariable("shipmentId") Long shipmentId) {
        ShipmentResponse shipment = shipmentService.canceledShipment(shipmentId);
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }
}
