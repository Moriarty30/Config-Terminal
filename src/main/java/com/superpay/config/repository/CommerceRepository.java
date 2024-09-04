package com.superpay.config.repository;

import com.superpay.config.entity.CommerceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommerceRepository extends JpaRepository<CommerceEntity, String> {


}
