package com.superpay.config.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomException extends Exception {
    private String code;
    private String message;
    private HttpStatus httpStatus;
}
