package dh0023.springcore.member.service;

import dh0023.springcore.member.domain.Member;
import dh0023.springcore.member.repository.MemberRepository;

public class MemberServiceImpl implements MemberService{

    /**
     * 생성자 DI를 통해 구현클래스 의존성 제거 => 실행에만 집중 가능
     */
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findByMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
