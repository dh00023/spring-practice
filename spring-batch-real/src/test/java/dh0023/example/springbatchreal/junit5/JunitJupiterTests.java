package dh0023.example.springbatchreal.junit5;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.util.StringUtils;

import static org.assertj.core.api.Assertions.*;

public class JunitJupiterTests {

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Before All");
    }


    @BeforeEach
    void beforeEach() {
        System.out.println("Before Each");
    }

    @Test
    void test1() {
        System.out.println("test1");
    }


    @Test
    void test2() {
        System.out.println("test2");
        assertThat(StringUtils.hasText("")).isFalse();
    }

    @AfterAll
    public static void afterAll() {
        System.out.println("After All");
    }


    @AfterEach
    void afterEach() {
        System.out.println("After Each");
    }
}
