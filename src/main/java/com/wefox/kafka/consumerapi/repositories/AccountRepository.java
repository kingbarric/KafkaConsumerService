package com.wefox.kafka.consumerapi.repositories;

import com.wefox.kafka.consumerapi.entity.Accounts;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Accounts,Integer> {
}
