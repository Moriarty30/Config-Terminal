package com.superpay.config.repository;

import com.superpay.config.entity.ConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends JpaRepository<ConfigEntity, String>, JpaSpecificationExecutor {
    @Query(value = "select * from config LIMIT 1", nativeQuery = true)
    ConfigEntity getConfig();
}
