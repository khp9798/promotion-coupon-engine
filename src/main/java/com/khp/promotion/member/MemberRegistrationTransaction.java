package com.khp.promotion.member;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.khp.promotion.outbox.SignupCouponOutboxEvent;
import com.khp.promotion.outbox.SignupCouponOutboxEventRepository;

@Service
public class MemberRegistrationTransaction {

	private final MemberRepository memberRepository;
	private final SignupCouponOutboxEventRepository outboxEventRepository;

	public MemberRegistrationTransaction(MemberRepository memberRepository,
		SignupCouponOutboxEventRepository outboxEventRepository) {
		this.memberRepository = memberRepository;
		this.outboxEventRepository = outboxEventRepository;
	}

	@Transactional
	public Member createMember(String email) {

		Member savedMember = memberRepository.save(new Member(email));

		SignupCouponOutboxEvent outboxEvent = new SignupCouponOutboxEvent(savedMember.getId(), Instant.now());

		outboxEventRepository.save(outboxEvent);

		return savedMember;
	}
}
