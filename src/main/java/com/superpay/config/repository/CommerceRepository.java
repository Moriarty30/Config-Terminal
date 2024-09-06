package com.superpay.config.repository;

import com.superpay.config.entity.CommerceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommerceRepository extends JpaRepository<CommerceEntity, String> {

    List<CommerceEntity> findByIdIn (List<String> ids);

    @Query(value = "select * from commerces where id = :commerceIdOrNit or nit = :commerceIdOrNit order by created_at desc limit 1", nativeQuery = true)
    CommerceEntity getCommerceByIdOrNit(@Param("commerceIdOrNit") String commerceIdOrNit);
}
