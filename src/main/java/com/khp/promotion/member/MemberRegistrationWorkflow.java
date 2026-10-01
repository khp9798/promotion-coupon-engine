package com.khp.promotion.member;

import org.springframework.stereotype.Service;

@Service
public class MemberRegistrationWorkflow {

    private final MemberRegistrationTransaction registrationTransaction;
    private final MemberRegisteredEventPublisher eventPublisher;

    public MemberRegistrationWorkflow(
            MemberRegistrationTransaction registrationTransaction,
            MemberRegisteredEventPublisher eventPublisher
    ) {
        this.registrationTransaction = registrationTransaction;
        this.eventPublisher = eventPublisher;
    }

    public Member register(String email) {
        Member member = registrationTransaction.createMember(email);
        eventPublisher.publish(new MemberRegistered(member.getId()));
        return member;
    }
}
