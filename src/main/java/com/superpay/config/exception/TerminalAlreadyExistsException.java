package com.superpay.config.exception;

public class TerminalAlreadyExistsException extends RuntimeException {
    public TerminalAlreadyExistsException(String message) {
        super(message);
    }
}
