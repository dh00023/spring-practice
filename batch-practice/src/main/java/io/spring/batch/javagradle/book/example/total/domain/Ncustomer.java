package io.spring.batch.javagradle.book.example.total.domain;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity // 매핑할 객체가 Entity임을 나타냄
@Table(name = "ncustomer") // Entityrㅏ 매핑되는 테이블 지정
public class Ncustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long customerId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postalCode;
    private String ssn;
    private String emailAddress;
    private String homePhone;
    private String cellPhone;
    private String workPhone;
    private int notificationPreferences;

}
