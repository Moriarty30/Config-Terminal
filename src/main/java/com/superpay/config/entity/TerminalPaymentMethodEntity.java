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

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Entity(name = "terminals_payment_methods")
public class TerminalPaymentMethodEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id")
    private String id;
    @Column(name = "terminal_id")
    private String terminalId;
    @Column(name = "payment_method_id")
    private String paymentMethodId;
}
