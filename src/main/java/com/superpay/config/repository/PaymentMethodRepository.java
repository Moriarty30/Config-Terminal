package com.superpay.config.repository;

import com.superpay.config.entity.PaymentMethodEntity;
import com.superpay.config.entity.TerminalPaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, String> {
    /*
    @Query(value = "select pm.* from terminals_payment_methods tpm " +
            "left join terminals t on tpm.terminal_id = t.id " +
            "left join payment_methods pm on tpm.payment_method_id = pm.id " +
            "where t.code = (:terminalId) and t.enabled = true and pm.enabled = true", nativeQuery = true)


     */
    @Query(value = "SELECT * FROM payment_methods WHERE id IN (:ids)", nativeQuery = true)
    List<PaymentMethodEntity> getTerminalPaymentMethods(@Param("ids") List<String> ids);

    @Query(value = "select * from payment_methods where id in (id) or code in (id)", nativeQuery = true)
    PaymentMethodEntity getPaymentMethodEntitiesByid(@Param("id") String id);
}
