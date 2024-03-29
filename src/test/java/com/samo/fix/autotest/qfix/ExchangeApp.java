package com.samo.fix.autotest.qfix;


import com.samo.fix.autotest.data.SessionManager;
import io.cucumber.spring.ScenarioScope;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
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
        log.info("initialized quickfixCfg using active profile name {}", quickfixCfg);
        String qfixAcceptorCfg = quickfixCfg.getExchangeSimCfg();
        try {
            SessionSettings sessionSettings = new SessionSettings(qfixAcceptorCfg);
            if(socketAcceptor == null) {//ensuring initialization once only
                socketAcceptor  = SocketAcceptor.newBuilder()
                        .withQueueCapacity(500)
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
        log.info("Socket Acceptor started");
        for (SessionID sessionID : socketAcceptor.getSessions()) {
            SessionManager.SESSION_ID_MAP.putIfAbsent(sessionID.getSenderCompID(), sessionID);
            log.info("this.printSessions() = {}" , this.printSessions());
        }
        log.info("socketAcceptor.getSessions() = {} ", socketAcceptor.getSessions());
        log.info("this.printSessions() = {}" , this.printSessions());
    }

    @PreDestroy
    public void stop() throws InterruptedException {
        log.info("Attempt to stop application");
        for (SessionID sessionID : socketAcceptor.getSessions()) {
            SessionManager.SESSION_ID_MAP.remove(sessionID.getSenderCompID());
        }
            TimeUnit.MILLISECONDS.sleep(1000);
        socketAcceptor.stop();
        isAlive.set(false);
    }
}
