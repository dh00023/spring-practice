package dh0023.springcore.beanfind;

import dh0023.springcore.config.AppConfig;
import dh0023.springcore.member.service.MemberService;
import dh0023.springcore.member.service.MemberServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class ApplicationContextBasicFindTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("빈 이름으로 조회")
    void findBeanByName() {
        MemberService memberService = ac.getBean("memberService", MemberService.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }


    @Test
    @DisplayName("이름없이 타입으로만 조회")
    void findBeanByType() {
        // 타입으로만 조회하면, 같은 타입이 둘 이상인 경우 오류가 발생한다.
        MemberService memberService = ac.getBean(MemberService.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }

    @Test
    @DisplayName("구체 타입으로 조회")
    void findBeanByName2() {

        // 역할과 구현을 구분하고, 역할에 의존해야한다.
        // 하지만 구체 타입으로 조회하는건 구현에 의존하는 것이므로 좋은 방식은 아니다.
        MemberService memberService = ac.getBean("memberService", MemberServiceImpl.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }

    @Test
    @DisplayName("빈 이름으로 조회X")
    void findBeanByNameX() {

//      ac.getBean("xxxxx", MemberService.class);

        // org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'xxxxx' available
        // lambda 실행히 왼쪽 Exception이 수행되어야한다.
        assertThrows(NoSuchBeanDefinitionException.class, () -> ac.getBean("xxxxx", MemberService.class));

    }
}
