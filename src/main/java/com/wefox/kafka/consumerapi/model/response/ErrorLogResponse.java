package com.wefox.kafka.consumerapi.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ErrorLogResponse {
	
	@JsonProperty("payment_id")
	private String paymentId;
	
	@JsonProperty("created_at")
	private String createdAt;

}
