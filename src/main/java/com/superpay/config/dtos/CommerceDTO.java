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
public class CommerceDTO {
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "nit")
    private String nit;
    @JsonProperty(value = "enabled")
    private Boolean enabled;
    @JsonProperty(value = "address")
    private String address;
    @JsonProperty(value = "terminals")
    private List<ConfigTerminalDTO> terminals;
}

