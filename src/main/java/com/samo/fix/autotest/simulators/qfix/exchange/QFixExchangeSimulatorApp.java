package com.samo.fix.autotest.simulators.qfix.exchange;

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

@Log4j2
@Component
public class QFixExchangeSimulatorApp implements Application {

    @Autowired
    private AppCfg appCfg;
    @Autowired
    private QuickfixCfg quickfixCfg;

    public QFixExchangeSimulatorApp(AppCfg appCfg, QuickfixCfg quickfixCfg) {
        this.appCfg = appCfg;
        this.quickfixCfg = quickfixCfg;
    }

    private SocketAcceptor socketAcceptor;

    private String qfixAcceptorCfg;
    private volatile boolean isInitialized;
    public QFixExchangeSimulatorApp(String quickfixDefaultCfg) {
        this.qfixAcceptorCfg = quickfixDefaultCfg;
        if(isInitialized) throw new IllegalCallerException("This is an attempt for duplicate initialization");
        this.isInitialized = true;
    }
    public QFixExchangeSimulatorApp(QuickfixCfg quickfixCfg) {
        this(quickfixCfg.getInitiatorCfg());
    }

    public QFixExchangeSimulatorApp() {
        if(isInitialized) throw new IllegalCallerException("This is an attempt for duplicate initialization");
        this.isInitialized = true;
    }

    @PostConstruct
    public void init() {
        log.info("initialized quickfixCfg using active profile name {}", quickfixCfg);
        this.qfixAcceptorCfg = quickfixCfg.getExchangeSimCfg();
        try {
            SessionSettings sessionSettings = new SessionSettings(this.qfixAcceptorCfg);
            if(socketAcceptor == null) {//ensuring initialization once only
                socketAcceptor  = SocketAcceptor.newBuilder()
                        .withSettings(sessionSettings)
                        .withApplication(this)
                        .withLogFactory(new ScreenLogFactory(sessionSettings))//TODO FileLogFactory
                        .withMessageStoreFactory(new FileStoreFactory(sessionSettings))
                        .withMessageFactory(new DefaultMessageFactory())
                        .build();
            }
        } catch (ConfigError e) {
            throw new RuntimeException(e);
        }
    }

    public void start() throws ConfigError {
        socketAcceptor.start();
        log.info("Socket initiator started");
        for (SessionID sessionID : socketAcceptor.getSessions()) {
            try(Session session = Session.lookupSession(sessionID)) {
                log.info("READY sessionID {} ", sessionID);
                SessionStatus.SESSION_STATUS_MAP.computeIfAbsent(sessionID, sid -> SessionStatus.IN_PROGRESS);
                SessionStatus.SESSION_MAP.computeIfAbsent(sessionID.getSenderCompID(), senderCompId -> session);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stop() throws ConfigError {
        log.info("Attempt to stop application");
        for (SessionID sessionID : socketAcceptor.getSessions()) {
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
        socketAcceptor.stop();
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
