package com.samo.fix.autotest.steps;

import com.samo.fix.autotest.CustomTestExecutionListener;
import com.samo.fix.autotest.FIXAutotestAppTest;
import com.samo.fix.autotest.data.OrderStore;
import com.samo.fix.autotest.steps.processor.CustomMessageBuilder;
import io.cucumber.java.en.Then;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import quickfix.Message;

@Log4j2
public class SendMessages extends FIXAutotestAppTest {
    @Autowired
    protected OrderStore orderStore;
    @Autowired
    protected CustomMessageBuilder customMessageBuilder;
    @Then("{word} Send Messages")
    public void send(String word) throws InterruptedException {
        log.info("Starting sending messages for {}", word);
        exchangeApp = CustomTestExecutionListener.getExchange();
        clientApp = CustomTestExecutionListener.getClient();
        log.info("exchangeApp {}, clientApp {}", exchangeApp, clientApp);
        if(null == exchangeApp || null == clientApp)
            throw new RuntimeException("exchange, client or both are not initialized");

        Deque<Message> msgStack = orderStore.poll(word);
        int sizeBefore = msgStack.size();
        while(!msgStack.isEmpty()) {
            Message message = msgStack.poll();
            clientApp.send(message);
        }
        int sizeAfter = msgStack.size();
        log.info("Message Stack Size before {}, after {}", sizeBefore, sizeAfter);
        TimeUnit.SECONDS.sleep(10);//Give some time for proper initialization and logon shake-hand
    }
}
