package com.me.warepulse.inventory.service;

import com.me.warepulse.inventory.dto.IncreaseInventoryDto;

public interface InventoryEventService {

    void receive(IncreaseInventoryDto request);

    /* todo::
        /inventories/increase - POST, 재고 증가
        /inventories/decrease - POST, 재고 감소
        /inventories/moved - POST, 재고 이동
        /inventories/adjusted - POST, 재고 검수
        /inventories/reserve - POST, 재고 예약
        /inventories/release - POST, 재고 예약 해제
        /inventories/increase - PUT/PATCH, 재고 증가
    */
}
