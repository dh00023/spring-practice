package dh0023.springtest.junit5.testmethod;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.EnumSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ParameterizedMethodTest {

    @ParameterizedTest
    @ValueSource(strings = {"faker", "zeus", "oner", "keria", "gumayusi"})
    void parameterizedStringTest(String t1) {
        assertThat(StringUtils.hasText(t1));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 4, 100, 10204})
    void parameterizedIntTest(int i) {
        assertThat(i % 2).isEqualTo(0);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", " ", "\t", "\n"})
    void nullAndEmptyTest(String str) {
        assertThat(StringUtils.hasText(str)).isFalse();
    }

    @ParameterizedTest
    @EnumSource
    void MonthEnumTest(Month month) {
        assertThat(month).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(names = {"MAY", "JUNE"})
    void enumSourceIncludeModeTest(Month month) {
        assertThat(EnumSet.of(Month.JUNE, Month.MAY)).contains(month);
    }

    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = {"MAY", "JUNE"})
    void enumSourceExcludeModeTest(Month month) {
        assertThat(EnumSet.of(Month.JUNE, Month.MAY)).doesNotContain(month);
    }

    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.MATCH_ANY, names = {"^.*SDAY$", "^.*ESDAY$"})
    void enumSourceMatchAnyModeTest(DayOfWeek day) {
        assertThat(EnumSet.of(DayOfWeek.THURSDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)).contains(day);
    }

    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.MATCH_ALL, names = {"^.*SDAY$", "^.*ESDAY$"})
    void enumSourceMatchAllModeTest(DayOfWeek day) {
        assertThat(EnumSet.of(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)).contains(day);
    }


    @ParameterizedTest
    @MethodSource
    void stringMethodSourceTest(String str) {
        assertThat(StringUtils.hasText(str)).isTrue();
    }

    static Stream<String> stringMethodSourceTest() {
        return Stream.of("faker", "bang", "wolf");
    }

    @ParameterizedTest
    @MethodSource("range")
    void primitiveTypeMethodSourceTest(int i) {
        assertThat(i).isLessThan(5);
    }

    static IntStream range() {
        return IntStream.range(0, 5);
    }

    @ParameterizedTest
    @MethodSource("stringAndIntProvider")
    void multiParameterMethodSourceTest(String name, int age) {
        assertThat(name).isNotBlank();
        assertThat(age).isEqualTo(26);
    }

    static Stream<Arguments> stringAndIntProvider() {
        return Stream.of(
                Arguments.arguments("faker", "26"),
                Arguments.arguments("bang", "26")
        );
    }

    @ParameterizedTest
    @MethodSource("dh0023.springtest.junit5.testmethod.T1#members")
    void externalMethodSourceTest(String name, int age, String position) {
        assertThat(name).isNotBlank();
        assertThat(position).isNotBlank();
        assertThat(age).isEqualTo(26);
    }

    @ParameterizedTest
    @ArgumentsSource(value = MyArgumentsProvider.class)
    void argumentSourceTest(String fruit) {
        assertThat(fruit).isNotBlank();
    }

    static class MyArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of("apple", "banana").map(Arguments::of);
        }
    }

}
