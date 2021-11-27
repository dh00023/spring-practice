package io.spring.batch.javagradle.example.file.common.domain;

import lombok.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@XmlRootElement(name = "customer")
public class Customer {

    private Long id;
    private String firstName;
    private String middleInitial;
    private String lastName;
    private String addressNumber;
    private String street;
    private String city;
    private String state;
    private String zipCode;

    private String address; // customAddressMapper

    private List<Transaction> transactions;

    @XmlElementWrapper(name = "transactions")
    @XmlElement(name = "transaction")
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

}
