package io.spring.batch.javagradle.example.file.common.processor;

import io.spring.batch.javagradle.example.file.common.domain.Customer;
import org.springframework.batch.item.ItemProcessor;

public class EvenFilteringItemProcessor implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer item) throws Exception {
        return Integer.parseInt(item.getZipCode()) % 2 == 0 ? null : item;
    }
}
