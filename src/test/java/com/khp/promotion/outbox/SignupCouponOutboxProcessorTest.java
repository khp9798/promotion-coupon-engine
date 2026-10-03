package com.khp.promotion.outbox;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.khp.promotion.coupon.MemberCouponRepository;
import com.khp.promotion.member.Member;
import com.khp.promotion.member.MemberRegistrationWorkflow;
import com.khp.promotion.member.MemberRepository;

@SpringBootTest
class SignupCouponOutboxProcessorTest {

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
}
