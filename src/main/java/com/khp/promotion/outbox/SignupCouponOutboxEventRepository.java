package com.khp.promotion.outbox;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignupCouponOutboxEventRepository extends JpaRepository<SignupCouponOutboxEvent, Long> {

	long countByMemberId(Long memberId);

	List<SignupCouponOutboxEvent> findByStatusOrderByOccurredAtAsc(OutboxStatus status, Pageable pageable);

}
