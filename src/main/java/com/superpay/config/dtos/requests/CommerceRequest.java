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
public class CommerceRequest {
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "nit")
    private String nit;
    @JsonProperty(value = "enabled")
    private Boolean enabled;
    @JsonProperty(value = "address")
    private String address;
}
