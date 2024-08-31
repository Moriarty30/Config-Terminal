package com.superpay.config.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TerminalDTO {
    private String id;
    private String name;
    private String location;
    private boolean active;
}
