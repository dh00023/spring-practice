package io.spring.batch.javagradle.book.example.total.validator;

import io.spring.batch.javagradle.book.example.total.domain.NcustomerUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * @JdbcTest : Spring Data Jdbc를 사용하지 않고 오직 DataSource를 필요로하는 테스트에서 사용
 * in-memory embedded db가 설정되어 테스트를 위한 JdbcTemplate 생성
 * 일반적인 @ConfigurationProperties와 @Component 빈들은 스캔되지 않는다.
 * @ExtendWith(SpringExtension.class) : Spring을 사용
 */
@JdbcTest
//@ExtendWith(SpringExtension.class)
public class NcustomerValidatorIntegrationTests {

    @Autowired
    private DataSource dataSource;

    private NcustomerValidator ncustomerValidator;

    @BeforeEach
    public void setUp() {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(this.dataSource);

        this.ncustomerValidator = new NcustomerValidator(template);
    }

    @Test
    public void 고객없는경우_EXCEPTION_테스트() {

        // given
        NcustomerUpdate ncustomerUpdate = new NcustomerUpdate(-5);

        // when
        ValidationException exception = assertThrows(ValidationException.class, () -> this.ncustomerValidator.validate(ncustomerUpdate));

        // then
        assertEquals("CustomerId -5가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    public void 고객있는경우_테스트() {

        // given
        NcustomerUpdate ncustomerUpdate = new NcustomerUpdate(5);

        this.ncustomerValidator.validate(ncustomerUpdate);
    }


}