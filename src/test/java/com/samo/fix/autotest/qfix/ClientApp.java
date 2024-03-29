package com.samo.fix.autotest.qfix;


import com.samo.fix.autotest.data.SessionManager;
import io.cucumber.spring.ScenarioScope;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import quickfix.*;

@Log4j2
@Service
@ScenarioScope
public class ClientApp extends FIXApplication {
    protected Logger log(){return log;}
    private SocketInitiator socketInitiator;
    @PostConstruct
    public void init() {
        String initiatorCfg = quickfixCfg.getInitiatorCfg();
        log.info("initializing FIX Client with {}", initiatorCfg);
        try {
            SessionSettings sessionSettings = new SessionSettings(initiatorCfg);
            if(socketInitiator == null) {//ensuring initialization once only
                socketInitiator  = SocketInitiator.newBuilder()
                        .withSettings(sessionSettings)
                        .withApplication(this)
                        .withLogFactory(new FileLogFactory(sessionSettings))//TODO FileLogFactory
                        .withMessageStoreFactory(new FileStoreFactory(sessionSettings))
                        .withMessageFactory(new DefaultMessageFactory())
                        .build();
            }
        } catch (ConfigError e) {
            log.error("FAILED to Start ", e);
            throw new RuntimeException(e);
        }
    }

  public void start() throws ConfigError {
        socketInitiator.start();
        for (SessionID sessionID : socketInitiator.getSessions()) {
            SessionManager.SESSION_ID_MAP.putIfAbsent(sessionID.getSenderCompID(), sessionID);
        }
    }

    @PreDestroy
    public void stop() {
        log.info("Attempt to stop application");
        for (SessionID sessionID : socketInitiator.getSessions()) {
            SessionManager.SESSION_ID_MAP.remove(sessionID.getSenderCompID());
        }
        socketInitiator.stop();
        isAlive.set(false);
    }
}
