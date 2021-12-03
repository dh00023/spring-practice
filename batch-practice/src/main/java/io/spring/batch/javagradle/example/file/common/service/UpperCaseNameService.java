package io.spring.batch.javagradle.example.file.common.service;

import io.spring.batch.javagradle.example.file.common.domain.Customer;
import org.springframework.stereotype.Service;

@Service
public class UpperCaseNameService {
    public Customer upperCase(Customer customer) {

        Customer newCustomer = new Customer(customer);

        newCustomer.setFirstName(newCustomer.getFirstName().toUpperCase());
        newCustomer.setLastName(newCustomer.getLastName().toUpperCase());
        newCustomer.setMiddleInitial(newCustomer.getMiddleInitial().toUpperCase());

        return newCustomer;
    }
}
