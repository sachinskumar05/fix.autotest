package com.samo.fix.autotest.data;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import quickfix.Message;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Component
public class OrderStore {

    private final ArrayDeque<Message> orderPool =  new ArrayDeque<>();
    private final Map<String, Message> messageMap = new ConcurrentHashMap<>();

    public void addMessage(Message message) {

    }

}
