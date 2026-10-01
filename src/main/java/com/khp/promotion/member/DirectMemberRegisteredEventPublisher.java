package com.khp.promotion.member;

import com.khp.promotion.coupon.SignupCouponIssuer;
import org.springframework.stereotype.Component;

@Component
public class DirectMemberRegisteredEventPublisher implements MemberRegisteredEventPublisher {

    private final SignupCouponIssuer signupCouponIssuer;

    public DirectMemberRegisteredEventPublisher(SignupCouponIssuer signupCouponIssuer) {
        this.signupCouponIssuer = signupCouponIssuer;
    }

    @Override
    public void publish(MemberRegistered event) {
        signupCouponIssuer.issue(event.memberId());
    }
}
