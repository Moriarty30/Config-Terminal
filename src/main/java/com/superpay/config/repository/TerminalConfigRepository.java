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
    

    @Query(value = "select * from terminals_configs where terminal_id = :terminalId and code = :code limit 1", nativeQuery = true)
    TerminalConfigEntity findByTerminalIdAndCode(@Param("terminalId") String terminalId, @Param("code") String code);
}
