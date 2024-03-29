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
public class ExchangeApp extends FIXApplication {
    protected Logger log(){return log;}
    private SocketAcceptor socketAcceptor;
    @PostConstruct
    public void init() {
        String exchangeSimCfg = quickfixCfg.getExchangeSimCfg();
        log.info("initializing FIX Exchange with {}", exchangeSimCfg);
        try {
            SessionSettings sessionSettings = new SessionSettings(exchangeSimCfg);
            if(socketAcceptor == null) {//ensuring initialization once only
                socketAcceptor  = SocketAcceptor.newBuilder()
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
