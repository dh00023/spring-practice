package io.spring.batch.javagradle.example.db.paging.job;

import io.spring.batch.javagradle.example.db.common.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.Collections;

/**
 * --job.name=jpaPagingItemReaderJob city=Chicago
 */
@RequiredArgsConstructor
@Configuration
public class JpaPagingCustomerJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaPagingItemReaderJob(){
        return jobBuilderFactory.get("jpaPagingItemReaderJob")
                .start(jpaPagingItemReaderStep())
                .build();
    }

    @Bean
    public Step jpaPagingItemReaderStep(){
        return stepBuilderFactory.get("jpaPagingItemReaderStep")
                .<Customer, Customer>chunk(10)
                .reader(customerJpaPagingItemReader(null))
                .writer(customerJpaPagingItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Customer> customerJpaPagingItemReader(@Value("#{jobParameters['city']}") String city) {
        return new JpaPagingItemReaderBuilder<Customer>()
                .name("customerJpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select c from Customer c where c.city = :city")
                .parameterValues(Collections.singletonMap("city", city))
                .pageSize(10)
                .build();
    }

    @Bean
    public ItemWriter customerJpaPagingItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }
}
