package dh0023.springcore.member.service;

import dh0023.springcore.member.domain.Member;
import dh0023.springcore.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberServiceImpl implements MemberService{

    /**
     * 생성자 DI를 통해 구현클래스 의존성 제거 => 실행에만 집중 가능
     */
    private final MemberRepository memberRepository;

    /**
     * @Autowired로 자동의존관계를 주입할 수 있으며, 이때 타입이 같은 빈을 찾아서 주입힌다.
     * @param memberRepository
     */
    @Autowired
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

    // 테스트 용도
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
