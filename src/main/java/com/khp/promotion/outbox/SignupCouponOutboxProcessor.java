package com.khp.promotion.outbox;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SignupCouponOutboxProcessor {

	private final SignupCouponOutboxEventRepository signupCouponOutboxEventRepository;
	private final SignupCouponOutboxEventHandler handler;

	public SignupCouponOutboxProcessor(SignupCouponOutboxEventRepository signupCouponOutboxEventRepository,
		SignupCouponOutboxEventHandler handler) {
		this.signupCouponOutboxEventRepository = signupCouponOutboxEventRepository;
		this.handler = handler;
	}

	public int processPending(int batchSize) {
		Pageable pageable = PageRequest.of(0, batchSize);

		List<SignupCouponOutboxEvent> signupCouponOutboxEventList = signupCouponOutboxEventRepository.findByStatusOrderByOccurredAtAsc(
			OutboxStatus.PENDING, pageable);

		int successCount = 0;

		for (SignupCouponOutboxEvent event : signupCouponOutboxEventList) {
			try{
				handler.handle(event.getId());
				successCount++;
			}catch (RuntimeException e){
				log.error("아웃박스 이벤트 처리 실패, eventId = {}",event.getId(), e);
				handler.recordFailure(event.getId(), e.getMessage());
			}
		}

		return successCount;
	}

}
