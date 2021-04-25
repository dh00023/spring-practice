package dh0023.springcore.config;

import dh0023.springcore.discount.service.DiscountPolicy;
import dh0023.springcore.discount.service.RateDiscountPolicy;
import dh0023.springcore.member.repository.MemberRepository;
import dh0023.springcore.member.repository.MemoryMemberRepository;
import dh0023.springcore.member.service.MemberService;
import dh0023.springcore.member.service.MemberServiceImpl;
import dh0023.springcore.order.service.OrderService;
import dh0023.springcore.order.service.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 애플리케션의 실제 동작에 필요한 구현 객체 생성
 * 생성한 객체 인스턴스의 참조를 생성자를 통해 주입해준다.
 */
@Configuration
public class AppConfig {

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(getMemberRepository());
    }

    @Bean
    public MemberRepository getMemberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService(){
        return new OrderServiceImpl(getMemberRepository(), getDiscountPolicy());
    }

    @Bean
    public DiscountPolicy getDiscountPolicy() {
        return new RateDiscountPolicy();
    }

}
