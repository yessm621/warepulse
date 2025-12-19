package com.me.warepulse.repository;

import com.me.warepulse.entity.InventoryEvents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryEventRepository extends JpaRepository<InventoryEvents, Long> {
}
