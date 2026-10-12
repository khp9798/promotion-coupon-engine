package com.khp.promotion.outbox;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

public class SignupCouponOutboxSchedulerTest {

	@Test
	void delegatesProcessingWithConfiguredBatchSize() {
		SignupCouponOutboxProcessor processor =
			mock(SignupCouponOutboxProcessor.class);

		SignupCouponOutboxScheduler scheduler =
			new SignupCouponOutboxScheduler(processor, 100);

		scheduler.processPendingEvents();

		verify(processor, times(1)).processPending(100);
	}
}
