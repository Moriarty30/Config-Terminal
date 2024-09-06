package com.superpay.config.repository;

import com.superpay.config.entity.TerminalConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerminalConfigRepository extends JpaRepository<TerminalConfigEntity, String> {

    @Query(value = "select * from terminals_configs where terminal_id = (:terminalId) order by created_at desc", nativeQuery = true)
    List<TerminalConfigEntity> getTerminalConfigs(@Param("terminalId") String terminalId);
}