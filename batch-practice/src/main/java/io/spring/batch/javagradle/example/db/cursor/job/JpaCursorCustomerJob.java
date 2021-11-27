package io.spring.batch.javagradle.example.db.cursor.job;

import io.spring.batch.javagradle.example.db.common.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.Collections;

/**
 * --job.name=jpaCursorItemReaderJob city=Chicago
 */
@RequiredArgsConstructor
@Configuration
public class JpaCursorCustomerJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaCursorItemReaderJob(){
        return jobBuilderFactory.get("jpaCursorItemReaderJob")
                .start(jpaCursorItemReaderStep())
                .build();
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
