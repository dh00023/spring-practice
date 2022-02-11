package dh0023.springmvc.member.service;

import dh0023.springmvc.member.domain.Member;
import dh0023.springmvc.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * JPA를 통한 모든 데이터 변경은 트랜잭션 안에서 실행해야하기 때문에 Transactional 어노테이션 설정
 */
@Transactional
public class MemberService {


    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }
    /**
     * 회원 가입
     * 이때 같은 이름의 중복회원은 가입 안됨.
     * @param member
     * @return
     */
    public Long join(Member member){

//        Optional<Member> result = memberRepository.findByName(member.getName());
//        result.ifPresent( m -> {
//            throw new IllegalStateException("이미 존재하는 회원입니다.");
//        });

        // cntrl + t는 extract 함수
        validateDuplicateMember(member);

        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName())
                .ifPresent( m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    /**
     * 전체 회원 조회
     * @return
     */
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    /**
     * 회원 ID로 조회해오
     * @param id
     * @return
     */
    public Optional<Member> findOne(Long id){
        return memberRepository.findById(id);
    }
}
