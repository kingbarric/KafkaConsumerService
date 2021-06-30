package com.wefox.kafka.consumerapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wefox.kafka.consumerapi.config.ConfigProperties;
import com.wefox.kafka.consumerapi.entity.Accounts;
import com.wefox.kafka.consumerapi.entity.Payments;
import com.wefox.kafka.consumerapi.enums.ErrorTypes;
import com.wefox.kafka.consumerapi.enums.PaymentType;
import com.wefox.kafka.consumerapi.model.ErrorModel;
import com.wefox.kafka.consumerapi.model.PaymentModel;
import com.wefox.kafka.consumerapi.repositories.AccountRepository;
import com.wefox.kafka.consumerapi.repositories.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerEventService {

	private final ObjectMapper objectMapper;

	private final KafkaTemplate<Integer, PaymentModel> kafkaTemplate;

	private final PaymentRepository paymentRepo;

	private final AccountRepository accountRepo;

	private final ConfigProperties configProperties;

	public Mono<Void> savePayment(ConsumerRecord<Integer, PaymentModel> consumerRecord) {

		try {

			PaymentModel paymentModel = consumerRecord.value();

			Optional<Accounts> accountOp = accountRepo.findById(paymentModel.getAccountId());
			if (accountOp.isPresent()) {

				Accounts account = accountOp.get();
				Payments payment = new Payments();
				payment.setAccountId(account);
				payment.setAmount(paymentModel.getAmount());
				payment.setPaymentId(paymentModel.getPaymentId());
				payment.setCreditCard(paymentModel.getCreditCard());
				payment.setPaymentType(PaymentType.getEnum(paymentModel.getPaymentType()));
				payment = paymentRepo.save(payment);
				account.setLastPaymentDate(LocalDateTime.now());
				accountRepo.save(account);
				log.info("Data saved into database ...");
				return Mono.empty();
			} else {

				log.info("No such account number available");
				return Mono.empty();
			}

		} catch (Exception e) {

			// log error
			ErrorModel errorModel = ErrorModel.builder().paymentId(consumerRecord.value().getPaymentId())
					.error(ErrorTypes.DATABASE).errorDescription("Network Error OCcured").build();

			return logError(errorModel).doOnNext(res -> log.info("ErrorLogResponse :: {} ", res)).then();

			// log.info("Exception thrown while saving data ", e);

		}

	}

	public void handleRecovery(ConsumerRecord<Integer, PaymentModel> record) {

		Integer key = record.key();
		PaymentModel message = record.value();

		ListenableFuture<SendResult<Integer, PaymentModel>> listenableFuture = kafkaTemplate.sendDefault(key, message);

		listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, PaymentModel>>() {
			@Override
			public void onFailure(Throwable ex) {
				handleFailure(key, message, ex);
			}

			@Override
			public void onSuccess(SendResult<Integer, PaymentModel> result) {
				handleSuccess(key, message, result);
			}

		});
	}

	private void handleFailure(Integer key, PaymentModel value, Throwable ex) {
		log.error("ErrorModel Sending the Message and the exception is {}", ex.getMessage());
		try {
			throw ex;
		} catch (Throwable throwable) {
			log.error("ErrorModel in OnFailure: {}", throwable.getMessage());
		}
	}

	private void handleSuccess(Integer key, PaymentModel value, SendResult<Integer, PaymentModel> result) {
		log.info("Message Sent SuccessFully for the key : {} and the value is {} , partition is {}", key, value,
				result.getRecordMetadata().partition());
	}

	/**
	 * Make an Api call to verify payment
	 * 
	 * @param consumerRecord
	 * @return
	 */

	public Mono<Void> verifyPayment(ConsumerRecord<Integer, PaymentModel> consumerRecord) {

		try {
			return WebClient.builder().build().post().uri(configProperties.getConfirmPaymentUrl())
					.headers((httpHeaders) -> {
						httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
						httpHeaders.setContentType(MediaType.APPLICATION_JSON);

					}).body(Mono.just(consumerRecord.value()), PaymentModel.class).exchangeToMono(response -> {
						log.info("response :: {} ", response);

						if (response.statusCode().equals(HttpStatus.OK)) {

							// save valid payment into database
							log.info("Payment valid");
							savePayment(consumerRecord);

							return response.releaseBody().onErrorResume(exception -> {

								// Network
								ErrorModel errorModel = ErrorModel.builder()
										.paymentId(consumerRecord.value().getPaymentId()).error(ErrorTypes.NETWORK)
										.errorDescription("Network Error OCcured").build();

								return logError(errorModel).doOnNext(res -> log.info("ErrorLogResponse :: {} ", res))
										.then(); 	}

					);
						} else {
							// Turn to error make another http call

							log.info("Payment not  valid");
							ErrorModel errorModel = ErrorModel.builder()
									.paymentId(consumerRecord.value().getPaymentId()).error(ErrorTypes.OTHERS)
									.errorDescription("Network Error OCcured").build();

							return logError(errorModel).doOnNext(res -> log.info("ErrorLogResponse :: {} ", res))
									.then();
						}
					});
		} catch (Exception e) {
			log.info("Calling verify Error {}", e);

			return Mono.empty();
		}
	}

	/**
	 * Log error
	 *
	 * @param errorModel
	 * @return
	 */

	public Mono<Void> logError(ErrorModel errorModel) {

		try {
			log.info("Calling verify");
			return WebClient.builder().build().post().uri(configProperties.getLogErrorUrl()).headers((httpHeaders) -> {
				httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
				httpHeaders.setContentType(MediaType.APPLICATION_JSON);

			}).body(Mono.just(errorModel), ErrorModel.class).exchangeToMono(response -> {
				log.info("response :: {} ", response);

				if (response.statusCode().equals(HttpStatus.OK)) {

					// save valid payment into database
					log.info("Error logging was successful");

					return response.releaseBody().onErrorResume(exception -> Mono.empty());
				} else {
					// Turn to error make another http call
					log.info("call to log comeplete with error");

					return Mono.empty();
				}
			}).timeout(Duration.ofMillis(configProperties.getTimeOut()));

		} catch (Exception e) {

			log.info("Calling log error :: Error occured {}", e);
			return Mono.empty();
		}
	}
}
