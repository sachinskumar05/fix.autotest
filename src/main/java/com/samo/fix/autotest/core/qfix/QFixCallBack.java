package com.samo.fix.autotest.core.qfix;

import quickfix.ConfigError;
import quickfix.SessionNotFound;

public interface QFixCallBack<T> {
    T execute() throws ConfigError, SessionNotFound;
}
