package io.spring.batch.javagradle.book.basic.db.paging.job;

import io.spring.batch.javagradle.book.basic.db.common.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.HibernatePagingItemReader;
import org.springframework.batch.item.database.builder.HibernatePagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.Collections;

/**
 * --job.name=hibernatePagingItemReaderJob city=Chicago
 */
@RequiredArgsConstructor
@Configuration
public class HibernatePagingCustomerJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job hibernatePagingItemReaderJob() {
        return jobBuilderFactory.get("hibernatePagingItemReaderJob")
                .start(hibernatePagingItemReaderStep())
                .build();
    }

    @Bean
    public Step hibernatePagingItemReaderStep() {
        return stepBuilderFactory.get("hibernatePagingItemReaderStep")
                .<Customer, Customer>chunk(10)
                .reader(customerHibernatePagingItemReader(null))
                .writer(customerHibernatePagingItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public HibernatePagingItemReader<Customer> customerHibernatePagingItemReader(@Value("#{jobParameters['city']}") String city) {
        return new HibernatePagingItemReaderBuilder<Customer>()
                .name("customerHibernatePagingItemReader") // Reader??? ??????, ExecutionContext??? ??????????????? ??????
                .sessionFactory(entityManagerFactory.unwrap(SessionFactory.class))
                .queryString("from Customer where city = :city")        // HQL ??????
                .parameterValues(Collections.singletonMap("city", city)) // SQL ?????? ??????????????? ????????????
                .pageSize(10)       // Cursor??? ????????? ?????????! pageSize ??????
                .build();
    }

    @Bean
    public ItemWriter customerHibernatePagingItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }
}
