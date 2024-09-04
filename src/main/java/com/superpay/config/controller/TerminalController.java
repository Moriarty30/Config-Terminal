package com.superpay.config.controller;

import com.superpay.config.dtos.ByIds;
import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.service.TerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/terminals")
public class TerminalController {

    private final TerminalService terminalService;

    @Autowired
    public TerminalController(TerminalService terminalService) {
        this.terminalService = terminalService;
    }

    @PostMapping
    public ResponseEntity<TerminalDTO> createTerminal(@RequestBody TerminalDTO terminalDTO) {
        TerminalDTO createdTerminal = terminalService.createOrUpdateTerminal(terminalDTO);
        return ResponseEntity.ok(createdTerminal);
    }

    @PostMapping("/by-ids")
    public ResponseEntity<List<TerminalDTO>> getTerminalsByIds(@RequestBody ByIds idsRequest) {
        Set<String> ids = (Set<String>) idsRequest.getIds();
        List<TerminalDTO> terminals = terminalService.getTerminalsByIds(ids);
        return ResponseEntity.ok(terminals);
    }
}
