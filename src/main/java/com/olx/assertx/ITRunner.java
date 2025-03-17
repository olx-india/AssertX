package com.olx.assertx;

import com.olx.assertx.application.TestApplication;
import com.olx.assertx.application.factory.ApplicationFactory;
import com.olx.assertx.application.model.ApplicationFramework;
import com.olx.assertx.configuration.FrameworkConfiguration;
import com.olx.assertx.configuration.UserTestConfiguration;
import com.olx.assertx.mocks.builder.DockerComposeBuilder;
import com.olx.assertx.service.model.Profile;
import com.olx.assertx.service.model.PropertyKey;
import com.olx.assertx.utils.EnvYamlConstructor;
import com.olx.assertx.utils.FileUtility;
import com.olx.assertx.utils.Paths;
import com.olx.assertx.utils.PortManager;
import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.configuration.ShutdownStrategy;
import com.palantir.docker.compose.connection.DockerMachine;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@RunWith(Cucumber.class)
@CucumberOptions(
        glue = {"com.olx.assertx.stepdefinitions"},
        plugin = {"json:target/cucumber-reports/cucumber.json"},
        monochrome = true
)
public class ITRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ITRunner.class);
    private static FrameworkConfiguration frameworkConfiguration;
    private static UserTestConfiguration userTestConfiguration;
    private static Profile profile = Profile.LOCAL;

    @ClassRule
    public static DockerComposeRule containerRule;

    @BeforeClass
    public static void setup() throws Exception {
        frameworkConfiguration = (FrameworkConfiguration) loadConfig(Paths.FRAMEWORK_CONFIG,
                FrameworkConfiguration.class);

        if (System.getProperties().containsKey(PropertyKey.PROFILE.getValue())) {
            LOGGER.info("Profile: " + System.getProperty(PropertyKey.PROFILE.getValue()));
            profile = Profile.fromString(System.getProperty(PropertyKey.PROFILE.getValue()));
        }
        System.setProperty(PropertyKey.DOCKER_HOST.getValue(),
                frameworkConfiguration.getProfileConfig().get(profile).getDockerHost());

        LOGGER.info("Set assertx profile=" + profile);

        userTestConfiguration = (UserTestConfiguration) loadConfig(Paths.USER_TEST_CONFIG,
                UserTestConfiguration.class);

        DockerComposeBuilder.build(userTestConfiguration.getMocks(), frameworkConfiguration);

        launchContainers();

        launchApplication();

    }

    private static void launchContainers() throws IOException, InterruptedException {
        LOGGER.info("Launch docker containers");
        containerRule =
                DockerComposeRule.builder()
                        .machine(DockerMachine.localMachine().build())
                        .file(Paths.DOCKER_COMPOSE_FILE)
                        .saveLogsTo(Paths.LOGS_FOLDER)
                        .shutdownStrategy((dockerCompose, docker) -> {
                            ShutdownStrategy.KILL_DOWN.shutdown(dockerCompose, docker);
                            dockerCompose.rm();
                        })
                        .removeConflictingContainersOnStartup(true)
                        .build();
        containerRule.dockerCompose().build();
        containerRule.after();
        containerRule.before();
    }

    private static Object loadConfig(String configPath, Class<?> className) throws IOException {
        LOGGER.info("Load config from path={} as class={}", configPath, className);
        Yaml yaml = new Yaml(new EnvYamlConstructor());
        yaml.addImplicitResolver(EnvYamlConstructor.ENV_TAG, EnvYamlConstructor.ENV_FORMAT, "$");
        try (InputStream in = ITRunner.class.getResourceAsStream(configPath)) {
            return yaml.loadAs(in, className);
        }
    }

    private static void setSystemProperties() {
        Map<String, Integer> portMap = userTestConfiguration.getMocks().getToxiproxy().isEnabled() ?
                PortManager.getInstance().getToxiProxyPortMap() : PortManager.getInstance().getServicePortMap();
        LOGGER.info("Setting system properties={}", portMap);
        for (Map.Entry<String, Integer> entry : portMap.entrySet()) {
            System.setProperty(entry.getKey(), entry.getValue().toString());
        }
    }

    private static void launchApplication() throws Exception {
        LOGGER.info("Launching application");
        setSystemProperties();
        for (ApplicationFramework framework : frameworkConfiguration.getAvailableFrameworks()) {
            TestApplication testApplication = ApplicationFactory.getTestApplication(framework, userTestConfiguration);
            testApplication.launch();
        }
    }

    private static void generateHtmlReport() {
        LOGGER.info("Generate HTML report in path={}", Paths.CUCUMBER_REPORTS);
        Configuration config = new Configuration(new File("target"), "AssertX Execution Report");

        ReportBuilder reportBuilder =
                new ReportBuilder(
                        FileUtility.getFilteredFileList(
                                Paths.CUCUMBER_REPORTS,
                                new String[]{"json"},
                                true),
                        config);
        reportBuilder.generateReports();
    }

    @AfterClass
    public static void teardown() {
        LOGGER.info("Remove spun docker containers");
        containerRule.after();

        LOGGER.info("Exiting application");
        for (ApplicationFramework framework : frameworkConfiguration.getAvailableFrameworks()) {
            TestApplication testApplication = ApplicationFactory.getTestApplication(framework, userTestConfiguration);
            testApplication.terminate();
        }

        generateHtmlReport();
    }

}
