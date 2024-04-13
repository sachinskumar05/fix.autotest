package com.samo.fix.autotest.steps;

import com.samo.fix.autotest.CustomTestExecutionListener;
import com.samo.fix.autotest.FIXAutotestAppTest;
import com.samo.fix.autotest.data.OrderStore;
import com.samo.fix.autotest.steps.processor.CustomMessageBuilder;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import java.util.List;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import quickfix.ConfigError;
import quickfix.Message;

@Log4j2
public class PrepareFixMessages extends FIXAutotestAppTest {
    @Autowired
    private CustomMessageBuilder customMessageBuilder;
    @Autowired
    private OrderStore orderStore;

  @Given("{word} Prepare FIX Messages using below data table")
  public void prepareFixMessages(String tag, DataTable dataTable) throws ConfigError, InterruptedException {
        log.info("exchangeApp {}, clientApp {}", exchangeApp, clientApp);
        CustomTestExecutionListener.setClient(clientApp);
        CustomTestExecutionListener.setExchange(exchangeApp);
        if(null == exchangeApp || null == clientApp)
            throw new RuntimeException("exchange, client or both are not started");

        List<Message> messageList = customMessageBuilder.convertDataTables(dataTable);
        orderStore.queueMessages(tag, messageList);
        System.out.println(String.format("tag %s, messageList %s", tag, messageList));
    }
}
