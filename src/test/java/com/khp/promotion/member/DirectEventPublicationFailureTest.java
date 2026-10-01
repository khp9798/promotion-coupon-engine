package com.khp.promotion.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import com.khp.promotion.coupon.MemberCouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class DirectEventPublicationFailureTest {

    @Autowired
    private MemberRegistrationWorkflow registrationWorkflow;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberCouponRepository memberCouponRepository;

    @MockitoBean
    private MemberRegisteredEventPublisher eventPublisher;

    @BeforeEach
    void cleanDatabase() {
        memberCouponRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void memberRemainsButSignupCouponIsMissingWhenDirectPublicationFails() {
        doThrow(new IllegalStateException("simulated event transport failure"))
                .when(eventPublisher)
                .publish(any(MemberRegistered.class));

        assertThatThrownBy(() -> registrationWorkflow.register("new-member@example.com"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("simulated event transport failure");

        Member savedMember = memberRepository.findByEmail("new-member@example.com").orElseThrow();
        assertThat(memberCouponRepository.countByMemberId(savedMember.getId())).isZero();
    }
}
