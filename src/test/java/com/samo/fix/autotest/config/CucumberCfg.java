package com.samo.fix.autotest.config;

import io.cucumber.spring.CucumberContextConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Configuration
@Scope(SCOPE_CUCUMBER_GLUE)
@ConfigurationProperties(prefix = "app-cfg")
@Data
public class CucumberCfg {
    private String clOrdIdPrefix = "C-";
    private int timeoutInSec = 1;
    private int retryCount = 10;
}
