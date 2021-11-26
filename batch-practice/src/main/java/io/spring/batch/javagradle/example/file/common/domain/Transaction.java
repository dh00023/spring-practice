package io.spring.batch.javagradle.example.file.common.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@ToString
@XmlType(name = "transaction")
public class Transaction {
    private String accountNumber;
    private Date transactionDate;
    private Double amount;

    @Setter(value = AccessLevel.NONE)
    private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

}
