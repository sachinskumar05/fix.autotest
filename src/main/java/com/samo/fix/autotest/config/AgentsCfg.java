package com.samo.fix.autotest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "agents-cfg")
@Data
public class AgentsCfg {
    Map<String,String> clientAgent;
    Map<String,String> exchangeAgent;
}
