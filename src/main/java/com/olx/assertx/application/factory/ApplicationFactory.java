package com.olx.assertx.application.factory;

import com.olx.assertx.application.DropwizardTestApplication;
import com.olx.assertx.application.SpringTestApplication;
import com.olx.assertx.application.TestApplication;
import com.olx.assertx.application.model.ApplicationFramework;
import com.olx.assertx.configuration.UserTestConfiguration;

public class ApplicationFactory {

    public static TestApplication getTestApplication(ApplicationFramework framework,
                                                     UserTestConfiguration userTestConfiguration) {
        TestApplication testApplication = null;
        if (framework == ApplicationFramework.SPRING) {
            testApplication = new SpringTestApplication(userTestConfiguration);
        } else if (framework == ApplicationFramework.DROPWIZARD) {
            testApplication = new DropwizardTestApplication(userTestConfiguration);
        }
        return testApplication;
    }

}
