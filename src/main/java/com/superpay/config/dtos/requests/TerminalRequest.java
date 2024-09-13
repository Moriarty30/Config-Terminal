package com.superpay.config.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerminalRequest {
    @JsonProperty(value = "code")
    private String code;
    @JsonProperty(value = "enabled")
    private Boolean enabled;
    @JsonProperty(value = "commerceId")
    private String commerceId;
    @JsonProperty(value = "paymentMethods")
    private List<String> paymentMethods;
    @JsonProperty(value = "configs")
    private List<String> configs;
}