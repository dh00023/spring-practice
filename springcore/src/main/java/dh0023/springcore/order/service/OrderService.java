package dh0023.springcore.order.service;

import dh0023.springcore.member.repository.MemberRepository;
import dh0023.springcore.member.repository.MemoryMemberRepository;
import dh0023.springcore.order.domain.Order;

public interface OrderService {

    Order createOrder( Long memberId, String itemName, int itemPrice);
}
