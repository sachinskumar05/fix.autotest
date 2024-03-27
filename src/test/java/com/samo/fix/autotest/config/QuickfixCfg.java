package com.samo.fix.autotest.config;

import io.cucumber.spring.ScenarioScope;
import java.util.Objects;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ScenarioScope
@ConfigurationProperties(prefix = "quickfix-cfg")
@Data
public class QuickfixCfg {
    private String userDir = System.getProperty("user.dir");
    private String absCfgFileName = userDir + "/config/initiator/quickfix-";
    private String exchangeSimulator = userDir + "/config/acceptor/quickfix-exchange-simulator-";
    private String fileExt = ".cfg";
    private String envName = Objects.requireNonNullElse(System.getProperty("profile"), "default");

    @Getter @Setter
    private String initiatorCfg = absCfgFileName + envName + fileExt;
    @Getter @Setter
    private String exchangeSimCfg = exchangeSimulator + envName + fileExt;
}
