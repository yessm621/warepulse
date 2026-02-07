package com.me.adjustmentservice.repository;

import com.me.adjustmentservice.entity.Adjustment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdjustmentRepository extends JpaRepository<Adjustment, Long> {
}
