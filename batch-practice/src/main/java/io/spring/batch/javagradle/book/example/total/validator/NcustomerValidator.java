package io.spring.batch.javagradle.book.example.total.validator;

import io.spring.batch.javagradle.book.example.total.domain.NcustomerUpdate;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;

@Component
public class NcustomerValidator implements Validator<NcustomerUpdate> {

    // binding시 문자열로 매핑
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public static final String FIND_CUSTOMER_QUERY = "SELECT COUNT(*) FROM NCUSTOMER WHERE customer_id = :id";

    public NcustomerValidator(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Autowired
    public NcustomerValidator(NamedParameterJdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    @Override
    public void validate(NcustomerUpdate customer) throws ValidationException {
        Map<String, Long> params = Collections.singletonMap("id", customer.getCustomerId());

        Long count = jdbcTemplate.queryForObject(FIND_CUSTOMER_QUERY, params, Long.class);

        if (count == 0) {
            throw new ValidationException(String.format("CustomerId %s가 존재하지 않습니다.", customer.getCustomerId()));
        }


    }
}
