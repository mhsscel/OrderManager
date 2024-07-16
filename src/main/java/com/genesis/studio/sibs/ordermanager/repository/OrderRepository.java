package com.genesis.studio.sibs.ordermanager.repository;

import com.genesis.studio.sibs.ordermanager.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByItemIdAndCompletedFalse(Long itemId);
}

