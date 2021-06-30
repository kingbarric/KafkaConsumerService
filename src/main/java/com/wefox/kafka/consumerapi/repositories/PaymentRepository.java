package com.wefox.kafka.consumerapi.repositories;

import com.wefox.kafka.consumerapi.entity.Payments;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payments,String> {
}
