package com.me.inventoryservice.service;

import com.me.inventoryservice.service.dto.*;

public interface InventoryEventService {

    void receive(IncreaseInventoryDto dto);

    void shipment(DecreaseInventoryDto dto);

    void reserve(ReserveInventoryDto dto);

    void release(ReleaseInventoryDto dto);

    void adjustment(AdjustmentInventoryDto dto);
}
