package com.olx.assertx.utils;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvYamlConstructor extends Constructor {
    public static final Tag ENV_TAG = new Tag("!ENV");
    public static final Pattern ENV_FORMAT =
            Pattern.compile("^\\$\\{\\s*((?<name>\\w+)((?<separator>:?(-|\\?))(?<value>\\S+)?)?)\\s*\\}(?<rest>\\S+)$");

    public EnvYamlConstructor() {
        super(new LoaderOptions());
        this.yamlConstructors.put(ENV_TAG, new ConstructEnv());
    }

    public EnvYamlConstructor(TypeDescription theRoot, Collection<TypeDescription> moreTDs, LoaderOptions loadingConfig) {
        super(theRoot, moreTDs, loadingConfig);
        this.yamlConstructors.put(ENV_TAG, new ConstructEnv());
    }

    public String apply(String name, String separator, String value, String environment) {
        if (environment != null && !environment.isEmpty()) {
            return environment;
        } else {
            if (separator != null) {
                if (separator.equals("?") && environment == null) {
                    throw new MissingEnvironmentVariableException("Missing mandatory variable " + name + ": " + value);
                }

                if (separator.equals(":?")) {
                    if (environment == null) {
                        throw new MissingEnvironmentVariableException("Missing mandatory variable " + name + ": " + value);
                    }

                    if (environment.isEmpty()) {
                        throw new MissingEnvironmentVariableException("Empty mandatory variable " + name + ": " + value);
                    }
                }

                if (separator.startsWith(":")) {
                    if (environment == null || environment.isEmpty()) {
                        return value;
                    }
                } else if (environment == null) {
                    return value;
                }
            }

            return "";
        }
    }

    public static String getEnv(String key) {
        return System.getenv(key);
    }

    private class ConstructEnv extends AbstractConstruct {
        private ConstructEnv() {
        }

        public Object construct(Node node) {
            String val = EnvYamlConstructor.this.constructScalar((ScalarNode)node);
            Matcher matcher = ENV_FORMAT.matcher(val);
            matcher.matches();
            String name = matcher.group("name");
            String value = matcher.group("value");
            String separator = matcher.group("separator");
            String rest = matcher.group("rest");
            return apply(name, separator, value != null ? value : "", EnvYamlConstructor.getEnv(name)) + rest;
        }
    }
}
