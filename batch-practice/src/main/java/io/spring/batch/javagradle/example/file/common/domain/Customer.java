package io.spring.batch.javagradle.example.file.common.domain;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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

    @NotNull(message = "firstname은 필수값입니다.")
    @Pattern(regexp = "[a-zA-Z]+", message = "firstname은 영어여야합니다.")
    private String firstName;

    @Size(min=1, max=1)
    @Pattern(regexp = "[a-zA-Z]", message = "middleInitial는 반드시 한자리 영어여야합니다.")
    private String middleInitial;

    @NotNull(message = "lastName 필수값입니다.")
    @Pattern(regexp = "[a-zA-Z]+", message = "lastName 영어여야합니다.")
    private String lastName;

    private String addressNumber;
    private String street;

    @NotNull(message = "city 필수값입니다.")
    @Pattern(regexp = "[a-zA-Z\\. ]+")
    private String city;

    @NotNull(message = "state 필수값입니다.")
    @Size(min=2, max=2)
    @Pattern(regexp = "[A-Z{2}]+")
    private String state;

    @NotNull(message = "zipCode 필수값입니다.")
    @Size(min=5, max=5)
    @Pattern(regexp = "[\\d{5}]+")
    private String zipCode;

    @Pattern(regexp = "[0-9a-zA-Z\\. ]+")
    private String address; // customAddressMapper

    private List<Transaction> transactions;

    @XmlElementWrapper(name = "transactions")
    @XmlElement(name = "transaction")
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

}
