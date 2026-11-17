# Gradle (Kotlin DSL)

Requires Gradle 5.10 or newer.

1. Create a source folder integrationTest/kotlin and a resources folder integrationTest/resources in src. Create a package with ITMain.kt. Create a package stepdefinitions and add your custom step definitions.
   ```
    package any.package.name

    import com.olx.assertx.ITRunner
    import io.cucumber.junit.CucumberOptions
    import io.cucumber.junit.Cucumber
    import org.junit.runner.RunWith

    @RunWith(Cucumber::class)
    @CucumberOptions(features = ["src/integrationTest/resources/features"], glue = ["any.package.name.stepdefinitions"])
    class ITMain : ITRunner()

   ```

2. Add the following lines in your dependencies section of build.gradle.kts
    ```
    testImplementation("in.olx:assertx:0.0.5")
    testImplementation("com.palantir.docker.compose:docker-compose-rule-junit4:1.0.0")
    testImplementation("io.cucumber:cucumber-junit:6.10.3")
    testImplementation("io.cucumber:cucumber-java:6.10.3")
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

