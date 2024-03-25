package com.samo.fix.autotest.data;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import quickfix.Message;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Log4j2
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class OrderStore {

    private final Map<String, ArrayDeque<Message>> orderPool =  new ConcurrentHashMap<>();
    private final Map<String, List<Message>> messageMap = new ConcurrentHashMap<>();

    public void queueMessages(String tag, List<Message> messageList) {
        orderPool.computeIfAbsent(tag, t->new ArrayDeque<>()).addAll(messageList);
        log.info("Messages stored for tag {}, msgCount {}", tag, messageList.size());
    }

    public Deque<Message> poll(String tag) {
        log.atDebug().log("Before orderPool size {}", orderPool.size());
        Deque<Message> deque = orderPool.remove(tag);
        log.atDebug().log("After orderPool size {}", orderPool.size());
        return deque;
    }

}
