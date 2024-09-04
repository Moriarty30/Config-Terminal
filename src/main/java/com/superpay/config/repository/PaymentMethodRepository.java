package com.superpay.config.repository;

import com.superpay.config.entity.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, String> {
    /*
    // Método que busca métodos de pago por el ID de la terminal
    List<PaymentMethodEntity> findAllByTerminalEntity_Id(String terminalId);

    // Método corregido para borrar por terminalId y code
    void deleteByTerminalEntity_IdAndCode(String terminalId, String code);

     */
}
