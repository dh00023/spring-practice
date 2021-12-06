package io.spring.batch.javagradle.book.basic.db.cursor.job;

import io.spring.batch.javagradle.book.basic.configurer.BasicBatchConfigurer;
import io.spring.batch.javagradle.book.basic.db.common.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManagerFactory;
import java.util.Collections;

/**
 * --job.name=jpaCursorItemReaderJob city=Chicago
 */
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "jpaCursorItemReaderJob")
@Import(BasicBatchConfigurer.class)
public class JpaCursorCustomerJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
//    private final JobRegistry jobRegistry;

    @Bean
    public Job job() throws DuplicateJobException {

        Job job = jobBuilderFactory.get("jpaCursorItemReaderJob")
                .start(jpaCursorItemReaderStep())
                .build();
//        ReferenceJobFactory factory = new ReferenceJobFactory(job);
//        jobRegistry.register(factory);

        return job;
    }

    @Bean
    public Step jpaCursorItemReaderStep(){
        return stepBuilderFactory.get("jpaCursorItemReaderStep")
                .<Customer, Customer>chunk(10)
                .reader(customerJpaCursorItemReader(null))
                .writer(customerJpaCursorItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaCursorItemReader<Customer> customerJpaCursorItemReader(@Value("#{jobParameters['city']}") String city) {
        return new JpaCursorItemReaderBuilder<Customer>()
                .name("customerJpaCursorItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select c from Customer c where c.city = :city")
                .parameterValues(Collections.singletonMap("city", city))
                .build();
    }

    @Bean
    public ItemWriter customerJpaCursorItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }
}
