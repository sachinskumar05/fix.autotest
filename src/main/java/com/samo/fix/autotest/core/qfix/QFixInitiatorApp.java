package com.samo.fix.autotest.core.qfix;

import com.samo.fix.autotest.SessionStatus;
import com.samo.fix.autotest.config.AppCfg;
import com.samo.fix.autotest.config.QuickfixCfg;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import quickfix.*;
import quickfix.field.SenderCompID;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Objects;

@Log4j2
@Component
public class QFixInitiatorApp implements Application {

    @Autowired
    private AppCfg appCfg;
    @Autowired
    private QuickfixCfg quickfixCfg;

    public QFixInitiatorApp(AppCfg appCfg, QuickfixCfg quickfixCfg) {
        this.appCfg = appCfg;
        this.quickfixCfg = quickfixCfg;
    }

    private SocketInitiator socketInitiator;

    private String qfixInitiatorCfg;
    private volatile boolean isInitialized;
    public QFixInitiatorApp(String quickfixDefaultCfg) {
        this.qfixInitiatorCfg = quickfixDefaultCfg;
        if(isInitialized) throw new IllegalCallerException("This is an attempt for duplicate initialization");
        this.isInitialized = true;
    }
    public QFixInitiatorApp(QuickfixCfg quickfixCfg) {
        this(quickfixCfg.getInitiatorCfg());
    }

    public QFixInitiatorApp() {
        if(isInitialized) throw new IllegalCallerException("This is an attempt for duplicate initialization");
        this.isInitialized = true;
    }

    @PostConstruct
    public void init() {
        log.info("initialized quickfixCfg using active profile name {}", quickfixCfg);
        this.qfixInitiatorCfg = quickfixCfg.getInitiatorCfg();
        try {
            SessionSettings sessionSettings = new SessionSettings(this.qfixInitiatorCfg);
            if(socketInitiator == null) {//ensuring initialization once only
                socketInitiator  = SocketInitiator.newBuilder()
                        .withSettings(sessionSettings)
                        .withApplication(this)
                        .build();
            }
        } catch (ConfigError e) {
            throw new RuntimeException(e);
        }
    }

    public void start() throws ConfigError {
        socketInitiator.start();
        log.info("Socket initiator started");
        for (SessionID sessionID : socketInitiator.getSessions()) {
            try(Session session = Session.lookupSession(sessionID)) {
                session.logon();
                log.info("Logon sent on sessionID {} ", sessionID);
                SessionStatus.SESSION_STATUS_MAP.computeIfAbsent(sessionID, sid -> SessionStatus.IN_PROGRESS);
                SessionStatus.SESSION_MAP.computeIfAbsent(sessionID.getSenderCompID(), senderCompId -> session);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stop() throws ConfigError {
        log.info("Attempt to stop application");
        for (SessionID sessionID : socketInitiator.getSessions()) {
            try(Session session = Session.lookupSession(sessionID)) {
                session.logout("Grace logout");
                log.info("Logout sent on sessionID {} ", sessionID);
                SessionStatus.SESSION_STATUS_MAP.computeIfPresent(sessionID, (sid, sessionStatus) -> SessionStatus.DOWN);
                SessionStatus.SESSION_MAP.remove(sessionID.getSenderCompID());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        socketInitiator.stop();
    }

    public void send(Message message) {
        try {
            int retryCount = appCfg.getRetryCount();
            String senderCompID = message.getHeader().getString(SenderCompID.FIELD);
            for(int i = 0; i >= retryCount; i++) {
                SessionID sessionID = SessionStatus.SESSION_MAP.get(senderCompID).getSessionID();
                if( SessionStatus.SESSION_STATUS_MAP.get(sessionID) == SessionStatus.UP &&
                    Session.sendToTarget(message, sessionID)) {
                    log.info("message sent to target {}, {} ", sessionID, message);
                    break;
                } else {
                    TimeUnit.MILLISECONDS.sleep(appCfg.getTimeoutInMillisecond());
                }
            }
        } catch (Exception e) {
            log.error("Failed to send from message {} ",  message, e);
        }
    }

    @Override
    public void onCreate(SessionID sessionId) {
        log.info("onCreate() called with sessionId {}", sessionId);
    }

    @Override
    public void onLogon(SessionID sessionId) {
        log.info("onLogon() called with sessionId {}", sessionId);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        log.info("onLogout() called with sessionId {}", sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
        log.info("toAdmin() called with sessionId {}, message {} ", sessionId, message);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        log.info("fromAdmin() called with sessionId {}, message {}", sessionId, message);
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
        log.info("toApp() called with sessionId {}, message {}", sessionId, message);
    }

    @Override
    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        log.info("fromApp() called with sessionId {}, message {}", sessionId, message);
    }
}
