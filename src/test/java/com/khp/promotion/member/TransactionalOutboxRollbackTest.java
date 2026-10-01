package com.khp.promotion.member;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.khp.promotion.outbox.SignupCouponOutboxEvent;
import com.khp.promotion.outbox.SignupCouponOutboxEventRepository;

@SpringBootTest
class TransactionalOutboxRollbackTest {

	@Autowired
	private MemberRegistrationTransaction registrationTransaction;

	@Autowired
	private MemberRepository memberRepository;

	@MockitoBean
	private SignupCouponOutboxEventRepository outboxEventRepository;

	@BeforeEach
	void cleanDatabase() {
		memberRepository.deleteAll();
	}

	@Test
	void rollsBackMemberWhenOutboxSaveFails() {
		String email = "rollback-member@example.com";

		doThrow(new IllegalStateException("simulated outbox save failure"))
			.when(outboxEventRepository)
			.save(any(SignupCouponOutboxEvent.class));

		assertThatThrownBy(() -> registrationTransaction.createMember(email))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("simulated outbox save failure");

		assertThat(memberRepository.findByEmail(email)).isEmpty();
	}

}
