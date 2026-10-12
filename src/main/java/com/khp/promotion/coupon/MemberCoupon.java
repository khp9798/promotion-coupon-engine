package com.khp.promotion.coupon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
    name = "member_coupons",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_member_coupons_member_id",
            columnNames = "member_id"
        )
    }
)
public class MemberCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Instant issuedAt;

    protected MemberCoupon() {
    }

    public MemberCoupon(Long memberId, Instant issuedAt) {
        this.memberId = memberId;
        this.issuedAt = issuedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }
}
