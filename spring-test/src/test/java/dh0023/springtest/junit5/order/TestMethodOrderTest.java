package dh0023.springtest.junit5.order;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class TestMethodOrderTest {

    @Order(1)
    @Test
    void test1() {

    }

    @Order(2)
    @Test
    void test2() {

    }


    @Order(3)
    @Test
    void test3() {

    }
}
