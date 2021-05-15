package dh0023.springcore.lombok;

import dh0023.springcore.member.domain.Grade;
import dh0023.springcore.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class LombokTest {
    @Test
    @DisplayName("toString() 테스트")
    void getToString() {
        Member member = new Member(1L, "test123", Grade.VIP);

        System.out.println(member);
    }
}
