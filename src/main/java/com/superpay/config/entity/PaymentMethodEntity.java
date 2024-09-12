package com.superpay.config.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity(name = "payment_methods")
public class PaymentMethodEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id")
    private String id;;
    @Column(name = "name")
    @Builder.Default private String name = "Unnamed";
    @Column(name = "code")
    @Builder.Default private String code = "00000";
    @Column(name = "enabled")
    @Builder.Default private Boolean enabled = false;
    @Column(name = "url_logo")
    private String urlLogo;
    @Column(name = "created_at")
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToMany(mappedBy = "paymentMethods")
    private Set<TerminalEntity> terminals = new HashSet<>();
}

