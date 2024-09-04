package com.superpay.config.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TerminalDTO {
    private String id;
    private String code;
    private Boolean enabled;
    private String merchantId;
    private LocalDateTime createdAt;
    private Set<PaymentMethodDTO> paymentMethods;
    private Set<TerminalConfigDTO> configs;
}
