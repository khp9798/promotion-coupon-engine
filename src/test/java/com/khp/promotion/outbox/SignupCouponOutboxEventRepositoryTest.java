package com.khp.promotion.outbox;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class SignupCouponOutboxEventRepositoryTest {

	@Autowired
	private SignupCouponOutboxEventRepository signupCouponOutboxEventRepository;

	private static final Long MEMBER_ID = 100L;

	private static final Instant OCCURRED_AT = Instant.parse("2026-10-01T09:00:00Z");

	@Test
	void savesPendingMemberRegisteredEvent() {

		SignupCouponOutboxEvent newOutboxEvent = new SignupCouponOutboxEvent(MEMBER_ID, OCCURRED_AT);

		SignupCouponOutboxEvent savedEvent = signupCouponOutboxEventRepository.save(newOutboxEvent);

		assertThat(savedEvent.getId()).isNotNull();
		assertThat(savedEvent.getMemberId()).isEqualTo(MEMBER_ID);
		assertThat(savedEvent.getEventType()).isEqualTo("MEMBER_REGISTERED");
		assertThat(savedEvent.getStatus()).isEqualTo(OutboxStatus.PENDING);
		assertThat(savedEvent.getOccurredAt()).isEqualTo(OCCURRED_AT);
		assertThat(signupCouponOutboxEventRepository.countByMemberId(MEMBER_ID)).isOne();
	}
}
