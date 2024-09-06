package com.superpay.config.repository;

import com.superpay.config.entity.TerminalPaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TerminalPaymentMethodRepository extends JpaRepository<TerminalPaymentMethodEntity, String>, JpaSpecificationExecutor {

    @Query(value = "select * from terminals_payment_methods where terminal_id in (:terminalsIds)", nativeQuery = true)
    List<TerminalPaymentMethodEntity> getTerminalPaymentMethodsByTerminalsIds(@Param("terminalsIds") List<String> terminalsIds);
}

