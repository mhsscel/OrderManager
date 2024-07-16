package com.genesis.studio.sibs.ordermanager.repository;

import com.genesis.studio.sibs.ordermanager.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
}
