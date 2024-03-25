package com.samo.fix.autotest.data;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import quickfix.Session;
import quickfix.SessionID;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class SessionManager {
    public enum SessionStatus {UP, DOWN, IN_PROGRESS;}

    public static final Map<SessionID, SessionStatus> SESSION_STATUS_MAP = new ConcurrentHashMap<>();

    public static final Map<String, Session> SESSION_MAP = new ConcurrentHashMap<>();
}
