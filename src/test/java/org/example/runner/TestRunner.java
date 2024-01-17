package org.example.runner;


import org.junit.platform.suite.api.*;

import static io.cucumber.core.options.Constants.*;

@Suite
@IncludeEngines("cucumber")
@ConfigurationParameters({
        @ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "true"),
        @ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "src/test/java/resources/features"),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org/example/steps"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
            value = "io/qameta/allure/cucumber7jvm/AllureCucumber7Jvm, pretty")
})
public class TestRunner {
}
