package com.wefox.kafka.consumerapi.model.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMicroserviceResponse {
    private String statusCode;
    private String statusMessage;
    private String timestamp;
    private Object data;

    public TransactionMicroserviceResponse(String statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.timestamp = LocalDateTime.now().toString();
    }
}