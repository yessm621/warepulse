package com.me.warepulse.repository;

import com.me.warepulse.entity.Inventories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventories, Long> {
}
