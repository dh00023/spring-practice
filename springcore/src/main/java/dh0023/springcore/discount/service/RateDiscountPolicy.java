package dh0023.springcore.discount.service;

import dh0023.springcore.member.domain.Grade;
import dh0023.springcore.member.domain.Member;

public class RateDiscountPolicy implements DiscountPolicy{

    private final static int DIS_PER = 10;

    @Override
    public int discount(Member member, int price) {
        if (member.getGrade() == Grade.VIP){
            return price * DIS_PER / 100;
        } else {
            return 0;
        }
    }
}
