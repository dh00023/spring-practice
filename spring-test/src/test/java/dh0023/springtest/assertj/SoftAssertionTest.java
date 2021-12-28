package dh0023.springtest.assertj;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

public class SoftAssertionTest {

    @Test
    @Disabled("SoftAssertions 테스트하기 위해 일부로 실패시킨 예제")
    void test() {
        String str = "test";
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(StringUtils.hasText(str))
                    .as("check '%s' is not null", str)
                    .isFalse();

            softAssertions.assertThat(StringUtils.hasLength(str))
                    .as("check '%s' is not null", str)
                    .isTrue();
        });


    }
}
