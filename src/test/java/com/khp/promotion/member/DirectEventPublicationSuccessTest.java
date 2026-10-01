package com.khp.promotion.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.khp.promotion.coupon.MemberCouponRepository;

@SpringBootTest
class DirectEventPublicationSuccessTest {

    @Autowired
    private MemberRegistrationWorkflow registrationWorkflow;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberCouponRepository memberCouponRepository;

    @BeforeEach
    void cleanDatabase() {
        memberCouponRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void signupCouponIsIssuedWhenDirectPublicationSucceeds() {

        registrationWorkflow.register("success-member@example.com");

        Member savedMember = memberRepository.findByEmail("success-member@example.com").orElseThrow();
        assertThat(memberCouponRepository.countByMemberId(savedMember.getId())).isOne();
    }
}
