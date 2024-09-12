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
public class ConfigTerminalRequest {
    @JsonProperty(value = "code")
    private String code;

    @JsonProperty(value = "type")
    private String type;

    @JsonProperty(value = "value")
    private String value;

    @JsonProperty(value = "terminalId")
    private String terminalId;
}
