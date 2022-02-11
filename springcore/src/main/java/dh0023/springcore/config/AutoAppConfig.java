package dh0023.springcore.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @ComponentScan은 @Component 어노테이션이 붙은 클래스를 빈으로 등록해준다.
 * 기본패키지를 설정해주지 않으면, 현재 패키지 하위로 설정된다.
 * 예외하고 싶은 클래스가 있는 경우 excludeFilters로 설정할 수 있다.
 */
@Configuration
@ComponentScan(
        basePackages = "dh0023.springcore",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {

}
