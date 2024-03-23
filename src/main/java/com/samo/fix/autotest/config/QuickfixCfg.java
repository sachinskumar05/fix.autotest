package com.samo.fix.autotest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
@ConfigurationProperties(prefix = "quickfix-cfg")
@Data
public class QuickfixCfg {
    private String userDir = System.getProperty("user.dir");
    private String absCfgFileName = userDir + "/config/initiator/quickfix-";
    private String exchangeSimulator = userDir + "/config/acceptor/quickfix-exchange-simulator-";
    private String fileExt = ".cfg";
    private String envName = Objects.requireNonNullElse(System.getProperty("profile"), "dafault");

    private String initiatorCfg = absCfgFileName + envName + fileExt;
    private String exchangeSimCfg = exchangeSimulator + envName + fileExt;
}
