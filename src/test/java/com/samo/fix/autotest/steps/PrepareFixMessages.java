package com.samo.fix.autotest.steps;

import com.samo.fix.autotest.data.OrderStore;
import com.samo.fix.autotest.steps.processor.CustomMessageBuilder;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import quickfix.ConfigError;
import quickfix.Message;

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

        List<Message> messageList = customMessageBuilder.convertDataTables(dataTable);
        orderStore.queueMessages(tag, messageList);
        System.out.println(String.format("tag %s, messageList %s", tag, messageList));
    }
}
