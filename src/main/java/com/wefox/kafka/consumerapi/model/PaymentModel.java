package com.wefox.kafka.consumerapi.model;

 
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

 
@Builder
@AllArgsConstructor 
@NoArgsConstructor
@Data
public class PaymentModel {
	 @JsonProperty("payment_id")
   private String paymentId;
   
	  @JsonProperty("account_id")
   private Integer accountId;
   
   @JsonProperty("payment_type") 
   private String paymentType;
   
   @JsonProperty("credit_card") 
   private String creditCard;
    
   private Integer amount;
    

}
