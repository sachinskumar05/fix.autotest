package com.samo.fix.autotest;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@CucumberOptions(features = "src/test/resources/features/sample",
		plugin = {"pretty", "html:target/cucumber/sample"},
		extraGlue = "com.samo.fix.autotest.steps")
@RunWith(Cucumber.class)
public class FIXAutoTestApplicationTests {
}
