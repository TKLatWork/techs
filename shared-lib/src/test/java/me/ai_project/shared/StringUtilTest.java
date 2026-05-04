package me.ai_project.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {

    @Test
    void testCapitalize() {
        assertEquals("Hello", StringUtil.capitalize("hello"));
        assertEquals("World", StringUtil.capitalize("World"));
        assertNull(StringUtil.capitalize(null));
        assertEquals("", StringUtil.capitalize(""));
    }

    @Test
    void testIsBlank() {
        assertTrue(StringUtil.isBlank(null));
        assertTrue(StringUtil.isBlank(""));
        assertTrue(StringUtil.isBlank("   "));
        assertFalse(StringUtil.isBlank("hello"));
    }
}
