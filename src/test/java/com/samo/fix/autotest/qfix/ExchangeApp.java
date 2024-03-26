package com.samo.fix.autotest.qfix;


import com.samo.fix.autotest.data.SessionManager;
import io.cucumber.spring.ScenarioScope;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import quickfix.*;

@Log4j2
@Component
@ScenarioScope
public class ExchangeApp extends FIXApplication {
    protected Logger log(){return log;}
    private SocketAcceptor socketAcceptor;

    @PostConstruct
    public void init() {
        log.info("initialized quickfixCfg using active profile name {}", quickfixCfg);
        String qfixAcceptorCfg = quickfixCfg.getExchangeSimCfg();
        try {
            SessionSettings sessionSettings = new SessionSettings(qfixAcceptorCfg);
            if(socketAcceptor == null) {//ensuring initialization once only
                socketAcceptor  = SocketAcceptor.newBuilder()
                        .withSettings(sessionSettings)
                        .withApplication(this)
                        .withLogFactory(new ScreenLogFactory(sessionSettings))//TODO FileLogFactory
                        .withMessageStoreFactory(new FileStoreFactory(sessionSettings))
                        .withMessageFactory(new DefaultMessageFactory())
                        .build();
            }
            keepAlive();
            this.start();
            TimeUnit.SECONDS.sleep(3);
            log.info("ExchangeApp instance {}", this);
            log.info("ExchangeApp sessions {}", this.printSessions());
        } catch (ConfigError e) {
            log.error("FAILED to Start ", e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void start() throws ConfigError {
        socketAcceptor.start();
        log.info("Socket Acceptor started");
        for (SessionID sessionID : socketAcceptor.getSessions()) {
            try(Session session = Session.lookupSession(sessionID)) {
                log.info("READY sessionID {} sessionHashCode {}", sessionID, session.hashCode());
                SessionManager.SESSION_ID_MAP.putIfAbsent(sessionID.getSenderCompID(), sessionID);
                SessionManager.SESSION_MAP.putIfAbsent(sessionID.getSenderCompID(), session);
                log.info("SessionManager.SESSION_MAP {}" , SessionManager.SESSION_ID_MAP);
            } catch (IOException e) {
                log.atError().withThrowable(e).log("FAILED to start Exchange Sessions {} ", socketAcceptor);
                throw new RuntimeException(e);
            }
        }
    }

    @PreDestroy
    public void stop() throws ConfigError {
        log.info("Attempt to stop application");
        for (SessionID sessionID : socketAcceptor.getSessions()) {
            try(Session session = Session.lookupSession(sessionID)) {
                session.logout("Grace logout");
                log.info("Logout sent on sessionID {} ", sessionID);
                SessionManager.SESSION_ID_MAP.remove(sessionID.getSenderCompID());
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
        isAlive.set(false);
    }
}
