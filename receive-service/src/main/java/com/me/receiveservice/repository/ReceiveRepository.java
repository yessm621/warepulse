package com.me.receiveservice.repository;

import com.me.receiveservice.entity.Receive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiveRepository extends JpaRepository<Receive, Long> {
}
