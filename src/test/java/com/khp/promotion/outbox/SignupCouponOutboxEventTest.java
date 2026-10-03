package com.khp.promotion.outbox;

import static org.assertj.core.api.Assertions.assertThat;

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
}
