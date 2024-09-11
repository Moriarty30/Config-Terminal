package com.superpay.config.controller;

import com.superpay.config.dtos.PaymentMethodDTO;
import com.superpay.config.entity.ConfigEntity;
import com.superpay.config.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

public class ConfigController {



    ConfigService configService;

    @GetMapping("/get-config")
    @ResponseStatus(HttpStatus.OK)
    public ConfigEntity getConfig() {
        return this.configService.getConfig();
    }

    @GetMapping("/increment-collaborator-portal-external-id-seq")
    @ResponseStatus(HttpStatus.OK)
    public ConfigEntity incrementCollaboratorPortalExternalIdSeq() {
        return this.configService.incrementCollaboratorPortalExternalIdSeq();
    }

    @GetMapping("/terminal-payment-methods/{terminalId}")
    @ResponseStatus(HttpStatus.OK)
    public List<PaymentMethodDTO> getTerminalPaymentMethods(@PathVariable String terminalId) {
        return this.configService.getTerminalPaymentMethods(terminalId);
    }
}
