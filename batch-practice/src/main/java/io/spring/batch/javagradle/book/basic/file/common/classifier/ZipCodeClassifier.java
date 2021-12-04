package io.spring.batch.javagradle.book.basic.file.common.classifier;

import io.spring.batch.javagradle.book.basic.file.common.domain.Customer;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.Classifier;

@AllArgsConstructor
public class ZipCodeClassifier implements Classifier<Customer, ItemProcessor<Customer, Customer>> {

    private ItemProcessor<Customer, Customer> oddProcessor;
    private ItemProcessor<Customer, Customer> evenProcessor;


    @Override
    public ItemProcessor<Customer, Customer> classify(Customer classifiable) {
        if (Integer.parseInt(classifiable.getZipCode()) % 2 == 0) {
            return evenProcessor;
        } else {
            return oddProcessor;
        }
    }
}
