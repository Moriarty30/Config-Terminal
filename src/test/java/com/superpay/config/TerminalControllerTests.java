package com.superpay.config;

import com.superpay.config.controller.TerminalController;
import com.superpay.config.entity.TerminalEntity;
import com.superpay.config.service.TerminalService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TerminalControllerTests {
    /*
    @Mock
    private TerminalService terminalService;

    @InjectMocks
    private TerminalController terminalController;

    public TerminalControllerTests() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTerminals() {
        List<TerminalEntity> terminalList = new ArrayList<>();
        when(terminalService.getAllTerminals()).thenReturn(terminalList);

        List<TerminalEntity> result = terminalController.getAllTerminals();
        assertNotNull(result);
        verify(terminalService, times(1)).getAllTerminals();
    }

    @Test
    void testGetTerminalById() {
        TerminalEntity terminal = new TerminalEntity();
        terminal.setId("terminal-1");

        when(terminalService.getTerminalById("terminal-1")).thenReturn(Optional.of(terminal));

        ResponseEntity<TerminalEntity> response = terminalController.getTerminalById("terminal-1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("terminal-1", response.getBody().getId());
    }

    @Test
    void testCreateOrUpdateTerminal() {
        TerminalEntity terminal = new TerminalEntity();
        terminal.setId("terminal-1");

        when(terminalService.createOrUpdateTerminal(terminal)).thenReturn(terminal);

        TerminalEntity result = terminalController.createOrUpdateTerminal(terminal);
        assertEquals("terminal-1", result.getId());
        verify(terminalService, times(1)).createOrUpdateTerminal(terminal);
    }

    @Test
    void testDeleteTerminal() {
        ResponseEntity<Void> response = terminalController.deleteTerminal("terminal-1");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(terminalService, times(1)).deleteTerminal("terminal-1");
    }
    */
}
