package com.khp.promotion.member;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberRegistrationTransaction {

    private final MemberRepository memberRepository;

    public MemberRegistrationTransaction(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member createMember(String email) {
        return memberRepository.save(new Member(email));
    }
}
