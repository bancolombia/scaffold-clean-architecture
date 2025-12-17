package co.com.bancolombia.api.config;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class CustomApiVersionParserTest {

    private CustomApiVersionParser parser;

    @BeforeEach
    void setUp() {
        parser = new CustomApiVersionParser();
    }

    @Test
    void shouldRemoveLowercaseVPrefix() {
        String result = parser.parseVersion("v1");
        assertEquals("1.0", result);
    }

    @Test
    void shouldRemoveUppercaseVPrefix() {
        String result = parser.parseVersion("V1");
        assertEquals("1.0", result);
    }

    @Test
    void shouldReturnVersionWhenNoPrefixExists() {
        String result = parser.parseVersion("1");
        assertEquals("1.0", result);
    }

    @Test
    void shouldHandleMultipleDigitVersions() {
        String result = parser.parseVersion("v10");
        assertEquals("10.0", result);
    }

    @Test
    void shouldHandleEmptyString() {
        String result = parser.parseVersion("");
        assertEquals(".0", result);
    }

    @Test
    void shouldHandleVersionWithDots() {
        String result = parser.parseVersion("v1.0.0");
        assertEquals("1.0.0", result);
    }

    @Test
    void shouldHandleVersionWithAlphanumeric() {
        String result = parser.parseVersion("v1-alpha");
        assertEquals("1-alpha.0", result);
    }

    @Test
    void shouldNotRemoveVInMiddleOfString() {
        String result = parser.parseVersion("1v2");
        assertEquals("1v2.0", result);
    }

    @Test
    void shouldHandleSingleVCharacter() {
        String result = parser.parseVersion("v");
        assertEquals(".0", result);
    }

    @Test
    void shouldHandleSingleUppercaseVCharacter() {
        String result = parser.parseVersion("V");
        assertEquals(".0", result);
    }

    @Test
    void shouldAddDotZeroToSingleNumber() {
        String result = parser.parseVersion("2");
        assertEquals("2.0", result);
    }

    @Test
    void shouldNotAddDotZeroWhenDotExists() {
        String result = parser.parseVersion("1.5");
        assertEquals("1.5", result);
    }

    @Test
    void shouldHandleVersionWithVPrefixAndDot() {
        String result = parser.parseVersion("v2.1");
        assertEquals("2.1", result);
    }

    @Test
    void shouldAddDotZeroAfterRemovingVPrefix() {
        String result = parser.parseVersion("v5");
        assertEquals("5.0", result);
    }

}
