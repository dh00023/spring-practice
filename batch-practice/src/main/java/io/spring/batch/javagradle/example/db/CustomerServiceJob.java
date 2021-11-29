package io.spring.batch.javagradle.example.db;

import io.spring.batch.javagradle.example.db.common.domain.Customer;
import io.spring.batch.javagradle.example.db.common.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * --job.name=serviceItemReaderJob city=Racine
 */
@RequiredArgsConstructor
@Configuration
public class CustomerServiceJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final CustomerService customerService;

    @Bean
    public Job serviceItemReaderJob(){
        return jobBuilderFactory.get("serviceItemReaderJob")
                .start(serviceItemReaderStep())
                .build();
    }

    @Bean
    public Step serviceItemReaderStep(){
        return stepBuilderFactory.get("serviceItemReaderStep")
                .<Customer, Customer>chunk(10)
                .reader(customerServiceItemReader())
                .writer(customerServiceItemWriter())
                .build();
    }

    @Bean
    public ItemReaderAdapter<Customer> customerServiceItemReader() {
        ItemReaderAdapter<Customer> adapter = new ItemReaderAdapter<>();

        adapter.setTargetObject(customerService);
        adapter.setTargetMethod("getCustomer");

        return adapter;
    }

    @Bean
    public ItemWriter customerServiceItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }
}
