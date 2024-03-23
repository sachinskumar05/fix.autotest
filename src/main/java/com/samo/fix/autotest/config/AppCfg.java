package com.samo.fix.autotest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app-cfg")
@Data
public class AppCfg {
    private Map<String, String> defaultFixValues;
    private int timeoutInMillisecond = 1000;
    private int retryCount = 10;
}
