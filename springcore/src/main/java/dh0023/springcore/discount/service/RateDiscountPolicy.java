package dh0023.springcore.discount.service;

import dh0023.springcore.member.domain.Grade;
import dh0023.springcore.member.domain.Member;
import org.springframework.stereotype.Component;

/**
 * @Component 어노테이션 추가로 빈설정
 * 이때 빈이름을 설정하고 싶은 경우에는 @Component("빈이름")으로 설정할 수 있다.
 */
@Component
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
