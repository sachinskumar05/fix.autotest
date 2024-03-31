package com.samo.fix.autotest;

import com.samo.fix.autotest.config.AgentsCfg;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Log4j2
@SpringBootApplication
public class FIXAutotestApp {
	@Autowired
	AgentsCfg agentsCfg;

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(FIXAutotestApp.class, args);
		FIXAutotestApp app = ctx.getBean(FIXAutotestApp.class);
		log.info("app.agentsCfg = {}", app.agentsCfg);
	}
}
