package io.spring.batch.javagradle.book.example.total.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter @Setter
public class NcustomerContactUpdate extends NcustomerUpdate{
    private final String emailAddress;
    private final String homePhone;
    private final String cellPhone;
    private final String workPhone;
    private final Integer notificationPreferences;

    public NcustomerContactUpdate(long customerId, String emailAddress, String homePhone, String cellPhone
            , String workPhone, Integer notificationPreferences) {
        super(customerId);
        this.emailAddress = StringUtils.hasText(emailAddress) ? emailAddress : null;
        this.homePhone = StringUtils.hasText(homePhone) ? homePhone : null;
        this.cellPhone = StringUtils.hasText(cellPhone) ? cellPhone : null;
        this.workPhone = StringUtils.hasText(workPhone) ? workPhone : null;
        this.notificationPreferences = notificationPreferences;
    }
}
