package dh0023.springcore.singleton;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.*;

class StatefulServiceTest {

    @Test
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
        StatefulService statefulSevice = ac.getBean(StatefulService.class);
        StatefulService statefulSevice2 = ac.getBean(StatefulService.class);

        int userAPrice = statefulSevice.order("userA", 10000); // ThreadA
        int userBPrice = statefulSevice2.order("userB", 20000); // ThreadB

        System.out.println("prc = " + userAPrice);

        assertThat(userAPrice).isEqualTo(10000);
        assertThat(userBPrice).isEqualTo(20000);
    }

    static class TestConfig {

        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }

}