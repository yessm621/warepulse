package com.me.warepulse.repository;

import com.me.warepulse.entity.InventoryEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryEventRepository extends JpaRepository<InventoryEvent, Long> {
}
