package com.genesis.studio.sibs.ordermanager.repository;

import com.genesis.studio.sibs.ordermanager.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByItemId(Long itemId);
}
