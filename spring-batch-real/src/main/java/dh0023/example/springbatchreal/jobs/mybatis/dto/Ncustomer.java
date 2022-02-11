package dh0023.example.springbatchreal.jobs.mybatis.dto;

import lombok.*;

@Getter @Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Ncustomer {
    private Long customerId;
    private String fullName;
    private String address;
    private String postalCode;
}
