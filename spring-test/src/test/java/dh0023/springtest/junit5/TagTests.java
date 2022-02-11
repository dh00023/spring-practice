package dh0023.springtest.junit5;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


public class TagTests {

    @Test
    @Tag("fast")
    void fast_test() {

    }


    @Test
    @Tag("fast")
    void fast_test2() {

    }


    @Test
    @Tag("slow")
    void slow_test() {

    }


    @Test
    @Tag("slow")
    void slow_test2() {

    }
}
