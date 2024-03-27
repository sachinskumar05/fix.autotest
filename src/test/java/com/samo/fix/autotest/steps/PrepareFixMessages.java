package com.samo.fix.autotest.steps;

import com.samo.fix.autotest.steps.processor.CustomMessageBuilder;
import com.samo.fix.autotest.data.OrderStore;
import com.samo.fix.autotest.qfix.ExchangeApp;
import com.samo.fix.autotest.qfix.ClientApp;
import io.cucumber.datatable.DataTable;
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
    private ExchangeApp exchangeApp;
//    @Autowired
//    private ClientApp clientApp;
    @Autowired
    private CustomMessageBuilder customMessageBuilder;
    @Autowired
    private OrderStore orderStore;
    @Given("{word} Prepare FIX Messages using below data table")
    public void prepareFixMessages(String tag, DataTable dataTable) throws InvalidMessage, UnsupportedDataTypeException {
        log.info("exchangeApp instance {} ", exchangeApp);//This line is initializing exchange first
//        log.info("clientApp instance {} ", clientApp);// initializing the client after exchange application

//        if(null == exchangeApp || null == clientApp)
//            throw new RuntimeException("exchange, client or both are not started");

        log.info("exchangeApp sessions {}", exchangeApp.printSessions());
//        log.info("clientApp sessions {}", clientApp.printSessions());

        List<Message> messageList = customMessageBuilder.convertDataTables(dataTable);
        orderStore.queueMessages(tag, messageList);
        System.out.println(String.format("tag %s, messageList %s", tag, messageList));
    }
}
