package com.superpay.config.controller;

import com.superpay.config.entity.TerminalConfigEntity;
import com.superpay.config.service.TerminalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terminal-configs")
public class TerminalConfigController {

    @Autowired
    private TerminalConfigService terminalConfigService;

    @GetMapping("/{terminalId}")
    public List<TerminalConfigEntity> getConfigsForTerminal(@PathVariable String terminalId) {
        return terminalConfigService.getConfigsForTerminal(terminalId);
    }

    @PostMapping
    public TerminalConfigEntity createOrUpdateConfig(@RequestBody TerminalConfigEntity config) {
        return terminalConfigService.createOrUpdateConfig(config);
    }

    @DeleteMapping("/{terminalId}/{configKey}")
    public ResponseEntity<Void> deleteConfig(@PathVariable String terminalId, @PathVariable String code) {
        terminalConfigService.deleteConfig(terminalId, code);
        return ResponseEntity.noContent().build();
    }
}