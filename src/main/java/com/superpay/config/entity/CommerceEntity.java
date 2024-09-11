package com.superpay.config.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity(name = "commerces")
public class CommerceEntity {
    @Id
    @Column(name = "id")
    @Builder.Default private String id = UUID.randomUUID().toString();
    @Column(name = "name")
    @Builder.Default private String name = "Unnamed";
    @Column(name = "nit")
    @Builder.Default private String nit = "00000";
    @Column(name = "enabled")
    @Builder.Default private Boolean enabled = false;
    @Column(name = "address")
    private String address;
    @Column(name = "created_at")
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
    @OneToMany(mappedBy = "commerceEntity", fetch = FetchType.LAZY)
    private List<TerminalEntity> terminals;
}