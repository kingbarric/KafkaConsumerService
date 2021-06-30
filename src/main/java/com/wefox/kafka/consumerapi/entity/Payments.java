package com.wefox.kafka.consumerapi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wefox.kafka.consumerapi.enums.PaymentType;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Data
public class Payments {
	 @JsonProperty("payment_id")
	@Id
    @Column(name = "payment_id")
    private String paymentId;
    
	  @JsonProperty("account_id")
    @ManyToOne
    @JoinColumn(name="accountId", nullable=false)
    private Accounts accountId;
    
    @JsonProperty("payment_type")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;
    
    @JsonProperty("credit_card")
    @Column(name = "credit_card")
    private String creditCard;
    @Column(name = "amount")
    private Integer amount;
    
    @JsonProperty("created_at")
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

}
