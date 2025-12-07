package com.inktrack.core.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordCheckTest {

    @Test
    void testLowercasePattern() {
        assertTrue(PasswordCheck.LOWERCASE_PATTERN.matcher("a").find());
        assertFalse(PasswordCheck.LOWERCASE_PATTERN.matcher("A").find());
        assertFalse(PasswordCheck.LOWERCASE_PATTERN.matcher("1").find());
    }

    @Test
    void testUppercasePattern() {
        assertTrue(PasswordCheck.UPPERCASE_PATTERN.matcher("A").find());
        assertFalse(PasswordCheck.UPPERCASE_PATTERN.matcher("a").find());
        assertFalse(PasswordCheck.UPPERCASE_PATTERN.matcher("1").find());
    }

    @Test
    void testDigitPattern() {
        assertTrue(PasswordCheck.DIGIT_PATTERN.matcher("1").find());
        assertFalse(PasswordCheck.DIGIT_PATTERN.matcher("a").find());
        assertFalse(PasswordCheck.DIGIT_PATTERN.matcher("A").find());
    }

    @Test
    void testSpecialCharPattern() {
        assertTrue(PasswordCheck.SPECIAL_CHAR_PATTERN.matcher("@").find());
        assertTrue(PasswordCheck.SPECIAL_CHAR_PATTERN.matcher("#").find());
        assertFalse(PasswordCheck.SPECIAL_CHAR_PATTERN.matcher("a").find());
        assertFalse(PasswordCheck.SPECIAL_CHAR_PATTERN.matcher("1").find());
    }

    @Test
    void testLengthPattern() {
        assertTrue(PasswordCheck.LENGTH_PATTERN.matcher("12345678").find());
        assertFalse(PasswordCheck.LENGTH_PATTERN.matcher("1234567").find());
    }
}
