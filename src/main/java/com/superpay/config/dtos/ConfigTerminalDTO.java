package com.superpay.config.dtos;

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
    private String id;
    private String terminalId;
    private String code;
    private String type;
    private String value;
    private LocalDateTime createdAt;
    private LocalDateTime createdAtTz;
    private String tag;

}
