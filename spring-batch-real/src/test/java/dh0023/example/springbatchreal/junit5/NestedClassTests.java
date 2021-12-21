package dh0023.example.springbatchreal.junit5;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("@Nested 테스트")
public class NestedClassTests {

    @Test
    @DisplayName("NestedCalssTest test")
    public void test() {
    }

    @Nested
    @DisplayName("Inner Test")
    class InnerTest {

        @DisplayName("InnerTest test")
        @Test
        void test() {

        }

        @Nested
        @DisplayName("Inner Inner Test")
        class InnerInnerTest{
            @DisplayName("InnerInnerTest test")
            @Test
            void test() {

            }
        }
    }
}
