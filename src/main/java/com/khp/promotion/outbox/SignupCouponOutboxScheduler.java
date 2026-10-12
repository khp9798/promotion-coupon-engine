package com.khp.promotion.outbox;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(
	name = "outbox.scheduling-enabled",
	havingValue = "true",
	matchIfMissing = true
)
@Component
public class SignupCouponOutboxScheduler {

	private final SignupCouponOutboxProcessor processor;
	private final int batchSize;

	public SignupCouponOutboxScheduler(SignupCouponOutboxProcessor processor, @Value("${outbox.batch-size}") int batchSize) {
		this.processor = processor;
		this.batchSize = batchSize;
	}

	@Scheduled(fixedDelayString = "${outbox.polling-interval-ms}")
	public void processPendingEvents(){

		processor.processPending(batchSize);
	}
}
