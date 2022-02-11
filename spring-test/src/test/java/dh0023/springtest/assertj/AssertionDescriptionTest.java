package dh0023.springtest.assertj;

import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertionDescriptionTest {

    @Test
    void asExample() {
        String blank = " ";
        assertThat(StringUtils.hasText(blank))
                .as("check '%s' is null", blank)
//                .isTrue();
                .isFalse();

    }
}
