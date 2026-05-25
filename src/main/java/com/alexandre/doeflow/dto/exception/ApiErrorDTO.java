package com.alexandre.doeflow.dto.exception;


public record ApiErrorDTO(
        int status,
        String error,
        String message
){}