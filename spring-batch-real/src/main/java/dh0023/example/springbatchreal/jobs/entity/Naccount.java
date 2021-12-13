package dh0023.example.springbatchreal.jobs.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity // 매핑할 객체가 Entity임을 나타냄
public class Naccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    private BigDecimal balance;
    private Date lastStatementDate;

}
