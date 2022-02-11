package dh0023.springmvc.member.service;

import dh0023.springmvc.member.domain.Member;
import dh0023.springmvc.member.repository.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 단위케이스
 * 순수한 단위 테스트가 더 좋은 테스트일 확률이 높다.
 */
class MemberServiceTest {

    MemberService memberService;
    MemoryMemberRepository memberRepository;

    @BeforeEach
    public void beforeEach(){
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository);
    }
    @AfterEach
    public void afterEach(){
        memberRepository.clearStore();
    }

    /**
     * 테스트 코드는 빌드에서 제외되기 때문에
     * 직관적으로 알아 볼 수 있도록 과감하게 한글을 쓰는 것도 괜찮다.
     *
     * given - when - then으로 진행하는 것을 추천
     * 
     * 테스트는 정상 flow도 괜찮지만, 예외 케이스도 중요하다.
     */
    @Test
    void 회원가입(){
        // given
        Member member = new Member();
        member.setName("faker");

        // when
        Long saveId = memberService.join(member);

        // then
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName().equals(findMember.getName()));
    }

    @Test
    void 중복_회원_예외(){
        // given
        Member member = new Member();
        member.setName("faker");

        Member member2 = new Member();
        member2.setName("faker");

        // when
        memberService.join(member);
//        try{
//            memberService.join(member2);
//            fail();
//        }catch(IllegalStateException e){
//            assertThat(e.getMessage().equals("이미 존재하는 회원입니다."));
//        }

        // lambda함수 수행시 Exception을 수행해야한다!!
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat(exception.getMessage().equals("이미 존재하는 회원입니다."));
    }

}