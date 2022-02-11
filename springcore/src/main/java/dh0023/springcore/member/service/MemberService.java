package dh0023.springcore.member.service;

import dh0023.springcore.member.domain.Member;

public interface MemberService {

    void join(Member member);

    Member findByMember(Long memberId);
}
