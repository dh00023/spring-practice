package dh0023.springcore.member;

import dh0023.springcore.config.AppConfig;
import dh0023.springcore.member.domain.Grade;
import dh0023.springcore.member.domain.Member;
import dh0023.springcore.member.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MemberServiceTest {

    MemberService memberService;

    @BeforeEach
    public void beforeEach(){
        AppConfig appConfig = new AppConfig();
        memberService = appConfig.memberService();
    }

    @Test
    void join(){

        // given
        Member member = new Member(1L, "memberA", Grade.VIP);

        // when
        memberService.join(member);
        Member member1 = memberService.findByMember(1L);

        // then
        Assertions.assertThat(member).isEqualTo(member1);
    }
}
