package dh0023.example.springbatchreal.jobs.mybatis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@AllArgsConstructor
public class Ncustomer {
    private Long customerId;
    private String fullName;
    private String address;
    private String postalCode;
}
