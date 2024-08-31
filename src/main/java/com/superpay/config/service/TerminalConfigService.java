package com.superpay.config.service;

import com.superpay.config.entity.TerminalConfigEntity;
import com.superpay.config.repository.TerminalConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TerminalConfigService {

    private final TerminalConfigRepository terminalConfigRepository;

    public TerminalConfigService(TerminalConfigRepository terminalConfigRepository) {
        this.terminalConfigRepository = terminalConfigRepository;
    }

    public List<TerminalConfigEntity> getConfigsForTerminal(String terminalId) {
        return terminalConfigRepository.findAllByTerminalId(terminalId);
    }

    public TerminalConfigEntity createOrUpdateConfig(TerminalConfigEntity config) {
        return terminalConfigRepository.save(config);
    }

    public void deleteConfig(String terminalId, String code) {
        terminalConfigRepository.deleteByTerminalIdAndCode(terminalId, code);
    }
}