package io.spring.batch.javagradle.example.file.common.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
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

}
