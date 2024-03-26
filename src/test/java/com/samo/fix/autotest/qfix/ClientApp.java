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
public class ClientApp extends FIXApplication {
    protected Logger log(){return log;}
    private SocketInitiator socketInitiator;
    @PostConstruct
    public void init() {
        log.info("initializing FIX Client with {}", quickfixCfg);
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
            this.keepAlive();
            this.start();
            TimeUnit.SECONDS.sleep(7);
            log.info("ClientApp instance {}", this);
            log.info("ClientApp sessions {}", this.printSessions());
        } catch (ConfigError e) {
            log.error("FAILED to Start ", e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() throws ConfigError {
        socketInitiator.start();
        log.info("Socket Initiator started");
        for (SessionID sessionID : socketInitiator.getSessions()) {
            try(Session session = Session.lookupSession(sessionID)) {
                session.logon();
                TimeUnit.SECONDS.sleep(3);
                log.info("Logon sent on sessionID {} {} sessionHashCode {}",
                        sessionID, session.isLogonSent(), session.hashCode());
                SessionManager.SESSION_ID_MAP.putIfAbsent(sessionID.getSenderCompID(), sessionID);
                SessionManager.SESSION_MAP.putIfAbsent(sessionID.getSenderCompID(), session);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @PreDestroy
    public void stop() throws ConfigError {
        log.info("Attempt to stop application");
        for (SessionID sessionID : socketInitiator.getSessions()) {
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
        socketInitiator.stop();
        isAlive.set(false);
    }
}
