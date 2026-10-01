package com.khp.promotion.member;

public interface MemberRegisteredEventPublisher {

    void publish(MemberRegistered event);
}
