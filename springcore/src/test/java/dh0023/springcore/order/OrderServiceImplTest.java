package dh0023.springcore.order;

import dh0023.springcore.config.AppConfig;
import dh0023.springcore.discount.service.FixDiscountPolicy;
import dh0023.springcore.member.domain.Grade;
import dh0023.springcore.member.domain.Member;
import dh0023.springcore.member.repository.MemoryMemberRepository;
import dh0023.springcore.member.service.MemberService;
import dh0023.springcore.order.domain.Order;
import dh0023.springcore.order.service.OrderService;
import dh0023.springcore.order.service.OrderServiceImpl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderServiceImplTest {

    @Test
    void createOrder() {
        MemoryMemberRepository memberRepository = new MemoryMemberRepository();
        memberRepository.save(new Member(1L, "name1", Grade.VIP));


        OrderServiceImpl orderService = new OrderServiceImpl(memberRepository, new FixDiscountPolicy());
        Order order  = orderService.createOrder(1L, "itemA", 10000);

        Assertions.assertThat(order.getDiscountPrice()).isEqualTo(1000);
    }

}
