package io.spring.batch.javagradle.book.example.total.domain;

import io.spring.batch.javagradle.book.example.total.serializer.JaxbDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "transaction")
public class Ntransaction {

    private long transactionId;
    private long accountId;
    private String description;
    private BigDecimal credit;
    private BigDecimal debit;
    private Date timestamp;

    @XmlJavaTypeAdapter(JaxbDateSerializer.class) // String -> Date 변환
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getTransactionAmount() {
        if (credit != null) {
            if (debit != null) {
                return credit.add(debit);
            } else {
                return credit;
            }
        } else if (debit != null) {
            return debit;
        } else {
            return BigDecimal.ONE;
        }
    }
}
