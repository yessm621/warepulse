package com.me.inventoryservice.service;

import com.me.inventoryservice.messagequeue.dto.ReceiveDto;
import com.me.inventoryservice.messagequeue.dto.ShipmentDto;
import com.me.inventoryservice.messagequeue.dto.AdjustmentDto;

public interface InventoryEventService {

    void receive(ReceiveDto dto);

    void shipment(ShipmentDto dto);

    void reserve(ShipmentDto dto);

    void release(ShipmentDto dto);

    void adjustment(AdjustmentDto dto);
}
