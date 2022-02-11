package io.spring.batch.javagradle.book.example.total.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter @Setter
public class NcustomerNameUpdate extends NcustomerUpdate {

    private final String firstName;
    private final String middleName;
    private final String lastName;

    public NcustomerNameUpdate(long customerId, String firstName, String middleName, String lastName) {
        super(customerId);
        this.firstName = StringUtils.hasText(firstName) ? firstName : null;
        this.lastName = StringUtils.hasText(lastName) ? lastName : null;
        this.middleName = StringUtils.hasText(middleName) ? middleName : null;
    }

}
