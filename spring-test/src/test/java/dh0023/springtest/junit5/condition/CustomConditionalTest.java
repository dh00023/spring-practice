package dh0023.springtest.junit5.condition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.extension.ExtendWith;

public class CustomConditionalTest {
    @Test
    @DisabledIf("customCondition")
    void disabled() {

    }

    @Test
    @EnabledIf("customCondition")
    void enabled() {

    }

    boolean customCondition() {
        return true;
    }
}
