package com.me.warepulse.inventory.service;

import com.me.warepulse.inventory.service.dto.DecreaseInventoryDto;
import com.me.warepulse.inventory.service.dto.IncreaseInventoryDto;
import com.me.warepulse.inventory.service.dto.ReleaseInventoryDto;
import com.me.warepulse.inventory.service.dto.ReserveInventoryDto;

public interface InventoryEventService {

    void receive(IncreaseInventoryDto request);

    void shipment(DecreaseInventoryDto request);

    void reserve(ReserveInventoryDto request);

    void release(ReleaseInventoryDto request);
}
