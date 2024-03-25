package com.samo.fix.autotest.steps;

import com.samo.fix.autotest.stepsProcessor.CustomMessageBuilder;
import com.samo.fix.autotest.data.OrderStore;
import com.samo.fix.autotest.qfix.exchange.QFixExchangeSimulatorApp;
import com.samo.fix.autotest.qfix.QFixInitiatorApp;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import jakarta.activation.UnsupportedDataTypeException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import quickfix.InvalidMessage;
import quickfix.Message;

import java.util.List;

@Log4j2
public class PrepareFixMessages {
    @Autowired
    private QFixInitiatorApp fixClientApp;

    @Autowired
    private QFixExchangeSimulatorApp exchangeSimulatorApp;

    @Autowired
    private CustomMessageBuilder customMessageBuilder;
    @Autowired
    private OrderStore orderStore;

    @BeforeAll
    void beforeAll() {
    }
    @AfterAll
    void afterAll() {}

    @Given("{word} Build FIX Messages using below data table")
    public void buildFixMessages(String tag, DataTable dataTable) throws InvalidMessage, UnsupportedDataTypeException {
        System.out.println(fixClientApp);
        List<Message> messageList = customMessageBuilder.convertDataTables(dataTable);
        orderStore.queueMessages(tag, messageList);
        System.out.println(String.format("tag %s, messageList %s", tag, messageList));
    }
}
