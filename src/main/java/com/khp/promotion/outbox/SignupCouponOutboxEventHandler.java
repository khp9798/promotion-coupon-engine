package com.khp.promotion.outbox;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.khp.promotion.coupon.SignupCouponIssuer;

@Service
public class SignupCouponOutboxEventHandler {

	private final SignupCouponIssuer signupCouponIssuer;
	private final SignupCouponOutboxEventRepository signupCouponOutboxEventRepository;

	public SignupCouponOutboxEventHandler(SignupCouponIssuer signupCouponIssuer,
		SignupCouponOutboxEventRepository signupCouponOutboxEventRepository) {
		this.signupCouponIssuer = signupCouponIssuer;
		this.signupCouponOutboxEventRepository = signupCouponOutboxEventRepository;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handle(Long eventId) {
		// 1. eventId로 아웃박스 이벤트 다시 조회
		SignupCouponOutboxEvent event = signupCouponOutboxEventRepository.findById(eventId).orElseThrow();

		// 2. 이미 PENDING이 아니라면 처리하지 않기
		if (event.getStatus() != (OutboxStatus.PENDING))
			return;
		// 3. 회원 쿠폰 발급
		signupCouponIssuer.issue(event.getMemberId());
		// 4. PUBLISHED로 상태 변경
		event.completePublication(Instant.now());
	}
}
