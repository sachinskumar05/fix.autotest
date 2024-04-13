package com.samo.fix.autotest.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import quickfix.SessionID;

@Component
public class SessionManager {
    public enum SessionStatus {UP, DOWN, IN_PROGRESS}
    public static final Map<String, SessionID> SESSION_ID_MAP = new ConcurrentHashMap<>();
}
