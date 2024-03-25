package com.samo.fix.autotest;

import com.samo.fix.autotest.stepsProcessor.CustomMessageBuilder;
import com.samo.fix.autotest.qfix.QFixInitiatorApp;
import com.samo.fix.autotest.data.OrderStore;
import com.samo.fix.autotest.qfix.exchange.QFixExchangeSimulatorApp;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

@CucumberOptions(features = "src/test/resources/features/sample",
		plugin = {"pretty", "html:target/cucumber/sample"},
		extraGlue = "com.samo.fix.autotest.steps")
@RunWith(Cucumber.class)
public class FIXAutoTestApplicationTests {

	@Autowired
	protected QFixInitiatorApp initiatorApp;
	@Autowired
	protected QFixExchangeSimulatorApp exchangeSimulatorApp;
	@Autowired
	protected OrderStore orderStore;
	@Autowired
	protected CustomMessageBuilder customMessageBuilder;

	void contextLoads() {
	}

}
