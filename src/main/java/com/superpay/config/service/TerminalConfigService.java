package com.superpay.config.service;

import com.superpay.config.dtos.ConfigTerminalDTO;
import com.superpay.config.dtos.requests.ConfigTerminalRequest;
import com.superpay.config.dtos.requests.TerminalRequest;
import com.superpay.config.entity.TerminalEntity;

import com.superpay.config.mappers.TerminalConfigMapper;
import com.superpay.config.mappers.TerminalMapper;
import com.superpay.config.repository.TerminalRepository;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service

public class TerminalConfigService {

    @Autowired
    TerminalRepository terminalRepository;

    @Autowired
    TerminalMapper terminalMapper;

    @Autowired
    TerminalConfigMapper terminalConfigMapper;


    public ConfigTerminalDTO createOrUpdateTerminal(ConfigTerminalRequest configTerminalRequest) {
        TerminalEntity terminalEntity = this.terminalConfigMapper.map(configTerminalRequest);

        return;
    }
}