package dh0023.springcore;

import dh0023.springcore.member.domain.Grade;
import dh0023.springcore.member.domain.Member;
import dh0023.springcore.member.service.MemberService;
import dh0023.springcore.member.service.MemberServiceImpl;

public class MemberApp {

    public static void main(String[] args) {
        MemberService memberService = new MemberServiceImpl();

        Member member = new Member(1L, "memberA", Grade.VIP);
        memberService.join(member);

        Member member1 = memberService.findByMember(1L);

        System.out.println("new member => " + member.getName());
        System.out.println("findBy member => " + member1.getName());
    }
}
