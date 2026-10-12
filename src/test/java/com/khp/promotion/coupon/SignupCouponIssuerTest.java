package com.khp.promotion.coupon;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

@SpringBootTest
class SignupCouponIssuerTest {

	@Autowired
	private SignupCouponIssuer signupCouponIssuer;

	@Autowired
	private MemberCouponRepository memberCouponRepository;

	@BeforeEach
	void cleanDatabase() {
		memberCouponRepository.deleteAll();
	}

	@Test
	void doesNotIssueSignupCouponTwiceForSameMember() {
		// Given
		Long memberId = 100L;

		// When: 첫 번째 발급
		signupCouponIssuer.issue(memberId);

		// Then: 두 번째 발급은 DB UNIQUE 제약 위반
		assertThatThrownBy(() -> signupCouponIssuer.issue(memberId)).isInstanceOf(
			DataIntegrityViolationException.class);

		// 최종 저장된 쿠폰은 한 장
		assertThat(memberCouponRepository.countByMemberId(memberId)).isOne();
	}
}