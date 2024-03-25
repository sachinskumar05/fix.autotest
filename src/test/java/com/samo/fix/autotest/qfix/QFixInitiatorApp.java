package com.samo.fix.autotest.qfix;

import com.samo.fix.autotest.data.SessionManager;
import com.samo.fix.autotest.config.AppCfg;
import com.samo.fix.autotest.config.QuickfixCfg;
import com.samo.fix.autotest.data.OrderStore;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import io.cucumber.spring.ScenarioScope;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import quickfix.*;
import quickfix.field.SenderCompID;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Log4j2
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class QFixInitiatorApp implements Application {

    @Autowired
    private AppCfg appCfg;
    @Autowired
    private QuickfixCfg quickfixCfg;
    @Autowired
    private OrderStore orderStore;

    public QFixInitiatorApp(AppCfg appCfg, QuickfixCfg quickfixCfg) {
        this.appCfg = appCfg;
        this.quickfixCfg = quickfixCfg;
    }

    private SocketInitiator socketInitiator;

    public QFixInitiatorApp() {
    }

    @PostConstruct
    public void init() {
        log.info("initialized quickfixCfg using active profile name {}", quickfixCfg);
        String qfixInitiatorCfg = quickfixCfg.getInitiatorCfg();
        try {
            SessionSettings sessionSettings = new SessionSettings(qfixInitiatorCfg);
            if(socketInitiator == null) {//ensuring initialization once only
                socketInitiator  = SocketInitiator.newBuilder()
                        .withSettings(sessionSettings)
                        .withApplication(this)
                        .withLogFactory(new ScreenLogFactory(sessionSettings))//TODO FileLogFactory
                        .withMessageStoreFactory(new FileStoreFactory(sessionSettings))
                        .withMessageFactory(new DefaultMessageFactory())
                        .build();
            }
            this.start();
        } catch (ConfigError e) {
            log.error("FAILED to Start ", e);
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
                SessionManager.SESSION_STATUS_MAP.computeIfAbsent(sessionID, sid -> SessionManager.SessionStatus.IN_PROGRESS);
                SessionManager.SESSION_MAP.computeIfAbsent(sessionID.getSenderCompID(), senderCompId -> session);
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
                SessionManager.SESSION_STATUS_MAP.computeIfPresent(sessionID, (sid, sessionStatus) -> SessionManager.SessionStatus.DOWN);
                SessionManager.SESSION_MAP.remove(sessionID.getSenderCompID());
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
            for(int sendingAttemptCount = 0; sendingAttemptCount < retryCount; sendingAttemptCount++) {
                SessionID sessionID = SessionManager.SESSION_MAP.get(senderCompID).getSessionID();
                if( SessionManager.SESSION_STATUS_MAP.get(sessionID) == SessionManager.SessionStatus.UP &&
                    Session.sendToTarget(message, sessionID)) {
                    log.info("message sent to target {}, {} ", sessionID, message);
                    break;
                } else {
                    log.info("Session is down/unavailable {}, FAILED to send message {}", sessionID, message);
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
