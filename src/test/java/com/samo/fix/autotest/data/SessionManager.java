package com.samo.fix.autotest.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;
import quickfix.Session;
import quickfix.SessionID;

@Component
@ScenarioScope
public class SessionManager {
    public enum SessionStatus {UP, DOWN, IN_PROGRESS}
    public static final Map<String, SessionID> SESSION_ID_MAP = new ConcurrentHashMap<>();
    public static final Map<String, Session> SESSION_MAP = new ConcurrentHashMap<>();
}
