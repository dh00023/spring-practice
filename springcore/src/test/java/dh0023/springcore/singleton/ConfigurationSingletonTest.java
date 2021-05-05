package dh0023.springcore.singleton;

import dh0023.springcore.config.AppConfig;
import dh0023.springcore.member.repository.MemberRepository;
import dh0023.springcore.member.service.MemberServiceImpl;
import dh0023.springcore.order.service.OrderServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ConfigurationSingletonTest {

    @Test
    void configurationTest() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);
        OrderServiceImpl orderService = ac.getBean("orderService", OrderServiceImpl.class);
        MemberRepository memberRepository = ac.getBean("memberRepository", MemberRepository.class);

        MemberRepository memberRepository1 = memberService.getMemberRepository();
        MemberRepository memberRepository2 = orderService.getMemberRepository();

        System.out.println("memberRepository = " + memberRepository);
        System.out.println("memberRepository1 = " + memberRepository1);
        System.out.println("memberRepository2 = " + memberRepository2);

        Assertions.assertThat(memberRepository).isSameAs(memberRepository1);
        Assertions.assertThat(memberRepository).isSameAs(memberRepository2);
    }


    @Test
    void configurationDeep() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        AppConfig bean = ac.getBean(AppConfig.class);

        // 순수한 클래스라면  class dh0023.springcore.config.AppConfig로 출력되어야한다.
        // 스프링이 CGLIB 바이트코드 조작 라이브러리를 사용해 AppConfig를 상속받은 임의의 클래스(AppConfig@CGLIB)를 스프링 빈으로 등록함.
        // @Bean이 붙은 메서드마다 이미 스프링 빈이 존재하면, 존재하는 빈을 반환, 존재하지 않는다면 새로 생
        // @Configuration을 등록하지 않아도, @Bean으로 스프링 빈등록이 가능하나, 바이트 조작을 하지 않는 Class가 생성된다.(싱글톤 보장X)
        System.out.println("bean = " + bean.getClass());
        // bean = class dh0023.springcore.config.AppConfig$$EnhancerBySpringCGLIB$$2a067523

    }
}
