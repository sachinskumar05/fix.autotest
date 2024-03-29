package com.samo.fix.autotest.steps;

import com.samo.fix.autotest.steps.processor.CustomMessageBuilder;
import com.samo.fix.autotest.data.OrderStore;
import com.samo.fix.autotest.qfix.ExchangeApp;
import com.samo.fix.autotest.qfix.ClientApp;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import jakarta.activation.UnsupportedDataTypeException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import quickfix.ConfigError;
import quickfix.InvalidMessage;
import quickfix.Message;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
public class PrepareFixMessages extends SpringIntegrationTest {
    @Autowired
    private CustomMessageBuilder customMessageBuilder;
    @Autowired
    private OrderStore orderStore;

  @Given("{word} Prepare FIX Messages using below data table")
  public void prepareFixMessages(String tag, DataTable dataTable) throws ConfigError, InterruptedException {
        exchangeApp.start();
        TimeUnit.SECONDS.sleep(2);//Give some time for proper initialization
        clientApp.start();
        TimeUnit.SECONDS.sleep(8);//Give some time for proper initialization and logon shake-hand
        log.info("exchangeApp instance {} ", exchangeApp);
        log.info("clientApp instance {} ", clientApp);

        if(null == exchangeApp || null == clientApp)
            throw new RuntimeException("exchange, client or both are not started");

        log.info("exchangeApp sessions {}", exchangeApp.printSessions());
        log.info("clientApp sessions {}", clientApp.printSessions());

        List<Message> messageList = customMessageBuilder.convertDataTables(dataTable);
        orderStore.queueMessages(tag, messageList);
        System.out.println(String.format("tag %s, messageList %s", tag, messageList));
    }
}
