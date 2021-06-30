package com.wefox.kafka.consumerapi.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Entity
@Data
public class Accounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Integer accountId;
    @Column(name = "email")
    private String email;
    @Column(name = "birthdate")
    private Date birthdate;
    @Column(name = "last_payment_date")
    private LocalDateTime lastPaymentDate; 

    @ToString.Exclude
    @OneToMany(mappedBy="accountId")
    private Set<Payments> payments;
}
