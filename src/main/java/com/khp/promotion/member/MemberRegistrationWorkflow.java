package com.khp.promotion.member;

import org.springframework.stereotype.Service;

@Service
public class MemberRegistrationWorkflow {

	private final MemberRegistrationTransaction registrationTransaction;

	public MemberRegistrationWorkflow(
		MemberRegistrationTransaction registrationTransaction
	) {
		this.registrationTransaction = registrationTransaction;
	}

	public Member register(String email) {
		return registrationTransaction.createMember(email);
	}
}
