package com.superpay.config.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigTerminalDTO {
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "terminalId")
    private String terminalId;
    @JsonProperty(value = "code")
    private String code;
    @JsonProperty(value = "type")
    private String type;
    @JsonProperty(value = "value")
    private String value;
    @JsonProperty(value = "created_at")
    private LocalDateTime createdAt;
    @JsonProperty(value = "created_at_tz")
    private LocalDateTime createdAtTz;
    //private String tag;

}
