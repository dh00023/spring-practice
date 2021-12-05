package io.spring.batch.javagradle.book.example.total.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@Getter @Setter @ToString
public class Nstatement {
    private final Ncustomer customer;
    private List<Naccount> accounts = new ArrayList<>();

    public Nstatement(Ncustomer customer, List<Naccount> accounts) {
        this.customer = customer;
        this.accounts.addAll(accounts);
    }
    public Nstatement(Ncustomer customer) {
        this.customer = customer;
    }

}
