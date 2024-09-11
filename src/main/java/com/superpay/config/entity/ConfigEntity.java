package com.superpay.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity(name = "config")
public class ConfigEntity {
    @Id
    @Column(name = "id")
    @Builder.Default private String id = UUID.randomUUID().toString();
    @Column(name = "collaborator_portal_external_id_seq")
    private Integer collaboratorPortalExternalIdSeq;
}
