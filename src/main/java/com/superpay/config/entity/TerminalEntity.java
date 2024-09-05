package com.superpay.config.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity(name = "terminals")
public class TerminalEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id")
    private String id;

    @Column(name = "code")
    @Builder.Default private String code = "00000";

    @Column(name = "enabled")
    @Builder.Default private Boolean enabled = false;

    @Column(name = "created_at")
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commerce_id", nullable = false)
    private CommerceEntity commerceEntity;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "terminals_payment_methods",
            joinColumns = @JoinColumn(name = "terminal_id"),
            inverseJoinColumns = @JoinColumn(name = "payment_method_id"))
    private Set<PaymentMethodEntity> paymentMethods = new HashSet<>();

    @OneToMany(mappedBy = "terminalEntity", fetch = FetchType.EAGER)
    private Set<TerminalConfigEntity> configs = new HashSet<>();
}