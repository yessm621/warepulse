package com.me.warepulse.shipment;

import com.me.warepulse.shipment.dto.ShipmentRequest;
import com.me.warepulse.shipment.dto.ShipmentResponse;

import java.util.List;

public interface ShipmentService {

    List<ShipmentResponse> findShipments();

    ShipmentResponse findShipment(Long shipmentId);

    ShipmentResponse createShipment(ShipmentRequest request);

    ShipmentResponse pickingShipment(Long shipmentId, int pickedQty, String username);

    ShipmentResponse shippedShipment(Long shipmentId, String username);

    ShipmentResponse canceledShipment(Long shipmentId);
}
