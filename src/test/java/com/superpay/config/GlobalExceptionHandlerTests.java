package com.superpay.config;

import com.superpay.config.exception.GlobalExceptionHandler;
import com.superpay.config.exception.ResourceNotFoundException;
import com.superpay.config.exception.TerminalAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");
        ResponseEntity<String> response = handler.handleResourceNotFoundException(exception);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", response.getBody());
    }

    @Test
    void testHandleTerminalAlreadyExistsException() {
        TerminalAlreadyExistsException exception = new TerminalAlreadyExistsException("Terminal already exists");
        ResponseEntity<String> response = handler.handleTerminalAlreadyExistsException(exception);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Terminal already exists", response.getBody());
    }

    @Test
    void testHandleGeneralException() {
        Exception exception = new Exception("General error");
        ResponseEntity<String> response = handler.handleGeneralException(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred: General error", response.getBody());
    }
}
