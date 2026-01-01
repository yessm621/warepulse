package com.me.warepulse.inventory;

import com.me.warepulse.inventory.entity.InventoryEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryEventRepository extends JpaRepository<InventoryEvent, Long> {
}
