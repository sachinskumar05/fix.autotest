package com.samo.fix.autotest;

import com.samo.fix.autotest.qfix.ClientApp;
import com.samo.fix.autotest.qfix.ExchangeApp;
import lombok.extern.log4j.Log4j2;
import org.springframework.test.context.support.AbstractTestExecutionListener;

@Log4j2
public class CustomTestExecutionListener extends AbstractTestExecutionListener {
    private static ThreadLocal<ClientApp> clientAppStore = new ThreadLocal<>();
    private static ThreadLocal<ExchangeApp> exchangeAppStore = new ThreadLocal<>();


    public static void setClient(ClientApp clientApp) {
        clientAppStore.set(clientApp);
    }
    public static void setExchange(ExchangeApp exchangeApp) {
        exchangeAppStore.set(exchangeApp);
    }
    public static ClientApp getClient() {
        return clientAppStore.get();
    }
    public static ExchangeApp getExchange() {
        return exchangeAppStore.get();
    }

}
