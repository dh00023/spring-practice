package dh0023.springtest.junit5.condition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

public class EnvironmentTest {
    @Test
    @EnabledIfEnvironmentVariable(named = "JUnit", matches = "5")
    void testEnabledIfEnvironmentVariable() {
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "JUnit", matches = "5")
    void testDisabledIfEnvironmentVariable() {
    }
}
