package com.me.warepulse.inventory.service;

import com.me.warepulse.adjustment.AdjustmentInventoryDto;
import com.me.warepulse.inventory.service.dto.*;

public interface InventoryEventService {

    void receive(IncreaseInventoryDto dto);

    void shipment(DecreaseInventoryDto dto);

    void reserve(ReserveInventoryDto dto);

    void release(ReleaseInventoryDto dto);

    void adjustment(AdjustmentInventoryDto dto);
}
