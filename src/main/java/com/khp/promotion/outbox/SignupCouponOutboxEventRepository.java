package com.khp.promotion.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SignupCouponOutboxEventRepository extends JpaRepository<SignupCouponOutboxEvent, Long> {

	long countByMemberId(Long memberId);
}
