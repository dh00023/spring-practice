package dh0023.springcore.discount.service;

import dh0023.springcore.member.domain.Grade;
import dh0023.springcore.member.domain.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("fixDiscountPolicy")
public class FixDiscountPolicy implements DiscountPolicy{

    private static final int DISCOUNT_AMT = 1000;

    @Override
    public int discount(Member member, int price) {
        if(member.getGrade() == Grade.VIP) {
            return DISCOUNT_AMT;
        } else {
            return 0;
        }
    }
}
