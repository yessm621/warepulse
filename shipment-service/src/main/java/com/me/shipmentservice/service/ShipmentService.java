package com.me.shipmentservice.service;

import com.me.shipmentservice.controller.dto.ShipmentRequest;
import com.me.shipmentservice.controller.dto.ShipmentResponse;

import java.util.List;

public interface ShipmentService {

    List<ShipmentResponse> findShipments();

    ShipmentResponse findShipment(Long shipmentId);

    ShipmentResponse createShipment(ShipmentRequest request);

    ShipmentResponse pickingShipment(Long shipmentId, int pickedQty, String username);

    ShipmentResponse shippedShipment(Long shipmentId, String username);

    ShipmentResponse canceledShipment(Long shipmentId);
}
