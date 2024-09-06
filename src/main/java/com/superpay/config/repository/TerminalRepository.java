package com.superpay.config.repository;

import com.superpay.config.entity.TerminalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TerminalRepository extends JpaRepository<TerminalEntity, String> {
    @Query(value = "select * from terminals where id = :terminalIdOrCode or code = :terminalIdOrCode order by created_at desc limit 1", nativeQuery = true)
    TerminalEntity getTerminalByIdOrCode(@Param("terminalIdOrCode") String terminalIdOrCode);

    @Query(value = "select * from terminals where id in (:terminalIdsOrCodes) or code in (:terminalIdsOrCodes) order by created_at desc", nativeQuery = true)
    List<TerminalEntity> getTerminalsByIdsOrCodes(@Param("terminalIdsOrCodes") List<String> terminalIdsOrCodes);

}