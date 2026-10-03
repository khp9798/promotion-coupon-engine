package com.khp.promotion.outbox;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "signup_coupon_outbox")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupCouponOutboxEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 아웃박스는 이벤트 전달용 테이블이므로 Member 연관관계 대신 식별자만 저장한다.
	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private String eventType;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OutboxStatus status;

	@Column(nullable = false)
	private Instant occurredAt;

	private Instant publishedAt;

	public SignupCouponOutboxEvent(Long memberId, Instant occurredAt) {
		this.memberId = memberId;
		this.eventType = "MEMBER_REGISTERED";
		this.status = OutboxStatus.PENDING;
		this.occurredAt = occurredAt;
	}

	public void completePublication(Instant publishedAt) {
		this.status = OutboxStatus.PUBLISHED;
		this.publishedAt = publishedAt;
	}

}
