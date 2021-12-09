package io.spring.batch.javagradle.book.example.total.repository;

import io.spring.batch.javagradle.book.example.total.domain.Ncustomer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NcustomerRepository extends JpaRepository<Ncustomer, Long> {
    Page<Ncustomer> findByCity(String city, Pageable pageRequest);
}
