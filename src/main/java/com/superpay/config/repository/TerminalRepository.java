package com.superpay.config.repository;

import com.superpay.config.entity.TerminalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TerminalRepository extends JpaRepository<TerminalEntity, String> {

    @Query(value = "SELECT * FROM terminals t WHERE t.id = :idOrName OR t.name = :idOrName LIMIT 1", nativeQuery = true)
    TerminalEntity findByIdOrName(@Param("idOrName") String idOrName);
}
