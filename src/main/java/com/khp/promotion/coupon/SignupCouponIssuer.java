package com.khp.promotion.coupon;

import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupCouponIssuer {

    private final MemberCouponRepository memberCouponRepository;

    public SignupCouponIssuer(MemberCouponRepository memberCouponRepository) {
        this.memberCouponRepository = memberCouponRepository;
    }

    @Transactional
    public void issue(Long memberId) {
        memberCouponRepository.save(new MemberCoupon(memberId, Instant.now()));
    }
}
