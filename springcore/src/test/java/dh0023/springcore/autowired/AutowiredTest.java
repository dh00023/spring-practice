package dh0023.springcore.autowired;

import dh0023.springcore.member.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.Nullable;

import java.util.Optional;

public class AutowiredTest {

    @Test
    void AutowiredOption() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestBean.class);
        TestBean bean = ac.getBean(TestBean.class);
    }

    static class TestBean {

        @Autowired(required = false)
        public void setNoBean(Member member) {
            System.out.println("member = " + member);
        }


        @Autowired
        public void setNoBean2(@Nullable Member member) {
            System.out.println("member2 = " + member);
        }

        @Autowired
        public void setNoBean3(Optional<Member> member) {
            System.out.println("member3 = " + member);
        }

    }
}
