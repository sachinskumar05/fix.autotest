package com.samo.fix.autotest;

import org.springframework.stereotype.Component;
import quickfix.Session;
import quickfix.SessionID;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum SessionStatus {
    UP, DOWN, IN_PROGRESS;

    public static final Map<SessionID, SessionStatus> SESSION_STATUS_MAP = new ConcurrentHashMap<>();

    public static final Map<String, Session> SESSION_MAP = new ConcurrentHashMap<>();
}
