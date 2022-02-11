package dh0023.springcore.lifecycle;

import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

public class BeanLifeCycleTest {

    @Test
    @Description("Life Cycle 적용전 최초 테스트")
    void lifeCycleTestBeforeCycle() {
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
        NetworkClientBasic client = ac.getBean(NetworkClientBasic.class);
        ac.close();
    }

    @Test
    @Description("인터페이스 적용 테스트")
    void lifeCycleInterfaceTest() {
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
        NetworkClientInterface client = ac.getBean(NetworkClientInterface.class);
        ac.close();
    }

    @Test
    @Description("메서드 적용 테스트")
    void lifeCycleMethodTest() {
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
        NetworkClientMethod client = ac.getBean(NetworkClientMethod.class);
        ac.close();
    }

    @Test
    @Description("어노테이션 방 적용 테스트")
    void lifeCycleTest() {
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
        NetworkClient client = ac.getBean(NetworkClient.class);
        ac.close();
    }


    @Configuration
    static class LifeCycleConfig {
        @Bean
        public NetworkClientBasic networkClientBasic() {
            NetworkClientBasic networkClient = new NetworkClientBasic();
            networkClient.setUrl("http://spring-core.dev");
            return networkClient;
        }

        @Bean
        public NetworkClientInterface networkClientInterface() {
            NetworkClientInterface networkClientInterface = new NetworkClientInterface();
            networkClientInterface.setUrl("http://spring-core.dev");
            return networkClientInterface;
        }

        @Bean(initMethod = "init", destroyMethod = "close")
        public NetworkClientMethod networkClientMethod() {
            NetworkClientMethod networkClientMethod = new NetworkClientMethod();
            networkClientMethod.setUrl("http://spring-core.dev");
            return networkClientMethod;
        }

        @Bean
        public NetworkClient networkClient() {
            NetworkClient networkClient = new NetworkClient();
            networkClient.setUrl("http://spring-core.dev");
            return networkClient;
        }
    }
}
