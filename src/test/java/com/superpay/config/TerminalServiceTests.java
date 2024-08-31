package com.superpay.config;

import com.superpay.config.entity.TerminalEntity;
import com.superpay.config.repository.TerminalRepository;
import com.superpay.config.service.TerminalService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TerminalServiceTests {
    /*
    @Mock
    private TerminalRepository terminalRepository;

    @InjectMocks
    private TerminalService terminalService;

    public TerminalServiceTests() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTerminals() {
        terminalService.getAllTerminals();
        verify(terminalRepository, times(1)).findAll();
    }

    @Test
    void testGetTerminalById() {
        TerminalEntity terminal = new TerminalEntity();
        terminal.setId("terminal-1");

        when(terminalRepository.findById("terminal-1")).thenReturn(Optional.of(terminal));

        Optional<TerminalEntity> foundTerminal = terminalService.getTerminalById("terminal-1");
        assertTrue(foundTerminal.isPresent());
        assertEquals("terminal-1", foundTerminal.get().getId());
    }

    @Test
    void testCreateOrUpdateTerminal() {
        TerminalEntity terminal = new TerminalEntity();
        terminal.setId("terminal-1");

        when(terminalRepository.save(terminal)).thenReturn(terminal);

        TerminalEntity savedTerminal = terminalService.createOrUpdateTerminal(terminal);
        assertEquals("terminal-1", savedTerminal.getId());
        verify(terminalRepository, times(1)).save(terminal);
    }

    @Test
    void testDeleteTerminal() {
        String terminalId = "terminal-1";
        terminalService.deleteTerminal(terminalId);
        verify(terminalRepository, times(1)).deleteById(terminalId);
    }
    */
}
