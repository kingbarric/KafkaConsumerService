package com.wefox.kafka.consumerapi.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.wefox.kafka.consumerapi.entity.Payments;
import com.wefox.kafka.consumerapi.model.PaymentModel;
import com.wefox.kafka.consumerapi.service.ConsumerEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaEventConsumer {

    @Autowired
    private ConsumerEventService consumerEventService;


    /**
     * Consumes Online payment
     * @param payment
     * @throws JsonProcessingException
     */
//    @KafkaListener(topics ="${props.topics.online}" )
    @KafkaListener(topics ="online" )
    public void onMessageOnline(PaymentModel payment) throws JsonProcessingException {

        log.info("ConsumerRecord Online  : {} ", payment);
        
        
    consumerEventService.verifyPayment(payment)
    .block();

    }

    /**
     * Consumes Offline payment
     * @param payment
     * @throws JsonProcessingException
     */
    @KafkaListener(topics ="offline" )
    public void onMessageOffline(PaymentModel payment) throws JsonProcessingException {

        log.info("ConsumerRecord Offline  : {} ", payment);
     consumerEventService.savePayment(payment);

    }
}