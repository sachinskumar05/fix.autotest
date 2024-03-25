package com.samo.fix.autotest.steps;

import com.samo.fix.autotest.stepsProcessor.CustomMessageBuilder;
import com.samo.fix.autotest.data.OrderStore;
import com.samo.fix.autotest.qfix.exchange.QFixExchangeSimulatorApp;
import com.samo.fix.autotest.qfix.QFixInitiatorApp;
import io.cucumber.java.en.Then;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import quickfix.Message;

import java.util.Deque;

@Log4j2
public class SendMessages {
    @Autowired
    protected QFixInitiatorApp fixClientApp;
    @Autowired
    protected QFixExchangeSimulatorApp exchangeSimulatorApp;
    @Autowired
    protected OrderStore orderStore;
    @Autowired
    protected CustomMessageBuilder customMessageBuilder;

    @Then("{word} Send Messages")
    public void send(String word) {
        log.info("Starting sending messages for {}", word);
        Deque<Message> msgStack = orderStore.poll(word);
        int sizeBefore = msgStack.size();
        while(!msgStack.isEmpty()) {
            fixClientApp.send(msgStack.poll());
        }
        int sizeAfter = msgStack.size();
        log.info("Message Stack Size before {}, after {}", sizeBefore, sizeAfter);
    }
}
