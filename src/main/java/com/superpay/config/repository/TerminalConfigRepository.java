package com.superpay.config.repository;

import com.superpay.config.entity.TerminalConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerminalConfigRepository extends JpaRepository<TerminalConfigEntity, String> {

    // Método que busca configuraciones por el ID de la terminal
    List<TerminalConfigEntity> findAllByTerminalEntity_Id(String terminalId);

    // Método corregido para borrar por terminalId y code
    void deleteByTerminalEntity_IdAndCode(String terminalId, String code);
}
