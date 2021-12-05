package io.spring.batch.javagradle.book.example.total.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class NcustomerUpdate {
    protected final long customerId;
}
