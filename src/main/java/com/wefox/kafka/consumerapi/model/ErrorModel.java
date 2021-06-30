package com.wefox.kafka.consumerapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wefox.kafka.consumerapi.enums.ErrorTypes;

import lombok.Builder;
import lombok.Data; 

@Data
@Builder
public class ErrorModel { 
    @JsonProperty("payment_id")
    private String paymentId; 
    private ErrorTypes error;
    @JsonProperty("error_description")
    private String errorDescription;
}