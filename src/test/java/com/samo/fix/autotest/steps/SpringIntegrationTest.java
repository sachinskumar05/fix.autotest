package com.samo.fix.autotest.steps;

import com.samo.fix.autotest.qfix.ClientApp;
import com.samo.fix.autotest.qfix.ExchangeApp;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quickfix.ConfigError;

import java.util.concurrent.TimeUnit;

@CucumberContextConfiguration
@SpringBootTest
public class SpringIntegrationTest {
    @Autowired
    protected ExchangeApp exchangeApp;
    @Autowired
    protected ClientApp clientApp;

}