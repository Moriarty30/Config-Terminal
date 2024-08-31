package com.superpay.config;

import com.superpay.config.entity.TerminalConfigEntity;
import com.superpay.config.repository.TerminalConfigRepository;
import com.superpay.config.service.TerminalConfigService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TerminalConfigServiceTests {

    @Mock
    private TerminalConfigRepository terminalConfigRepository;

    @InjectMocks
    private TerminalConfigService terminalConfigService;

    public TerminalConfigServiceTests() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetConfigsForTerminal() {
        List<TerminalConfigEntity> configList = new ArrayList<>();
        configList.add(new TerminalConfigEntity());

        when(terminalConfigRepository.findAllByTerminalId("terminal-1")).thenReturn(configList);

        List<TerminalConfigEntity> result = terminalConfigService.getConfigsForTerminal("terminal-1");
        assertEquals(1, result.size());
        verify(terminalConfigRepository, times(1)).findAllByTerminalId("terminal-1");
    }

    @Test
    void testCreateOrUpdateConfig() {
        TerminalConfigEntity config = new TerminalConfigEntity();
        config.setTerminalId("terminal-1");

        when(terminalConfigRepository.save(config)).thenReturn(config);

        TerminalConfigEntity savedConfig = terminalConfigService.createOrUpdateConfig(config);
        assertEquals("terminal-1", savedConfig.getTerminalId());
        verify(terminalConfigRepository, times(1)).save(config);
    }

    @Test
    void testDeleteConfig() {
        terminalConfigService.deleteConfig("terminal-1", "config-key");
        verify(terminalConfigRepository, times(1)).deleteByTerminalIdAndCode("terminal-1", "config-key");
    }
}
