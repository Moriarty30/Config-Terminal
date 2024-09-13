package com.superpay.config.controller;

import com.superpay.config.dtos.ByIds;
import com.superpay.config.dtos.ConfigTerminalDTO;
import com.superpay.config.dtos.TerminalDTO;
import com.superpay.config.dtos.requests.TerminalRequest;
import com.superpay.config.exception.CustomException;
import com.superpay.config.service.TerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/terminals")
public class TerminalController {

    @Autowired
    TerminalService terminalService;


    @PostMapping("/newTerminal")
    @ResponseStatus(HttpStatus.CREATED)
    public TerminalDTO savePaymentMethod(@RequestBody TerminalRequest terminalRequest) {
        return this.terminalService.createOrUpdateTerminal(terminalRequest);
    }


    @PostMapping("/by-ids")
    @ResponseStatus(HttpStatus.OK)
    public List<TerminalDTO> getTerminalsByIdsOrCodes(@RequestBody ByIds byIds) {
        return this.terminalService.getTerminalsByIdsOrCodes(byIds);
    }

}