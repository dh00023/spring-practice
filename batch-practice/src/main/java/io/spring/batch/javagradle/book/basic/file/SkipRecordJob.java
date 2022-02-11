package io.spring.batch.javagradle.book.basic.file;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class SkipRecordJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

//    @Bean
//    public Step skipRecordCopyFileStep() {
//        return this.stepBuilderFactory.get("skipRecordCopyFileStep")
//                .<Customer, Customer>chunk(10)
//                .reader(null)
//                .writer(null)
//                .faultTolerant()
//                .skip(Exception.class)
//                .noSkip(ParseException.class)
//                .skipLimit(10)
//                .build();
//    }

}
