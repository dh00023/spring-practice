package dh0023.springmvc.member.service;

import dh0023.springmvc.member.domain.Member;
import dh0023.springmvc.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 스프링 통합 테스트
 * @SpringBootTest 어노테이션으로 통합 테스트 가능(스프링 컨테이너와 테스트를 함께 실행)
 * @Transactional 테스트 실행시 Transaction을 실행하고 테스트 완료가되면 Rollback이 된다.(테스트 케이스에 붙은 경우에만)
 */
@SpringBootTest
@Transactional
class MemberServiceIntegrationTest {

    /**
     * 생성자 주입 방식을 선호하지만,
     * 테스트시에는 @Autowired로 바로 해도 무관
     */
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

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

        // then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat(exception.getMessage().equals("이미 존재하는 회원입니다."));
    }
}