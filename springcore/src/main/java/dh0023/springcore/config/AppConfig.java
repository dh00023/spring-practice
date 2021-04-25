package dh0023.springcore.config;

import dh0023.springcore.discount.service.FixDiscountPolicy;
import dh0023.springcore.member.repository.MemoryMemberRepository;
import dh0023.springcore.member.service.MemberService;
import dh0023.springcore.member.service.MemberServiceImpl;
import dh0023.springcore.order.service.OrderService;
import dh0023.springcore.order.service.OrderServiceImpl;

/**
 * 애플리케션의 실제 동작에 필요한 구현 객체 생성
 * 생성한 객체 인스턴스의 참조를 생성자를 통해 주입해준다.
 */
public class AppConfig {

    public MemberService memberService() {
        return new MemberServiceImpl(new MemoryMemberRepository());
    }

    public OrderService orderService(){
        return new OrderServiceImpl(new MemoryMemberRepository(), new FixDiscountPolicy());
    }

}
