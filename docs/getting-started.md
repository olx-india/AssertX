# Integration guide

!!! tip "Want to see it working first?"
    Run the [Spring Boot demo](https://github.com/olx-india/AssertX/tree/main/samples/spring-boot-demo) — one `mvn verify` and you have mocks, the app and a Cucumber report.

## Configuration
1. Configure `pom.xml` :
	
    - Add the following properties -
    
    ```xml
       <jacoco.integration-test.report>${project.build.directory}/coverage-reports/jacoco-it.exec</jacoco.integration-test.report>
       <jacoco.it.bundle.percentage.instruction>0.50</jacoco.it.bundle.percentage.instruction>
       <jacoco.version>0.8.2</jacoco.version>
       <skipIntegration>false</skipIntegration>
       <assertx.version>0.0.3</assertx.version> <!-- latest on Maven Central -->
    ```

    - Add **AssertX** dependency -

    ```xml
    <dependency>
        <groupId>in.olx</groupId>
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

## Execution

1. **Local** : Run the following commands on terminal.
    
    **(Note: Please ensure that you have a running local docker environment)**
    
    ```
    ./mvnw clean integration-test -DskipUnitTests=true
    ```

2. **Pipeline** : Add the following configuration in `.gitlab-ci.yml` or any map it to any other CI Script.

   - Use official public AssertX docker image : https://hub.docker.com/r/olxindia/assertx
   - ``docker pull olxindia/assertx``

```yaml
    ${IT_STEP_NAME}:
        image: olxindia/assertx
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

## Reporting
The reports will be uploaded to the following paths :
1. **Test Execution Report** - <https://${S3_BUCKET}.s3-${REGION}.amazonaws.com/${SERVICE_NAME}/${PIPELINE_ID}/assertx/cucumber-report/report-feature_*.html>
2. **Test Coverage Report** - <https://${S3_BUCKET}.s3-${REGION}.amazonaws.com/${SERVICE_NAME}/${PIPELINE_ID}/assertx/coverage-report/index.html>


