package io.spring.batch.javagradle.book.basic.db.common.repository;

import io.spring.batch.javagradle.book.basic.db.common.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Page<Customer> findByCity(String city, Pageable pageRequest);
}
