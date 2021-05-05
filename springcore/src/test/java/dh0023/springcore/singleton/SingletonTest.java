package dh0023.springcore.singleton;

import dh0023.springcore.config.AppConfig;
import dh0023.springcore.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.*;

public class SingletonTest {


    @Test
    @DisplayName("스프링 없는 순수한 DI 컨테이너")
    void pureContainer() {

        // 스프링 없는 순수한 DI 컨테이너인 AppConfig는 요청시마다 컨테이너 생성
        AppConfig appConfig = new AppConfig();

        // 1. 조회 : 호출시마다 객체 생성
        MemberService memberService = appConfig.memberService();

        MemberService memberService2 = appConfig.memberService();

        System.out.println("memverService = " + memberService);
        System.out.println("memberService2 = " + memberService2);

        // memverService = dh0023.springcore.member.service.MemberServiceImpl@75d4a5c2
        // memberService2 = dh0023.springcore.member.service.MemberServiceImpl@557caf28

        assertThat(memberService).isNotSameAs(memberService2);
    }

    @Test
    @DisplayName("싱글톤 패턴을 적용한 객체 사용")
    void singletonServiceTest() {
        SingletonService instance = SingletonService.getInstance();
        SingletonService instance2 = SingletonService.getInstance();

        System.out.println("instance1 = " + instance);
        System.out.println("instance2 = " + instance2);

        //instance1 = dh0023.springcore.singleton.SingletonService@5bf0d49
        //instance2 = dh0023.springcore.singleton.SingletonService@5bf0d49

        assertThat(instance).isSameAs(instance2);
    }

    @Test
    @DisplayName("스프링 컨테이너와 싱글톤")
    void springContainer() {

        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        // 1. 조회 : 호출시마다 객체 생성
        MemberService memberService = ac.getBean("memberService", MemberService.class);
        MemberService memberService2 = ac.getBean("memberService", MemberService.class);

        System.out.println("memverService = " + memberService);
        System.out.println("memberService2 = " + memberService2);

        //memverService = dh0023.springcore.member.service.MemberServiceImpl@5bf0d49
        //memberService2 = dh0023.springcore.member.service.MemberServiceImpl@5b7a5baa

        assertThat(memberService).isSameAs(memberService2);
    }
}