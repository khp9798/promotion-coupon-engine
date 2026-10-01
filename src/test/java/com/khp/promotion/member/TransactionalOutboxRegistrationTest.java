package com.khp.promotion.member;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.khp.promotion.outbox.SignupCouponOutboxEventRepository;

@SpringBootTest
class TransactionalOutboxRegistrationTest {

	@Autowired
	private MemberRegistrationTransaction registrationTransaction;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private SignupCouponOutboxEventRepository outboxEventRepository;

	@BeforeEach
	void cleanDatabase() {
		outboxEventRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	void savesMemberAndOutboxEventTogether() {
		// given
		String email = "outbox-member@example.com";

		// when
		Member savedMember = registrationTransaction.createMember(email);

		// then
		assertThat(memberRepository.findByEmail(email)).isPresent();
		assertThat(outboxEventRepository.countByMemberId(savedMember.getId())).isOne();
	}
}