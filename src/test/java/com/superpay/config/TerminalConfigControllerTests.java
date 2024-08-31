package com.superpay.config;

import com.superpay.config.controller.TerminalConfigController;
import com.superpay.config.entity.TerminalConfigEntity;
import com.superpay.config.service.TerminalConfigService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TerminalConfigControllerTests {

    @Mock
    private TerminalConfigService terminalConfigService;

    @InjectMocks
    private TerminalConfigController terminalConfigController;

    public TerminalConfigControllerTests() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetConfigsForTerminal() {
        List<TerminalConfigEntity> configList = new ArrayList<>();
        when(terminalConfigService.getConfigsForTerminal("terminal-1")).thenReturn(configList);

        List<TerminalConfigEntity> result = terminalConfigController.getConfigsForTerminal("terminal-1");
        assertNotNull(result);
        verify(terminalConfigService, times(1)).getConfigsForTerminal("terminal-1");
    }

    @Test
    void testCreateOrUpdateConfig() {
        TerminalConfigEntity config = new TerminalConfigEntity();
        config.setTerminalId("terminal-1");

        when(terminalConfigService.createOrUpdateConfig(config)).thenReturn(config);

        TerminalConfigEntity result = terminalConfigController.createOrUpdateConfig(config);
        assertEquals("terminal-1", result.getTerminalId());
        verify(terminalConfigService, times(1)).createOrUpdateConfig(config);
    }

    @Test
    void testDeleteConfig() {
        ResponseEntity<Void> response = terminalConfigController.deleteConfig("terminal-1", "config-key");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(terminalConfigService, times(1)).deleteConfig("terminal-1", "config-key");
    }
}
