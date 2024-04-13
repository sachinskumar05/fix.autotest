package com.samo.fix.autotest;

import com.samo.fix.autotest.qfix.ClientApp;
import com.samo.fix.autotest.qfix.ExchangeApp;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@CucumberContextConfiguration
@SpringBootTest
@TestExecutionListeners(value = {
        CustomTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class
})
@Log4j2
public class FIXAutotestAppTest {

    @Autowired
    protected ExchangeApp exchangeApp;
    @Autowired
    protected ClientApp clientApp ;

    @Before
    private void before(){
        log.info("Before All Started");
        log.info("Before All Ends");
    }

    @AfterAll
    private static void afterAll() {
        log.info("After All Started");
        log.info("After All Ends");
    }
}