package com.superpay.config.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TerminalConfigDTO {
    private String id;
    private String terminalId;
    private String code;
    private String type;
    private String value;
    private LocalDateTime createdAt;
}
