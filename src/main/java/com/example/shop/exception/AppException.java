package com.example.shop.exception;

import lombok.Data;

@Data
public class AppException extends RuntimeException {

    private ErrorResponse errorResponse;

    public AppException(ErrorResponse error)
    {
        super(error.getMessage());
        this.errorResponse = error;
    }
}
