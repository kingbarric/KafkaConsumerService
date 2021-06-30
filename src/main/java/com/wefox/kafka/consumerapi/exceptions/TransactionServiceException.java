package com.wefox.kafka.consumerapi.exceptions;

import lombok.Data;
@Data
public class TransactionServiceException extends RuntimeException {
    private final Integer httpCode;
    private String statusCode;

    public TransactionServiceException(Integer httpCode, String message, String statusCode) {
        super(message);
        this.httpCode = httpCode;
        this.statusCode = statusCode;
    }
}