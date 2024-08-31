package com.superpay.config.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "terminal_configs")
@Getter
@Setter
public class TerminalConfigEntity {

    @Id
    private String terminalId;
    private String code;
    private String configValue;
}