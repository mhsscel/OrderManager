package com.genesis.studio.sibs.ordermanager.repository;

import com.genesis.studio.sibs.ordermanager.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
