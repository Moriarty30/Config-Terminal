package com.superpay.config.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodRequest {
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "code")
    private String code;
    @JsonProperty(value = "enabled")
    private Boolean enabled;
    @JsonProperty(value = "urlLogo")
    private String urlLogo;
}