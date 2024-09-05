package com.superpay.config.repository;

import com.superpay.config.entity.TerminalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface TerminalRepository extends JpaRepository<TerminalEntity, String> {
    List<TerminalEntity> findByIdIn(Collection<String> ids);  // Collection en vez de Set o List
}