package com.khp.promotion.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.khp.promotion.coupon.MemberCouponRepository;
import com.khp.promotion.outbox.OutboxStatus;
import com.khp.promotion.outbox.SignupCouponOutboxEventRepository;

@SpringBootTest
class OutboxMemberRegistrationWorkflowTest {

	@Autowired
	private MemberRegistrationWorkflow registrationWorkflow;

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
	void registrationStoresPendingOutboxWithoutIssuingCouponImmediately() {

		String email = "outbox-member@example.com";

		Member member = registrationWorkflow.register(email);

		assertThat(memberRepository.findByEmail(email)).isPresent();
		assertThat(memberCouponRepository.countByMemberId(member.getId())).isZero();
		assertThat(outboxEventRepository.countByMemberId(member.getId())).isOne();
		assertThat(outboxEventRepository.findAll().get(0).getStatus()).isEqualTo(OutboxStatus.PENDING);
	}
}
