package com.samo.fix.autotest.steps;

import com.samo.fix.autotest.steps.processor.CustomMessageBuilder;
import com.samo.fix.autotest.data.OrderStore;
import com.samo.fix.autotest.qfix.ExchangeApp;
import com.samo.fix.autotest.qfix.ClientApp;
import io.cucumber.java.en.Then;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import quickfix.Message;

import java.util.Deque;

@Log4j2
public class SendMessages {
    @Autowired
    protected ExchangeApp exchangeApp;
    @Autowired
    protected ClientApp clientApp;
    @Autowired
    protected OrderStore orderStore;
    @Autowired
    protected CustomMessageBuilder customMessageBuilder;
    @Then("{word} Send Messages")
    public void send(String word) {
        log.info("Starting sending messages for {}", word);
        log.info("exchangeApp instance {} ", exchangeApp);
        log.info("clientApp instance {} ", clientApp);

        if(null == exchangeApp || null == clientApp)
            throw new RuntimeException("exchange, client or both are not initialized");

        log.info("exchangeApp sessions {}", exchangeApp.printSessions());
        log.info("clientApp sessions {}", clientApp.printSessions());

        Deque<Message> msgStack = orderStore.poll(word);
        int sizeBefore = msgStack.size();
        while(!msgStack.isEmpty()) {
            Message message = msgStack.poll();
            clientApp.send(message);
        }
        int sizeAfter = msgStack.size();
        log.info("Message Stack Size before {}, after {}", sizeBefore, sizeAfter);
    }
}
