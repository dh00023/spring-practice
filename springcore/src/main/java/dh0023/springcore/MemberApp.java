package dh0023.springcore;

import dh0023.springcore.config.AppConfig;
import dh0023.springcore.member.domain.Grade;
import dh0023.springcore.member.domain.Member;
import dh0023.springcore.member.service.MemberService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MemberApp {

    public static void main(String[] args) {

        // annotation 기반으로 관리(AppConfig 기반)
        // applicationContext는 스프링 컨테이너며, 인터페이스다.(다형성)
        // AnnotationConfigApplicationContext은 구현
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);

        Member member = new Member(1L, "memberA", Grade.VIP);
        memberService.join(member);

        Member member1 = memberService.findByMember(1L);

        System.out.println("new member => " + member.getName());
        System.out.println("findBy member => " + member1.getName());
    }
}
