package io.spring.batch.javagradle.example.file;

import com.mysql.cj.exceptions.PasswordExpiredException;
import io.spring.batch.javagradle.example.file.common.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class SkipRecordJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step skipRecordCopyFileStep() {
        return this.stepBuilderFactory.get("skipRecordCopyFileStep")
                .<Customer, Customer>chunk(10)
                .reader(null)
                .writer(null)
                .faultTolerant()
                .skip(Exception.class)
                .noSkip(ParseException.class)
                .skipLimit(10)
                .build();
    }

}
