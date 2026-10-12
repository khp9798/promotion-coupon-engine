package com.khp.promotion.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.khp.promotion.coupon.MemberCouponRepository;
import com.khp.promotion.coupon.SignupCouponIssuer;
import com.khp.promotion.member.Member;
import com.khp.promotion.member.MemberRegistrationWorkflow;
import com.khp.promotion.member.MemberRepository;

@SpringBootTest
class SignupCouponOutboxProcessorTest {

	@MockitoSpyBean
	private SignupCouponIssuer signupCouponIssuer;

	@Autowired
	private MemberRegistrationWorkflow registrationWorkflow;

	@Autowired
	private SignupCouponOutboxProcessor signupCouponOutboxProcessor;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberCouponRepository memberCouponRepository;

	@Autowired
	private SignupCouponOutboxEventRepository outboxEventRepository;

	@BeforeEach
	void cleanDatabase() {
		memberCouponRepository.deleteAll();

		outboxEventRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	void issuesCouponAndPublishesPendingOutboxEvent() {
		String email = "outbox-member@example.com";

		Member member = registrationWorkflow.register(email);

		assertThat(memberCouponRepository.countByMemberId(member.getId())).isZero();
		assertThat(outboxEventRepository.findAll().get(0).getStatus()).isEqualTo(OutboxStatus.PENDING);

		int processedCount = signupCouponOutboxProcessor.processPending(100);

		assertThat(processedCount).isOne();
		assertThat(memberCouponRepository.countByMemberId(member.getId())).isOne();
		assertThat(outboxEventRepository.findAll().get(0).getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
		assertThat(outboxEventRepository.findAll().get(0).getPublishedAt()).isNotNull();
	}

	@Test
	void doesNotIssueCouponAgainWhenProcessedSequentially() {
		String email = "sequential-member@example.com";

		Member member = registrationWorkflow.register(email);

		final int batchSize = 10;

		assertThat(signupCouponOutboxProcessor.processPending(batchSize)).isOne();
		assertThat(signupCouponOutboxProcessor.processPending(batchSize)).isZero();
		assertThat(memberCouponRepository.countByMemberId(member.getId())).isOne();
		assertThat(outboxEventRepository.countByMemberId(member.getId())).isOne();
		assertThat(outboxEventRepository.findAll().get(0).getStatus())
			.isEqualTo(OutboxStatus.PUBLISHED);
	}

	@Test
	void continuesProcessingOtherEventsWhenOneEventFails() {
		String email1 = "personA@email.com";
		String email2 = "personB@email.com";
		String email3 = "personC@email.com";

		Member memberA = registrationWorkflow.register(email1);
		Member memberB = registrationWorkflow.register(email2);
		Member memberC = registrationWorkflow.register(email3);

		doThrow(new IllegalStateException("coupon provider timeout"))
			.when(signupCouponIssuer)
			.issue(memberB.getId());

		int processedCount = signupCouponOutboxProcessor.processPending(100);

		assertThat(processedCount).isEqualTo(2);
		assertThat(memberCouponRepository.countByMemberId(memberA.getId())).isOne();
		assertThat(memberCouponRepository.countByMemberId(memberB.getId())).isZero();
		assertThat(memberCouponRepository.countByMemberId(memberC.getId())).isOne();

		SignupCouponOutboxEvent eventA =
			outboxEventRepository.findByMemberId(memberA.getId()).orElseThrow();
		SignupCouponOutboxEvent eventB =
			outboxEventRepository.findByMemberId(memberB.getId()).orElseThrow();
		SignupCouponOutboxEvent eventC =
			outboxEventRepository.findByMemberId(memberC.getId()).orElseThrow();

		assertThat(eventA.getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
		assertThat(eventA.getPublishedAt()).isNotNull();

		assertThat(eventB.getStatus()).isEqualTo(OutboxStatus.PENDING);
		assertThat(eventB.getPublishedAt()).isNull();

		assertThat(eventC.getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
		assertThat(eventC.getPublishedAt()).isNotNull();

		assertThat(eventB.getRetryCount()).isOne();
		assertThat(eventB.getLastError())
			.isEqualTo("coupon provider timeout");
		assertThat(eventB.getLastFailedAt()).isNotNull();
	}
}
