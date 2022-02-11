package dh0023.springcore.member.repository;

import dh0023.springcore.member.domain.Member;

public interface MemberRepository {

    void save(Member member);

    Member findById(Long id);
}
