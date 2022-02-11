package dh0023.springtest.junit5.condition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;

import static org.junit.jupiter.api.condition.OS.*;

public class OsTest {
    @Test
    @EnabledOnOs(MAC)
    void onlyOnMacOs() {
    }

    @Test
    @EnabledOnOs(WINDOWS)
    void onlyOnWindows() {
    }

    @Test
    @EnabledOnOs({LINUX, MAC})
    void onLinuxOrMac() {
    }

    @Test
    @DisabledOnOs(value = MAC,disabledReason = "MAC OS에서 테스트하지 않음")
    void notOnMacOs() {
    }

}
