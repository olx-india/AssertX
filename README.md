# AssertX

![](logo.jpeg)

AssertX is an API testing framework built using Cucumber and Java. It is designed to help teams automate API tests efficiently using a behavior-driven development (BDD) approach.

🔹 **Why Use AssertX for API Testing?**

🔹 **BDD-Based Approach** – Uses Cucumber, allowing non-technical stakeholders to understand test scenarios.

🔹 **Modular & Reusable** – Step definitions can be reused across different test cases.

🔹 **Easy API Test Automation** – Supports RESTful and SOAP API testing using RestAssured.

🔹 **Seamless Integration** – Can be plugged into CI/CD workflows.

## List of Contents

1. [Integration Steps](#integration-steps)
   <br></br>
    - [Configuration](#configuration)
    <br></br>
    - [Execution](#execution)
    <br></br>
    - [Reporting](#reporting)
    <br></br>
2. [Health Check](#health-check)
<br> </br>
    - [How to check service or mock ports](#how-to-check-service-or-mock-ports)
<br> </br>
3. [How to write Custom Mocks?](#how-to-write-custom-mocks)
<br> </br>
4. [How to add Parallelization](#how-to-add-parallelisation-works-with-failsafe-222x)
<br> </br>
5. [Gradle Integration](#gradle-integration-with-kotlin-requires-gradle--510-)
<br> </br>
6. [SOAP API Consumer & Downstream XML Response Support](#soap-api-consumer-and-downstream-xml-response-support)
<br> </br>
7. [FAQ](#faq)

## Integration Steps

### Configuration
1. Configure `pom.xml` :
	
    - Add the following properties -
    
    ```xml
       <jacoco.integration-test.report>${project.build.directory}/coverage-reports/jacoco-it.exec</jacoco.integration-test.report>
       <jacoco.it.bundle.percentage.instruction>0.50</jacoco.it.bundle.percentage.instruction>
       <jacoco.version>0.8.2</jacoco.version>
       <skipIntegration>false</skipIntegration>
       <assertx.version>1.0.0</assertx.version>
    ```

    - Add **AssertX** dependency -

    ```xml
    <dependency>
        <groupId>com.olx.assertx</groupId>
        <artifactId>assertx</artifactId>
        <!-- Set the assertx version below -->
        <version>${assertx.version}</version>
        <scope>test</scope>
    </dependency>
    ```

	- Add **Jacoco** plugin for coverage report -
    
    ```xml
    <plugin>
        <groupId>org.jacoco</groupId>
        <artifactId>jacoco-maven-plugin</artifactId>
        <version>${jacoco.version}</version>
        <configuration>
            <excludes>
                <!-- Add directories to be excluded from IT coverage -->
                <exclude></exclude>
            </excludes>
        </configuration>
        <executions>
            <execution>
                <id>pre-integration-test</id>
                <phase>pre-integration-test</phase>
                <goals>
                <goal>prepare-agent</goal>
                </goals>
                <configuration>
                   <destFile>${jacoco.integration-test.report}</destFile>
                </configuration>
                </execution>
            <execution>
                <id>post-integration-test</id>
                <phase>integration-test</phase>
                <goals>
                <goal>report</goal>
                </goals>
                <configuration>
                    <dataFile>${jacoco.integration-test.report}</dataFile>
                    <!-- Sets the output directory for the code coverage report. -->
                    <outputDirectory>${project.reporting.outputDirectory}/jacoco-it</outputDirectory>
                </configuration>
            </execution>
            <execution>
                <id>check-it</id>
                <phase>integration-test</phase>
                <goals>
                <goal>check</goal>
                </goals>
                <configuration>
                <rules>
                    <rule>
                    <element>BUNDLE</element>
                    <limits>
                        <limit>
                        <counter>INSTRUCTION</counter>
                        <value>COVEREDRATIO</value>
                        <!-- Add minimum coverage percentage required to pass -->
                        <minimum>${jacoco.it.bundle.percentage.instruction}</minimum>
                        </limit>
                    </limits>
                    </rule>
                </rules>
                <dataFile>${jacoco.integration-test.report}</dataFile>
                </configuration>
            </execution>
        </executions>
    </plugin>
    ```
    
    - Add **maven failsafe** plugin for IT execution -
    
    ```xml
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-failsafe-plugin</artifactId>
            <version>2.22.0</version>
            <configuration>
                <skip>${skipIntegration}</skip>
                <argLine>${argLine}</argLine>
                <executions>
                    <execution>
                        <goals>
                            <goal>integration-test</goal>
                        </goals>
                        <configuration>
                            <includes>
                                <include>full.package.path.to.ITMain</include>
                            </includes>
                            <skip>${skipIntegration}</skip>
                            <argLine>${argLine}</argLine>
                        </configuration>
                    </execution>
                    <execution>
                        <id>verify</id>
                        <phase>integration-test</phase>
                        <goals>
                            <goal>verify</goal>
                        </goals>
                    </execution>
                </executions>
            </configuration>
        </plugin>
    ```

	- Exclude **ITMain** from  **maven surefire** plugin used for UT execution -
    
    ```xml
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>${maven.surefire.plugin.version}</version>
            <configuration>
                <excludes>
                    <exclude>full.package.path.to.ITMain</exclude>
                </excludes>
                <skip>${skipUnitTests}</skip>
                <argLine>${surefireArgLine}</argLine>
            </configuration>
        </plugin>
    ```

2. Create feature files in `src/test/resources/${features.folder.name}`.

3. Create step definitions in `src/test/java/${package.path.of.step.definitions}`.

4. Create main IT execution class `ITMain.java` extending `com.olx.assertx.ITRunner` class and configure features and step definitions as following :
    
    ```java
    @CucumberOptions(
        features = {"src/test/resources/${features.folder.name}"},
        glue = {"${package.path.of.step.definitions}"})
    public class ITMain extends ITRunner {
    }
    ```

5. Create `api-testing.yml` file for providing IT configuration in `src/test/resources`.
    
    **Example** :
    
    ```yaml
    mainClass: com.olx.inventory.InventoryApplication                              // Specify the main execution class of the service/application
    application:
        spring:                                                                         // Configuration for spring application type
            profile: integration-test                                                   // Specify the profile to run the application
        dropwizard:                                                                     // Configuration for dropwizard application type
            configPath: config/application-integration-test.yml                         // Specify the path to your application config for ITs 
    mocks:                                                                              // Configures the mocks needed by the application
        externalServices:
            dependencies:                                                               // Add extra JS dependencies to be added in package.json
              - name: libphonenumber-js                                                 // Name of the dependency
                version: ~1.7.8                                                           // Version of the dependency
            global:                                                                     // Add the names of the global mocks needed
              - threepio
            userSpecified:                                                              // Add the custom service mocks
                routes:                                                                 // Specify the service mock configuration
                  - type: order                                                          // Name of main mock JS file for the service
                    resourcePath: src/test/resources/mocks/external-services/           // Base Folder where the JS mock files are present
                    folderName: order-service                                                    // Exact mock folder name to use
                    endpoints:                                                          // Specify the list of endpoints provided by the mock
                      - methodType: GET                                                 // Type of API endpoint
                        path: /api/v1/orders/:order                                       // Path of API endpoint
                        method: adResponseV2                                            // Method in JS file which serves this endpoint
                  - type: user
                    resourcePath: src/test/resources/mocks/external-services/
                    folderName: user-service
                    endpoints:
                      - methodType: GET
                        path: /api/v1/users/:userId
                        method: general
            port: 31270                                                                 // Optional -> Default is 31270
        redis:
            enabled: true                                                               // Specify whether redis is needed or not
            port: 6379                                                                  // Optional -> Default is 6379
            image: redis:latest                                                         // Optional -> Default is redis:latest
            configFilePath: redis/redis.conf                                            // Relative Path from resources to file, Optional -> Default is null, needed if user wants to create his own redis server/cluster
        toxiproxy:
            enabled: true                                                               // Specify whether toxiproxy is needed or not
            port: 8474                                                                  // Optional -> Default is 8474
        mysql:
            enabled: true
            dataDir: src/test/resources/database/                                       // Optional -> Path to directory having warm up scripts, should be .sql files containing schema creation and sample data insertion queries
            port: 3306                                                                  // Optional -> Default is 3306
            image: mysql:latest                                                         // Optional -> Default is mysql:latest
        localstack:
            enabled: true
            dataDir: src/test/resources/aws/                                            // Optional -> Path to directory having warm up scripts, should be .sh files containing resource creation scripts
            services:                                                                   // Optional -> Default is s3. List of supported service names can be found at https://docs.aws.amazon.com/cli/latest/reference/#available-services
              - s3
            region: us-east-1                                                           // Optional -> Default is us-east-1
            port: 4566                                                                  // Optional -> Default is 4566
            image: localstack/localstack                                                // Optional -> Default is localstack/localstack
        solr:
            enabled: true
            port: 8983                                                                  // Optional -> Default is 8983
            image: solr:latest                                                          // Optional -> Default is solr:latest
            collectionsDir: src/test/resources/solr/collections                         // Path to directory containing the solr collection configurations
            collections:                         
              - name: olxin                                                             // Name of the collection
                configPath: olxin/olxin/conf                                            // Relative path from `collectionsDir` to config directory of the collection
                primary: true                                                           // `true` if it is the main collection and should be used for healthcheck (Only one collection can be primary)
        zookeeper:
            enabled: true
            port: 2181                                                                  // Optional -> Default is 2181
            image: wurstmeister/zookeeper                                               // Optional -> Default is wurstmeister/zookeeper
        kafka:                                                                          // Requires `zookeeper` mock
            enabled: true
            port: 9092                                                                  // Optional -> Default is 9092
            image: wurstmeister/kafka                                                   // Optional -> Default is wurstmeister/kafka
            topics:                                                                     // List of topics that need to be created
              - itemSaved
        postgresql:
            enabled: true
            dataDir: src/test/resources/postgresql/                                     // Optional -> Path to directory having warm up scripts, should be .sql files containing schema creation and sample data insertion queries
            port: 5432                                                                  // Optional -> Default is 5432
            image: postgres:latest                                                      // Optional -> Default is postgres:latest
        opensearch:
            enabled: true
            port: 9200                                                                  // Optional -> Default is 9200
            extraPorts:                                                                 // Optional -> Only when extra ports need to be exposed such as performance port (9600)
              - 9600
            image: opensearchproject/opensearch:2.2.0                                   // Optional -> Default is opensearchproject/opensearch:2.2.0
            volumes:                                                                    // Optional -> Volumes to be mounted on the container
              - srcPath: src/test/resources/mocks/opensearch/indices/olxin/analysis     // Source folder path in the service repo
                destPath: /usr/share/opensearch/config                                  // Destination path in the container
                destFolder: analysis                                                    // Destination folder name in the `destPath`
        custom:                                                                         // Specify the list of fully qualified class paths to all custom mocks
          - com.olx.it.mocks.SolrMock
    ```

6. Create `application-integration-test.yml` for defining application run configuration with port placeholders which will be filled automatically by AssertX on execution.
 
    **Example** :
    
    ```yaml
    service-name:
        clients:
          id: service-id
          order-service:
            endpoint: http://0.0.0.0:${order-service}
            timeouts:
              connection: 10000
              read: 10000
              write: 10000
          user-service:
            endpoint: http://0.0.0.0:${user-service}
            timeouts:
              connection: 10000
              read: 10000
              write: 10000
        redis:
          master: 0.0.0.0:${redis}
        database:
          master:
            jdbcUrlTemplate: jdbc:mysql://127.0.0.1:${mysql}/_SITECODE_?useSSL=false&useUnicode=yes&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
            username: root                                                              // Default username set by assertx framework
            passoword: 1234                                                             // Default password set by assertx framework
    server:
        port: 9000
    ```

7. Add `/it` folder which is generated by AssertX at runtime in `.gitignore` file.

### Execution

1. **Local** : Run the following commands on terminal.
    
    **(Note: Please ensure that you have a running local docker environment)**
    
    ```
    ./mvnw clean integration-test -DskipUnitTests=true
    ```

2. **Pipeline** : Add the following configuration in `.gitlab-ci.yml`.
    
    ```yaml
    ${IT_STEP_NAME}:
        image: eclipse-temurin/17-jdk-alpine
        variables:
            DOCKER_DRIVER: overlay2
        services:
            - docker:dind
        script:
            - /app/execute-it.sh
        after_script:
            - /app/upload-report.sh ${SERVICE_NAME}
        allow_failure: false
        tags:
        - <ENV>(stg|prd)
        - ap-southeast-1
        - build

        only:
            refs:
                - {BRANCHES_TO_RUN_INTEGRATION_TESTS_ON}
    ```

### Reporting
The reports will be uploaded to the following paths :
1. **Test Execution Report** - <https://${S3_BUCKET}.s3-${REGION}.amazonaws.com/${SERVICE_NAME}/${PIPELINE_ID}/assertx/cucumber-report/report-feature_*.html>
2. **Test Coverage Report** - <https://${S3_BUCKET}.s3-${REGION}.amazonaws.com/${SERVICE_NAME}/${PIPELINE_ID}/assertx/coverage-report/index.html>


## Health Check
For all the global or user specified mocks, there is a `/healthcheck` endpoint available by default.

**For example :-**

Suppose, the external services container is running on port `31270` and user service mock is running on port `31721`.

Then, the health check using `curl` can be done as follows :
1. **External Services Container** -
   `curl -X GET http://0.0.0.0:31270/healthcheck`
2. **User Service Mock** - 
   `curl -X GET http://0.0.0.0:31271/healthcheck`

### How to check service or mock ports
1. If toxiproxy is enabled, the value corresponding to the `listen` field for a mock in `it/mocks/toxiproxy/config.json` is its port.
   
   For example :-
   
   ```
   {
      "name": "user-service",
      "listen": "0.0.0.0:31270",
      "upstream": "0.0.0.0:31274"
   }
   ```
   In the above config, the port for user service is `31270`.

2. If toxiproxy is disabled, you can run `docker ps` for getting the container ports.
   
   a.) All global or user specified mocks will have the port same as the external service container port
   
   b.) For other dependencies (like redis, mysql), you can simply pick their corresponding container ports.

## How to write Custom Mocks
1. Define a model class for your custom mock extending `BaseService`.
   
   For example :-

   ```
   @EqualsAndHashCode(callSuper = true)
   @JsonInclude(JsonInclude.Include.NON_NULL)
   @JsonPropertyOrder({
      "image",
      "container_name",
      "ports",
      "environment",
      "entrypoint",
      "healthcheck",
      "networks"
   })
   @Getter
   @Setter
   public class Solr extends BaseService {
      String image;
      List<String> entrypoint;
      
      @Builder
      public Solr(String containerName,
                  List<String> ports,
                  List<String> environment,
                  Networks networks,
                  Healthcheck healthcheck,
                  String image,
                  List<String> entrypoint) {
         super(containerName, ports, environment, networks, healthcheck);
         this.image = image;
         this.entrypoint = entrypoint;
      }
   }
   ```

2. Define a custom mock class implementing `BaseCustomMock`.
   
   For example :-
   
   ```
   public class SolrMock implements BaseCustomMock {
   
     @Override
     public boolean isEnabled() {                                                   // Defines whether mock is enabled or not         
       return true;
     }
   
     @Override
     public BaseService map(int port) {                                             // Return the object of your custom mock defined above 
       return Solr.builder()
         .image("solr:latest")
         .containerName(getServiceName())
         .ports(Collections.singletonList(port + ":" + port))
         .environment(List.of())
         .networks(Networks.builder().build())
         .entrypoint(List.of("sh", "-c",
           "precreate-core my_core; solr-foreground"))
         .healthcheck(Healthcheck.builder()
           .test(List.of("CMD-SHELL",
             "curl -s -A 'healthcheck'  http://localhost:" + port + "/solr/my_core/admin/ping?wt=json | grep -q '\"status\":\"OK\"'"))
           .interval("10s")
           .timeout("5s")
           .retries(3)
           .build())
         .build();
     }
   
     @Override
     public String getServiceName() {                                              // Return the name of your custom mock
       return "solr";
     }
   
     @Override
     public int getServicePort() {                                                 // Return the port of your custom mock
       return 8983;
     }
   }
   ```

3. Specify the fully qualified path of your custom mock class in `api-testing.yml` under `mocks.custom` section as mentioned in `Configuration` step.


## How to add parallelisation (Works with failsafe 2.22.x)
1. Move your stepdefinition classes and Runner class(ITMain) under the same package.
2. Add the following lines in your pom. Refer branch
    ```
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-failsafe-plugin</artifactId>
        <version>2.22.0</version>
        <configuration>
          <skip>${skipIntegration}</skip>
          <argLine>${failsafeArgLine}</argLine>
          <includes>
            <include>com.olx.it.InventoryIT</include>
          </includes>
          <parallel>methods</parallel>
          <threadCount>4</threadCount>
        </configuration>
    </plugin>
    ```
## How to use jedis client while validation?
The following method is exposed in BaseStepDefinition class.
public Jedis getJedisClient() {...}

The method should be called to get the client in custom step definitions and we can perform the desired operations on redis 
For Ex. get(String key), set(String key, String value) etc.


## Gradle Integration with Kotlin (Requires Gradle > 5.10 )

1. Create a source folder integrationTest/kotlin and a resources folder integrationTest/resources in src. Create a package with ITMain.kt. Create a package stepdefinitions and add your custom step definitions.
   ```
    package any.package.name

    import com.olx.assertx.ITRunner
    import cucumber.api.CucumberOptions
    import cucumber.api.junit.Cucumber
    import org.junit.runner.RunWith

    @RunWith(Cucumber::class)
    @CucumberOptions(features = ["src/integrationTest/resources/features"], glue = ["any.package.name.stepdefinitions"])
    class ITMain : ITRunner()

   ```

2. Add the following lines in your dependencies section of build.gradle.kts
    ```
    testImplementation("com.olx.assertx:assertx:1.0.0")
    testImplementation("com.palantir.docker.compose:docker-compose-rule-junit4:0.34.0")
    testImplementation("io.cucumber:cucumber-junit:4.0.0")
    testImplementation("io.cucumber:cucumber-java:4.0.0")
    testImplementation("org.junit.vintage:junit-vintage-engine:5.7.2")

    ```
3. Create integrationTest sourceSets in root section
    ```
    sourceSets.create("integrationTest") {
        compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
        runtimeClasspath += output + compileClasspath + sourceSets["test"].runtimeClasspath
    }

    ```
4. Add jacoco to plugins section
    ```
    id 'jacoco'
    ```
5. Add the following lines in tasks section
    ```
    create<JacocoCoverageVerification>("jacocoIntegrationTestCoverageVerification") {
        executionData(
            file("${project.buildDir}/jacoco/integrationTest.exec"))
        

        val javaTree = file("${project.buildDir}/classes/java/main")
        //kotlin compiled classes
        val kotlinTree = file("${project.buildDir}/classes/kotlin/main")
        // list of classes to be evaluated.
        classDirectories.setFrom(files(javaTree, kotlinTree))
        violationRules {
            rule {
                element = "CLASS"
                includes = listOf("any.package.name.**")
                excludes = listOf(
                    "any.package.name.toexclude.*",
                    "any.package.name.api.*",
                    )
                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = "0.8".toBigDecimal() //80% coverage
                }
            }
        }
    }

    create<JacocoReport>("jacocoIntegrationTestReport") {
        reports.csv.isEnabled = true
        reports.xml.isEnabled = true
        executionData(
            file("${project.buildDir}/jacoco/integrationTest.exec"))
        reports {
            // for devs
            html.destination = file("target/site/jacoco-it")
        }

        val javaTree = file("${project.buildDir}/classes/java/main")
        //kotlin compiled classes
        val kotlinTree = file("${project.buildDir}/classes/kotlin/main")
        // list of classes to be evaluated. 
        classDirectories.setFrom(files(javaTree, kotlinTree))

        afterEvaluate {
            classDirectories.setFrom(files(classDirectories.files.map {
                fileTree(it).apply {
                    exclude( <files to exclude>
                    )
                }
            }))
        }
    }

    

        

    ```
6. Create an integrationTest task in root section 
    ```
    task<Test>("integrationTest") {
        description = "Runs the integration tests"
        group = "verification"
        testClassesDirs = sourceSets["integrationTest"].output.classesDirs
        classpath = sourceSets["integrationTest"].runtimeClasspath
        project.properties["assertx-profile"]?.let { systemProperty("assertx-profile", it) }
        useJUnitPlatform()
        finalizedBy(tasks["jacocoIntegrationTestReport"], tasks["jacocoIntegrationTestCoverageVerification"])
    }
    ```
7. For pipeline changes steps are same as maven, just change the script section from /app/execute-it.sh to /app/execute-it-gradle.sh 

## SOAP API Consumer and Downstream XML Response Support
Execution -  Follow the steps listed below to accomplish the objective.

1. Create an XML files  in src/test/resources/mocks/external-services/abc containing your downstream system (i.e abc)  API XML responses.

   abc.xml  - contains the actual response that you will get by calling the soap API.

   abcCallback.xml - blank XML file to cater the SoapActionCallback.

2. Update the JS file(abc.js) to read the XML as mentioned in the below example
   ```
   var fs = require('fs');

   const abcResponseObject = (fs.readFileSync('./user-specified/abc/abc.xml', 'utf8'));
   
   const abcResponseCallbackObject = (fs.readFileSync('./user-specified/abc/abcCallback.xml', 'utf8'));
   
   exports.abcOutput = (req, res) => {
   return res.end(abcResponseObject);
   };
   
   exports.abcCallbackOutput = (req, res) => {
   return res.end(abcResponseCallbackObject);
   };
   ```
3. Configured SOAP API endpoint as well soap action callback API in the api-testing.yml config file

   **Example** :

   ```yaml
   mainClass: com.olx.inventory.InventoryApplication
   
   application:
     spring:
       profile: integration-test
   
   mocks:
     externalServices:
       userSpecified:
         routes:
           - type: payment
             resourcePath: src/test/resources/mocks/external-services/
             folderName: payment-service
             endpoints:
               - methodType: POST
                 path: /payment/payment.asmx
                 method: paymentOutput
   
               - methodType: POST
                 path: /payment/soapcallbackapi
                 method: paymentCallbackOutput
   ```