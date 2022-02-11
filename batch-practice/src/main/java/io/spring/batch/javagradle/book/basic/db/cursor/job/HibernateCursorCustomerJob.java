package io.spring.batch.javagradle.book.basic.db.cursor.job;

import io.spring.batch.javagradle.book.basic.db.common.configurer.HibernateBatchConfigurer;
import io.spring.batch.javagradle.book.basic.db.common.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.HibernateCursorItemReader;
import org.springframework.batch.item.database.builder.HibernateCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManagerFactory;
import java.util.Collections;

/**
 * --job.name=hibernateCursorItemReaderJob city=Chicago
 */
@RequiredArgsConstructor
@Configuration
@Import(HibernateBatchConfigurer.class)
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "hibernateCursorItemReaderJob")
public class HibernateCursorCustomerJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("hibernateCursorItemReaderJob")
                .start(hibernateCursorItemReaderStep())
                .build();
    }

    @Bean
    public Step hibernateCursorItemReaderStep() {
        return stepBuilderFactory.get("hibernateCursorItemReaderStep")
                .<Customer, Customer>chunk(10)
                .reader(customerHibernateCursorItemReader(null))
                .writer(customerHibernateCursorItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public HibernateCursorItemReader<Customer> customerHibernateCursorItemReader(@Value("#{jobParameters['city']}") String city) {
        return new HibernateCursorItemReaderBuilder<Customer>()
                .name("customerHibernateCursorItemReader") // Reader의 이름, ExecutionContext에 저장되어질 이름
                .sessionFactory(entityManagerFactory.unwrap(SessionFactory.class))
                .queryString("from Customer where city = :city")        // HQL 쿼리
                .parameterValues(Collections.singletonMap("city", city)) // SQL 문에 주입해야할 파라미터
                .build();
    }

    @Bean
    public ItemWriter customerHibernateCursorItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }
}
