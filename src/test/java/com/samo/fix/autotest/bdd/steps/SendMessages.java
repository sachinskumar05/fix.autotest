package com.samo.fix.autotest.bdd.steps;

import com.samo.fix.autotest.core.qfix.QFixInitiatorApp;
import com.samo.fix.autotest.data.OrderStore;
import io.cucumber.java.en.Then;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quickfix.Message;

import java.util.Deque;

@SpringBootTest
@Log4j2
public class SendMessages {
    @Autowired
    private QFixInitiatorApp fixInitiatorApp;
    @Autowired
    private OrderStore orderStore;
    @Then("{word} Send Messages")
    public void send(String word) {
        log.info("Starting sending messages for {}", word);
        Deque<Message> msgStack = orderStore.poll(word);
        int sizeBefore = msgStack.size();
        for (Message msg : msgStack) {
            fixInitiatorApp.send(msg);
        }
        int sizeAftter = msgStack.size();
        log.info("Size before {}, after {}", sizeBefore, sizeAftter);
        if(sizeAftter == 0) {
            log.info("All messages were sent successfully");
        }
    }
}
