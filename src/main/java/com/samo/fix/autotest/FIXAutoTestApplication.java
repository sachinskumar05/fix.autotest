package com.samo.fix.autotest;

import com.samo.fix.autotest.config.AppCfg;
import com.samo.fix.autotest.config.CucumberCfg;
import com.samo.fix.autotest.config.QuickfixCfg;
import com.samo.fix.autotest.core.qfix.QFixInitiatorApp;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import quickfix.ConfigError;

@Log4j2
@SpringBootApplication
public class FIXAutoTestApplication {

	private QFixInitiatorApp initiatorApp = new QFixInitiatorApp();

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(FIXAutoTestApplication.class, args);
		AppCfg appCfg = ctx.getBean(AppCfg.class);
		CucumberCfg cucumberCfg = ctx.getBean(CucumberCfg.class);
		QuickfixCfg quickfixCfg = ctx.getBean(QuickfixCfg.class);
		log.atInfo().log(appCfg);
		log.atInfo().log(cucumberCfg);
		log.atInfo().log(quickfixCfg);

		FIXAutoTestApplication app = ctx.getBean(FIXAutoTestApplication.class);
		app.run();
	}

	private void run() {
		try {
			initiatorApp.init();
			initiatorApp.start();
		} catch (ConfigError e) {
			log.error("Failed to run {} ", this.getClass().getName(), e);
		}
	}

}
