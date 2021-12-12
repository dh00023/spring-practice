package dh0023.example.springbatchreal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SpringBatchRealApplication {

    public static void main(String[] args) {
        // main thread가 종료되면 jvm 강제 종료
        // main thread가 종료됐다는 것은 자식 thread도 모두 종료됐다는 것을 보장
//        SpringApplication.run(SpringBatchRealApplication.class, args);
        System.exit(SpringApplication.exit(SpringApplication.run(SpringBatchRealApplication.class, args)));
    }

}
