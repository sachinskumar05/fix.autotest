package com.samo.fix.autotest.config;

import io.cucumber.spring.CucumberContextConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Objects;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Configuration
@Scope(SCOPE_CUCUMBER_GLUE)
@ConfigurationProperties(prefix = "quickfix-cfg")
public class QuickfixCfg {
    private String userDir = System.getProperty("user.dir");
    private String absCfgFileName = userDir + "/config/initiator/quickfix-";
    private String exchangeSimulator = userDir + "/config/acceptor/quickfix-exchange-simulator-";
    private String fileExt = ".cfg";
    private String envName = Objects.requireNonNullElse(System.getProperty("profile"), "dafault");

    @Getter @Setter
    private String initiatorCfg = absCfgFileName + envName + fileExt;
    @Getter @Setter
    private String exchangeSimCfg = exchangeSimulator + envName + fileExt;
}
