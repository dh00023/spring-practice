package dh0023.springcore.discount.service;

import dh0023.springcore.member.domain.Member;

public interface DiscountPolicy {

    /**
     * @return 할인 대상 금액
     */
    int discount(Member member, int price);
}
