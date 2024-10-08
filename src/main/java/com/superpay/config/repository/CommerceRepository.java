package com.superpay.config.repository;

import com.superpay.config.entity.CommerceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CommerceRepository extends JpaRepository<CommerceEntity, String> {

    /*
      @Query("SELECT c FROM CommerceEntity c " +
           "LEFT JOIN FETCH c.terminals t " +
           "LEFT JOIN FETCH t.paymentMethods pm " +
           "LEFT JOIN FETCH t.configs tc " +
           "WHERE c.id = :commerceIdOrNit OR c.nit = :commerceIdOrNit")
           Select * from commerces LEFT JOIN FETCH terminals LEFT JOIN FETCH payment_methods LEFT JOIN FETCH terminal_configs WHERE id = :commerceIdOrNit or nit = :commerceIdOrNit
     */

    @Query(value = "select * from commerces where id = :commerceIdOrNit or nit = :commerceIdOrNit limit 1", nativeQuery = true)
    CommerceEntity getCommerceByIdOrNit(@Param("commerceIdOrNit") String commerceIdOrNit);

    @Query(value = "select * from commerces where id = :commerceIdOrNit or nit = :commerceIdOrNit", nativeQuery = true)
    List<CommerceEntity> getCommerceByNitorId(@Param("commerceIdOrNit") String commerceIdOrNit);

}
