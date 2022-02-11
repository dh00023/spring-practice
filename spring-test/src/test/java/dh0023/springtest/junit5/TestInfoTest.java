package dh0023.springtest.junit5;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

@DisplayName("TestInfo 테스트")
public class TestInfoTest {

    TestInfoTest(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
        assertThat("TestInfo 테스트").isEqualTo(testInfo.getDisplayName());
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
        String displayName = testInfo.getDisplayName();
        assertThat("demo test").isEqualTo(testInfo.getDisplayName());
    }

    @Test
    @Tag("demo1")
    @DisplayName("demo test")
    void test(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
        assertThat(testInfo.getTags().contains("demo1")).isTrue();
    }


}
