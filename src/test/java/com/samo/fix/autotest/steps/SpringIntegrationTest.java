package com.samo.fix.autotest.steps;

import com.samo.fix.autotest.qfix.ClientApp;
import com.samo.fix.autotest.qfix.ExchangeApp;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest
@Log4j2
public class SpringIntegrationTest {
    @Autowired
    protected ExchangeApp exchangeApp;
    @Autowired
    protected ClientApp clientApp;

}