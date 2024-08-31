package com.superpay.config.repository;

import com.superpay.config.entity.TerminalConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerminalConfigRepository extends JpaRepository<TerminalConfigEntity, String> {
    // Método que busca configuraciones por terminalId
    List<TerminalConfigEntity> findAllByTerminalId(String terminalId);
    // Añadir método para borrar por terminalId y configKey
    void deleteByTerminalIdAndCode(String terminalId, String code);
}