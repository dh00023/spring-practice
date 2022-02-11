package dh0023.springtest.junit5.testmethod;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.TestInfo;

import static org.assertj.core.api.Assertions.assertThat;

public class RepeatedMethodTest {

    @RepeatedTest(5)
    void repetition_test(TestInfo testInfo, RepetitionInfo repetitionInfo) {
        assertThat(testInfo.getDisplayName()).isEqualTo("repetition " + repetitionInfo.getCurrentRepetition() + " of " + repetitionInfo.getTotalRepetitions());
        assertThat(repetitionInfo.getTotalRepetitions()).isEqualTo(5);
    }

    @RepeatedTest(value = 5, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    void repetition_name_test(RepetitionInfo repetitionInfo) {
        assertThat(repetitionInfo.getTotalRepetitions()).isEqualTo(5);
    }
}
