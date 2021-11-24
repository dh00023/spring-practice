package io.spring.batch.javagradle.basic.service;

public class CustomerService {

    public void serviceMethod(String message) {
        System.out.println("service method 호출");
        System.out.println(message);
    }
}
