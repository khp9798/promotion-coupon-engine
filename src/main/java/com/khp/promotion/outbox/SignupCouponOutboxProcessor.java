package com.khp.promotion.outbox;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.khp.promotion.coupon.SignupCouponIssuer;

@Service
public class SignupCouponOutboxProcessor {

	private final SignupCouponOutboxEventRepository signupCouponOutboxEventRepository;
	private final SignupCouponIssuer signupCouponIssuer;

	public SignupCouponOutboxProcessor(SignupCouponOutboxEventRepository signupCouponOutboxEventRepository,
		SignupCouponIssuer signupCouponIssuer) {
		this.signupCouponOutboxEventRepository = signupCouponOutboxEventRepository;
		this.signupCouponIssuer = signupCouponIssuer;
	}

	@Transactional
	public int processPending(int batchSize) {
		Pageable pageable = PageRequest.of(0, batchSize);

		List<SignupCouponOutboxEvent> signupCouponOutboxEventList = signupCouponOutboxEventRepository.findByStatusOrderByOccurredAtAsc(
			OutboxStatus.PENDING, pageable);

		for (SignupCouponOutboxEvent event : signupCouponOutboxEventList) {
			signupCouponIssuer.issue(event.getMemberId());
			event.completePublication(Instant.now());
		}

		return signupCouponOutboxEventList.size();
	}

}
