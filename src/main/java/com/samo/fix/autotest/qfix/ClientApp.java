package com.samo.fix.autotest.qfix;


import com.samo.fix.autotest.data.SessionManager;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import quickfix.*;

import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class ClientApp extends FIXApplication {
    protected Logger log(){return log;}
    private SocketInitiator socketInitiator;
    private String initiatorCfg;
    @PostConstruct
    public void init() {
        initiatorCfg = null==initiatorCfg?quickfixCfg.getInitiatorCfg():initiatorCfg;
        log.info("initializing FIX Client with {}", initiatorCfg);
        try {
            SessionSettings sessionSettings = new SessionSettings(initiatorCfg);
            if(socketInitiator == null) {//ensuring initialization once only
                socketInitiator  = SocketInitiator.newBuilder()
                        .withSettings(sessionSettings)
                        .withApplication(this)
                        .withLogFactory(new FileLogFactory(sessionSettings))
                        .withMessageStoreFactory(new FileStoreFactory(sessionSettings))
                        .withMessageFactory(new DefaultMessageFactory())
                        .build();
            }
            this.start();
            TimeUnit.SECONDS.sleep(0);//Give some time for proper initialization and logon shake-hand
        } catch (ConfigError | InterruptedException e) {
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
