package com.superpay.config.controller;

import com.superpay.config.dtos.ConfigTerminalDTO;
import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.dtos.requests.ConfigTerminalRequest;
import com.superpay.config.service.TerminalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/terminal-config")
public class TerminalConfigController {

    @Autowired
    private TerminalConfigService terminalConfigService;


    @PostMapping("/create-or-update")
    @ResponseStatus(HttpStatus.CREATED)
    public ConfigTerminalDTO createOrUpdateTerminalConfig(@RequestBody ConfigTerminalRequest request) {
        return terminalConfigService.createOrUpdateTerminalConfig(request);
    }

    // Endpoint para obtener una configuración de terminal por terminalId y código
    @GetMapping("/{IdorCode}")
    @ResponseStatus(HttpStatus.OK)
    public List<ConfigTerminalDTO> getTerminalConfig(@PathVariable String IdorCode) {
        return terminalConfigService.getConfigTerminal(IdorCode);
    }

    /*
    // Endpoint para obtener todas las configuraciones de un terminal por terminalId
    @GetMapping("/{terminalId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TerminalDTO> getConfigsByTerminalIdorCode(@PathVariable String terminalId) {
        return terminalConfigService.getTerminalsByConfigIdorCode(terminalId);
    }
    */


}
