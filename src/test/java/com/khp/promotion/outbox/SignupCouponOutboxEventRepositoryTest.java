package com.khp.promotion.outbox;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

	@Test
	void findsOldestPendingEventsWithinBatchSize() {

		SignupCouponOutboxEvent event1 = new SignupCouponOutboxEvent(100L, OCCURRED_AT);
		SignupCouponOutboxEvent event2 = new SignupCouponOutboxEvent(200L, OCCURRED_AT.plusSeconds(60));
		SignupCouponOutboxEvent event3 = new SignupCouponOutboxEvent(300L, OCCURRED_AT.plusSeconds(120));

		signupCouponOutboxEventRepository.saveAll(
			List.of(event1, event2, event3)
		);

		Pageable pageable = PageRequest.of(0, 2);

		List<SignupCouponOutboxEvent> list = signupCouponOutboxEventRepository.findByStatusOrderByOccurredAtAsc(
			OutboxStatus.PENDING, pageable);

		assertThat(list).hasSize(2);
		assertThat(list.get(0).getMemberId()).isEqualTo(100L);
		assertThat(list.get(1).getMemberId()).isEqualTo(200L);

	}
}
