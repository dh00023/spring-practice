package io.spring.batch.javagradle.book.example.total.validator;

import io.spring.batch.javagradle.book.example.total.domain.NcustomerUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import org.springframework.batch.item.validator.ValidationException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


public class NcustomerValidatorTests {

    /**
     * 외부 의존성이 필요하므로 Mock객체로 생성하기위한
     * 어노테이션 설정
     */
    @Mock
    private NamedParameterJdbcTemplate template;

    private NcustomerValidator ncustomerValidator;

    @BeforeEach
    public void setUp() {

        /**
         * 어노테이션 선언한 Mock 객체 생성
         * 기존 initMocks는 deperecated
         */
        MockitoAnnotations.openMocks(this);

        this.ncustomerValidator = new NcustomerValidator(this.template);
    }

    @Test
    public void valid_customer_테스트() {
        // given
        NcustomerUpdate customer = new NcustomerUpdate(5L);

        // when
        // ArgumentCaptor를 사용해 capture를 통해 유연하게 아규먼트 값을 넘길 수 잇다.
        ArgumentCaptor<Map<String, Long>> params = ArgumentCaptor.forClass(Map.class);
        // 생성한 mock 객체에 조건 지정
        // ArgumentMatchers.eq()로 특정 타입 지정
        when(this.template.queryForObject(eq(NcustomerValidator.FIND_CUSTOMER_QUERY), params.capture(), eq(Long.class)))
                .thenReturn(2L);

        this.ncustomerValidator.validate(customer);

        // then
        assertEquals(5L, (long) params.getValue().get("id"));
    }

    @Test
    public void invalid_customer_테스트() {
        // given
        NcustomerUpdate customer = new NcustomerUpdate(5L);

        // when
        // ArgumentCaptor를 사용해 capture를 통해 유연하게 아규먼트 값을 넘길 수 잇다.
        ArgumentCaptor<Map<String, Long>> params = ArgumentCaptor.forClass(Map.class);
        // 생성한 mock 객체에 조건 지정
        // ArgumentMatchers.eq()로 특정 타입 지정
        when(this.template.queryForObject(eq(NcustomerValidator.FIND_CUSTOMER_QUERY), params.capture(), eq(Long.class)))
                .thenReturn(0L);

        // 예외발생확인
        Throwable exception = assertThrows(ValidationException.class, () -> this.ncustomerValidator.validate(customer));

        // then
        assertEquals("CustomerId 5가 존재하지 않습니다.", exception.getMessage());
    }

}