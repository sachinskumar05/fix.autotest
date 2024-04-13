package com.samo.fix.autotest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app-cfg")
@Data
public class CucumberCfg {
    private String clOrdIdPrefix = "C-";
    private int timeoutInSec = 1;
    private int retryCount = 10;
}
