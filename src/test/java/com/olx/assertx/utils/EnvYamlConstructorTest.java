package com.olx.assertx.utils;

import org.junit.Test;
import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Covers environment substitution rules for https://github.com/olx-india/AssertX/issues/19.
 */
public class EnvYamlConstructorTest {
    private final EnvYamlConstructor constructor = new EnvYamlConstructor();

    @Test
    public void expandsVariableWithoutSeparator() {
        assertEquals("configured", constructor.apply("VAR", null, "", "configured"));
        assertEquals("", constructor.apply("VAR", null, "", null));
        assertEquals("", constructor.apply("VAR", null, "", ""));
    }

    @Test
    public void colonDashUsesDefaultForMissingOrEmptyVariable() {
        assertEquals("configured", constructor.apply("VAR", ":-", "default", "configured"));
        assertEquals("default", constructor.apply("VAR", ":-", "default", null));
        assertEquals("default", constructor.apply("VAR", ":-", "default", ""));
    }

    @Test
    public void dashUsesDefaultOnlyForMissingVariable() {
        assertEquals("configured", constructor.apply("VAR", "-", "default", "configured"));
        assertEquals("default", constructor.apply("VAR", "-", "default", null));
        assertEquals("", constructor.apply("VAR", "-", "default", ""));
    }

    @Test
    public void colonQuestionMarkRejectsMissingAndEmptyVariables() {
        assertEquals("configured", constructor.apply("VAR", ":?", "msg", "configured"));

        MissingEnvironmentVariableException missing = assertThrows(MissingEnvironmentVariableException.class,
                () -> constructor.apply("VAR", ":?", "msg", null));
        assertEquals("Missing mandatory variable VAR: msg", missing.getMessage());

        MissingEnvironmentVariableException empty = assertThrows(MissingEnvironmentVariableException.class,
                () -> constructor.apply("VAR", ":?", "msg", ""));
        assertEquals("Empty mandatory variable VAR: msg", empty.getMessage());
    }

    @Test
    public void questionMarkRejectsMissingVariableAndAcceptsEmptyVariable() {
        assertEquals("configured", constructor.apply("VAR", "?", "msg", "configured"));
        assertEquals("", constructor.apply("VAR", "?", "msg", ""));

        MissingEnvironmentVariableException missing = assertThrows(MissingEnvironmentVariableException.class,
                () -> constructor.apply("VAR", "?", "msg", null));
        assertEquals("Missing mandatory variable VAR: msg", missing.getMessage());
    }
}
