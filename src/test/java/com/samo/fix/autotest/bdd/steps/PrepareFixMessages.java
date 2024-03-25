package com.samo.fix.autotest.bdd.steps;

import com.samo.fix.autotest.bdd.stepsProcessor.CustomMessageBuilder;
import com.samo.fix.autotest.core.qfix.QFixInitiatorApp;
import com.samo.fix.autotest.data.OrderStore;
import com.samo.fix.autotest.simulators.qfix.exchange.QFixExchangeSimulatorApp;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.activation.UnsupportedDataTypeException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quickfix.InvalidMessage;
import quickfix.Message;

import java.util.List;

@CucumberContextConfiguration
@SpringBootTest
@Log4j2
public class PrepareFixMessages {
    @Autowired
    private QFixInitiatorApp fixInitiatorApp;
    @Autowired
    private QFixExchangeSimulatorApp exchangeSimulatorApp;

    @Autowired
    private OrderStore orderStore;
    @Autowired
    private CustomMessageBuilder customMessageBuilder;
    @Given("{word} Build FIX Messages using below data table")
    public void buildFixMessages(String tag, DataTable dataTable) throws InvalidMessage, UnsupportedDataTypeException {
        List<Message> messageList = customMessageBuilder.convertDataTables(dataTable);
        orderStore.queueMessages(tag, messageList);
        System.out.println(String.format("tag %s, messageList %s", tag, messageList));
    }
}
