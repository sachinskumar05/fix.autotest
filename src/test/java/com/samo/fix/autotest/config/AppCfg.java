package com.samo.fix.autotest.config;

import io.cucumber.spring.CucumberContextConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Map;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Configuration
@Scope(SCOPE_CUCUMBER_GLUE)
@ConfigurationProperties(prefix = "app-cfg")
@Data
public class AppCfg {
    private Map<String, String> defaultFixValues;
    private int timeoutInMillisecond = 1000;
    private int retryCount = 10;
}
