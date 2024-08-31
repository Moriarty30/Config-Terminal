package com.superpay.config.controller;

import com.superpay.config.dtos.ByIds;
import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.dtos.requests.CreateTerminalRequest;
import com.superpay.config.entity.TerminalEntity;
import com.superpay.config.exception.CustomException;
import com.superpay.config.service.TerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terminals")
public class TerminalController {

    @Autowired
    private TerminalService terminalService;

    @GetMapping
    public List<TerminalEntity> getAllTerminals() {
        return terminalService.getAllTerminals();
    }

    @PostMapping("/by-ids")
    public List<TerminalEntity> getTerminalsByIdsorCodes(@RequestBody ByIds ids) {
        return terminalService.getTerminalsByIds(ids.getIds());
    }

    @GetMapping("/by-id/{id}")
    public TerminalEntity getTerminalByIdorCode(@PathVariable("id") String id) throws CustomException {
        return terminalService.getTerminalById(id);
    }

    @PostMapping
    public TerminalDTO createOrUpdateTerminal(@RequestBody CreateTerminalRequest createTerminalRequest) {
        return terminalService.createOrUpdateTerminal(createTerminalRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTerminal(@PathVariable String id) {
        terminalService.deleteTerminal(id);
        return ResponseEntity.noContent().build();
    }
}
