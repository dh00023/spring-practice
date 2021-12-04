package io.spring.batch.javagradle.book.basic.db.paging.job;

import io.spring.batch.javagradle.book.basic.db.common.domain.Customer;
import io.spring.batch.javagradle.book.basic.db.common.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;

/**
 * --job.name=repositoryItemReaderJob city=Racine
 */
@RequiredArgsConstructor
@Configuration
public class RepositoryCustomerJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final CustomerRepository customerRepository;

    @Bean
    public Job repositoryItemReaderJob(){
        return jobBuilderFactory.get("repositoryItemReaderJob")
                .start(repositoryItemReaderStep())
                .build();
    }

    @Bean
    public Step repositoryItemReaderStep(){
        return stepBuilderFactory.get("repositoryItemReaderStep")
                .<Customer, Customer>chunk(10)
                .reader(customerRepositoryItemReader(null))
                .writer(customerRepositoryItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Customer> customerRepositoryItemReader(@Value("#{jobParameters['city']}") String city) {
        return new RepositoryItemReaderBuilder<Customer>()
                .name("customerRepositoryItemReader")
                .arguments(Collections.singletonList(city)) // pageable 파라미터를 제외한 arguments
                .methodName("findByCity")                   // 호출할 메서드명
                .repository(customerRepository)             // Repository 구현체
                .sorts(Collections.singletonMap("lastName", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemWriter customerRepositoryItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }
}
