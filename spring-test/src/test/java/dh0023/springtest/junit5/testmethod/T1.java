package dh0023.springtest.junit5.testmethod;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class T1 {

    static Stream<Arguments> members() {
        return Stream.of(
                Arguments.arguments("faker", "26", "mid"),
                Arguments.arguments("bang", "26", "bottom"),
                Arguments.arguments("wolf", "26", "support")
        );
    }

}
