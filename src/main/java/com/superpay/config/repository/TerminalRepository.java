package com.superpay.config.repository;

import com.superpay.config.entity.TerminalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TerminalRepository extends JpaRepository<TerminalEntity, String> {
}