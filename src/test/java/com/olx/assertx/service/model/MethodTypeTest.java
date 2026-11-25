package com.olx.assertx.service.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Covers HTTP method parsing for https://github.com/olx-india/AssertX/issues/19.
 */
public class MethodTypeTest {
    @Test
    public void parsesMethodNamesRegardlessOfCase() {
        assertEquals(MethodType.GET, MethodType.fromString("GET"));
        assertEquals(MethodType.GET, MethodType.fromString("get"));
        assertEquals(MethodType.GET, MethodType.fromString("gEt"));
    }

    @Test
    public void rejectsInvalidMethodNames() {
        assertThrows(IllegalArgumentException.class, () -> MethodType.fromString("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> MethodType.fromString(""));
    }
}
