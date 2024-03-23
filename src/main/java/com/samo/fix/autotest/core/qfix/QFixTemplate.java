package com.samo.fix.autotest.core.qfix;

import quickfix.ConfigError;
import quickfix.SessionNotFound;

public class QFixTemplate {
    public <T> T execute(QFixCallBack<T> callBack) {
        try{
            return callBack.execute();
        } catch (ConfigError e) {
            throw new RuntimeException(e);
        } catch (SessionNotFound e) {
            throw new RuntimeException(e);
        }
    }
}
