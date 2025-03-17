package com.olx.assertx.application;

import com.olx.assertx.application.model.ApplicationFramework;

import java.util.ArrayList;
import java.util.List;

public interface TestApplication {
    String ARG_FORMAT = "--%s=%s";

    default String[] getArgs() {
        List<String> args = new ArrayList<>();
        addArgs(args);
        return args.toArray(new String[0]);
    }

    default void launch() throws Exception {
        if (isEnabled()) {
            start(getArgs());
        }
    }

    default void terminate() {
        if (isEnabled()) {
            stop();
        }
    }

    boolean isEnabled();

    void addArgs(List<String> args);

    void start(String[] args) throws Exception;

    void stop();

    ApplicationFramework getApplicationFramework();

}
