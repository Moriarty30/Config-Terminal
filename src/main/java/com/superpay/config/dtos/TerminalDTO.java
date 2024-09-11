package com.superpay.config.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TerminalDTO {
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "code")
    private String code;
    @JsonProperty(value = "enabled")
    private Boolean enabled;
    @JsonProperty(value = "commerceId")
    private String merchantId;
    @JsonProperty(value = "paymentMethods")
    private List<PaymentMethodDTO> paymentMethods;
    @JsonProperty(value = "configs")
    private List<ConfigTerminalDTO> configs;




}
