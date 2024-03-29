package com.samo.fix.autotest.qfix;

import com.samo.fix.autotest.config.AppCfg;
import com.samo.fix.autotest.config.QuickfixCfg;
import com.samo.fix.autotest.data.SessionManager;
import io.cucumber.spring.ScenarioScope;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import quickfix.*;
import quickfix.field.SenderCompID;

@ScenarioScope
public abstract class FIXApplication implements Application {
    @Autowired
    protected AppCfg appCfg;
    @Autowired
    protected QuickfixCfg quickfixCfg;

    protected abstract Logger log();
    protected final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    protected final AtomicBoolean isAlive = new AtomicBoolean();
    public void keepAlive(Session session) {
        isAlive.set(true);
        executor.execute(()->{while(isAlive.get()) {
            try {
                log().info("Keep Alive session {}", session);
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                isAlive.set(false);
                throw new RuntimeException(e);
            }
        }
        });
    }
    public void send(Message message) {
        try {
            int retryCount = appCfg.getRetryCount();
            String senderCompID = message.getHeader().getString(SenderCompID.FIELD);
            for(int sendingAttemptCount = 0; sendingAttemptCount < retryCount; sendingAttemptCount++) {
                SessionID sessionID = SessionManager.SESSION_ID_MAP.get(senderCompID);
                try {

                    Session.sendToTarget(message, sessionID);
                    break;
                } catch (Exception e) {
                    log().info("Session is down/unavailable {}, FAILED to send message {}", sessionID, message);
                    java.util.concurrent.TimeUnit.MILLISECONDS.sleep(appCfg.getTimeoutInMillisecond());
                    if(sendingAttemptCount == retryCount-2) {
                        Assertions.fail(e);
                    }
                }
            }
        } catch (Exception e) {
            log().error("Failed to send from message {} ",  message, e);
        }
    }

    @Override
    public void onCreate(SessionID sessionId) {
        log().info("onCreate() called with sessionId {}", sessionId);
    }

    @Override
    public void onLogon(SessionID sessionId) {
        log().info("onLogon() called with sessionId {}", sessionId);
        log().info("SessionManager SESSION_ID_MAP {}" , SessionManager.SESSION_ID_MAP);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        log().info("onLogout() called with sessionId {}", sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
        log().info("toAdmin() called with sessionId {}, message {} ", sessionId, message);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        log().info("fromAdmin() called with sessionId {}, message {}", sessionId, message);
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
        log().info("toApp() called with sessionId {}, message {}", sessionId, message);
    }

    @Override
    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        log().info("fromApp() called with sessionId {}, message {}", sessionId, message);
    }
}
