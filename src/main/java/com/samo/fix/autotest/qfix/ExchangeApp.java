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
public class ExchangeApp extends FIXApplication {
    protected Logger log(){return log;}
    private SocketAcceptor socketAcceptor;
    private String acceptorCfg;
    @PostConstruct
    public void init() {
        acceptorCfg = null==acceptorCfg?quickfixCfg.getExchangeSimCfg():acceptorCfg;
        log.info("initializing FIX Exchange with {}", acceptorCfg);
        try {
            SessionSettings sessionSettings = new SessionSettings(acceptorCfg);
            if(socketAcceptor == null) {//ensuring initialization once only
                socketAcceptor  = SocketAcceptor.newBuilder()
                        .withSettings(sessionSettings)
                        .withApplication(this)
                        .withLogFactory(new FileLogFactory(sessionSettings))
                        .withMessageStoreFactory(new FileStoreFactory(sessionSettings))
                        .withMessageFactory(new DefaultMessageFactory())
                        .build();
            }
            this.start();
            TimeUnit.SECONDS.sleep(0);//Give some time for proper initialization
        } catch (ConfigError | InterruptedException e) {
            log.error("FAILED to Start ", e);
            throw new RuntimeException(e);
        }
    }

  public void start() throws ConfigError {
        socketAcceptor.start();
        for (SessionID sessionID : socketAcceptor.getSessions()) {
            SessionManager.SESSION_ID_MAP.putIfAbsent(sessionID.getSenderCompID(), sessionID);
        }
    }

    @PreDestroy
    public void stop() {
        for (SessionID sessionID : socketAcceptor.getSessions()) {
            SessionManager.SESSION_ID_MAP.remove(sessionID.getSenderCompID());
        }
        socketAcceptor.stop();
        isAlive.set(false);
    }
}
