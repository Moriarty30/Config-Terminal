package com.superpay.config.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity(name = "terminals")
public class TerminalEntity {
    @Id
    @Column(name = "id")
    @Builder.Default private String id = UUID.randomUUID().toString();

    @Column(name = "code")
    @Builder.Default private String code = "00000";

    @Column(name = "enabled")
    @Builder.Default private Boolean enabled = false;

    @Column(name = "created_at")
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commerce_id", nullable = false)
    private CommerceEntity commerceEntity;

    @ManyToMany
    @JoinTable(
            name = "terminals_payment_methods",
            joinColumns = @JoinColumn(name = "terminal_id"),
            inverseJoinColumns = @JoinColumn(name = "payment_method_id"))
    private Set<PaymentMethodEntity> paymentMethods = new HashSet<>();

    @OneToMany(mappedBy = "terminalEntity", fetch = FetchType.LAZY)
    private Set<TerminalConfigEntity> configs = new HashSet<>();
}