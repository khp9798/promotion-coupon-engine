package com.khp.promotion.outbox;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class SignupCouponOutboxEventTest {

	private static final Instant OCCURRED_AT = Instant.parse("2026-10-01T09:00:00Z");

	// 발행 완료 시 상태와 완료 시각이 함께 변경되는지 검증
	@Test
	void completesPublicationWithPublishedTime() {

		SignupCouponOutboxEvent event = new SignupCouponOutboxEvent(100L, OCCURRED_AT);

		Instant publishedAt = OCCURRED_AT.plusSeconds(60);
		event.completePublication(publishedAt);

		assertThat(event.getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
		assertThat(event.getPublishedAt()).isEqualTo(publishedAt);
	}

	@Test
	void changesStatusToFailedAfterThreeFailures(){
		SignupCouponOutboxEvent event =
			new SignupCouponOutboxEvent(100L, OCCURRED_AT);

		event.recordFailure("first failure", OCCURRED_AT.plusSeconds(60));
		event.recordFailure("second failure", OCCURRED_AT.plusSeconds(120));

		assertThat(event.getRetryCount()).isEqualTo(2);
		assertThat(event.getStatus()).isEqualTo(OutboxStatus.PENDING);

		Instant thirdFailedAt = OCCURRED_AT.plusSeconds(180);
		event.recordFailure("third failure", thirdFailedAt);

		assertThat(event.getRetryCount()).isEqualTo(3);
		assertThat(event.getStatus()).isEqualTo(OutboxStatus.FAILED);
		assertThat(event.getLastError()).isEqualTo("third failure");
		assertThat(event.getLastFailedAt()).isEqualTo(thirdFailedAt);
	}

	@Test
	void startsProcessingPendingEvent() {
		SignupCouponOutboxEvent event =
			new SignupCouponOutboxEvent(100L, OCCURRED_AT);

		Instant processingStartedAt =
			OCCURRED_AT.plusSeconds(10);

		event.startProcessing(processingStartedAt);

		assertThat(event.getStatus())
			.isEqualTo(OutboxStatus.PROCESSING);
		assertThat(event.getProcessingStartedAt())
			.isEqualTo(processingStartedAt);
	}

	@Test
	void cannotStartProcessingPublishedEvent() {
		SignupCouponOutboxEvent event =
			new SignupCouponOutboxEvent(100L, OCCURRED_AT);

		event.completePublication(OCCURRED_AT);

		Instant processingStartedAt =
			OCCURRED_AT.plusSeconds(10);

		assertThatThrownBy(
			() -> event.startProcessing(processingStartedAt)
		)
			.isInstanceOf(IllegalStateException.class);

		assertThat(event.getStatus())
			.isEqualTo(OutboxStatus.PUBLISHED);
		assertThat(event.getProcessingStartedAt())
			.isNull();
	}
}
