- [김영한 - 스프링 핵심 기본 원리](https://inf.run/JAwH)를 듣고 공부한 내용입니다.

# 비즈니스 요구사항과 설계

## 회원

- 회원을 가입하고 조회할 수 있다.
- 회원은 일반과 VIP 두 가지 등급이 있다.
- 회원 데이터는 자체 DB를 구축할 수 있고, 외부 시스템과 연동할 수 있다. (미확정)

## 주문과 할인 정책

- 회원은 상품을 주문할 수 있다.
- 회원 등급에 따라 할인 정책을 적용할 수 있다.
- 할인 정책은 모든 VIP는 1000원을 할인해주는 고정 금액 할인을 적용해달라. (나중에 변경 될 수 있다.)
- 할인 정책은 변경 가능성이 높다. 회사의 기본 할인 정책을 아직 정하지 못했고, 오픈 직전까지 고민을 미루고 싶다. 최악의 경우 할인을 적용하지 않을 수 도 있다. (미확정)


객체 다이어그램 : 서버가 떠서 실제로 참조된 객체를 보여준다.

**역할과 구현을 분리**해서 자유롭게 구현 객체를 조립할 수 있도록 설계하는 것이 중요하다.

# 최초 구현

```java
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
//    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

}
```

1. 정액 할인 정책에서 정률 할인 정책으로 변경시 SOLID 원칙의 DIP, OCP를 위반하고 있다.
    - DIP 위반 : 추상인터페이스(DiscountPolicy)와 구현 클레스 (FixDiscountPolicy or RateDiscountPolicy)에 의존하고 있어 지키지 못함
    - OCP 위반 : 정책 변경시 OrderServiceImpl 코드도 변경되어야 한다.

구현 클래스의 의존성을 제거하고, 추상 클래스에만 의존하게 하려면 `OrderServiceImpl`에 `DiscountPolicy`를 대신 생성하고, 주입해줘야한다.

## AppConfig

애플리케이션의 전체 동작 방식을 구성(config)하기 위해, 구현 객체를 생성하고, 연결하는 책임을 가지는 별도의 설정 클래스를 생성한다.

```java
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
```

```java

public class MemberServiceImpl implements MemberService{

    /**
     * 생성자 DI를 통해 구현클래스 의존성 제거 => 실행에만 집중 가능
     */
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findByMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
```

`AppConfig`를 통해 의존성 주입을 다루고, 기존 서비스에서의 구현체 의존성을 제거하여 DIP를 지킬 수 있게 변경

**구성 영역(`AppConfig`)**은 정책이 변경되면 당연히 변경된다. 하지만, 사용영역은 변경이 필요없어진다.