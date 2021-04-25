package dh0023.springcore.order.service;

import dh0023.springcore.discount.service.DiscountPolicy;
import dh0023.springcore.discount.service.FixDiscountPolicy;
import dh0023.springcore.discount.service.RateDiscountPolicy;
import dh0023.springcore.member.domain.Member;
import dh0023.springcore.member.repository.MemberRepository;
import dh0023.springcore.member.repository.MemoryMemberRepository;
import dh0023.springcore.order.domain.Order;

public class OrderServiceImpl implements OrderService{


    private final MemberRepository memberRepository = new MemoryMemberRepository();

    // 정률로 변경하려면 기존의 FixDiscountPolicy를 RateDiscountPolicy로 변경
    // SOLID 법칙의 OCP, DIP를 지키지 못함
    // DIP 위반 : 추상인터페이스(DiscountPolicy)와 구현 클레스 (FixDiscountPolicy or RateDiscountPolicy)에 의존하고 있어 지키지 못함
    // OCP 위반 : 정책 변경시 OrderServiceImpl 코드도 변경되어야 한다.
//    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

}
