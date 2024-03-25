package com.samo.fix.autotest.bdd.stepsProcessor;

import com.samo.fix.autotest.SessionStatus;
import com.samo.fix.autotest.config.AppCfg;
import com.samo.fix.autotest.config.CucumberCfg;
import io.cucumber.datatable.DataTable;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Log4j2
public class CustomMessageBuilder {
    @Autowired
    private AppCfg appCfg;
    @Autowired
    private CucumberCfg cucumberCfg;

    public Message enrichMessageHeaders(String senderCompId, Message message) {
        log.atDebug().log("");
        Session session = SessionStatus.SESSION_MAP.get(senderCompId);
        if(null == session) return null;
        SessionID sessionID = session.getSessionID();
        String beginString = sessionID.getBeginString();
        String targetCompId = sessionID.getTargetCompID();
        Message.Header header = message.getHeader();
        header.setString(BeginString.FIELD, beginString);
        header.setString(TargetCompID.FIELD, targetCompId);
        header.setString(SenderCompID.FIELD, senderCompId);
        log.info("getDefaultMessage message {}", header);
        return message;
    }

    public Message enrichClOrdId(Message message) {
        Optional<String> optionalForSenderCompID = message.getHeader().getOptionalString(SenderCompID.FIELD);
        if(optionalForSenderCompID.isPresent() &&
                SessionStatus.SESSION_MAP.containsKey(optionalForSenderCompID.get())) {
            String clOrdId = cucumberCfg.getClOrdIdPrefix() + System.nanoTime();
            Optional<String> msgTypeOptional = message.getHeader().getOptionalString(MsgType.FIELD);
            String msgType = null;
            if(msgTypeOptional.isPresent()) {
                msgType = msgTypeOptional.get();
            }
            assert msgType != null;
            if(msgType.equals("D") || msgType.equals("G") || msgType.equals("F")) {
                message.getHeader().setString(ClOrdID.FIELD, clOrdId);
            }
        } else {
            log.error("There is no session for SenderCompID {} of message {}", optionalForSenderCompID, message);
        }
        return message;
    }

    public Message enrichDefaultFields(Message message) {
        Map<String,String> defaultFixValue = appCfg.getDefaultFixValues();
        for (Map.Entry<String, String> entry :defaultFixValue.entrySet()) {
            String key = entry.getKey();
        }
        return message;
    }

    public List<Message> convertDataTables(DataTable dataTable) {
        List<Message> messageList = new ArrayList<>();
        List<Map<String, String>> tableValues = dataTable.asMaps();
        for (Map<String,String> row : tableValues) {
            Message message = new Message();
            Message.Header header = message.getHeader();
            for(Map.Entry<String, String> entry : row.entrySet()) {
                String fieldTag = entry.getKey();
                String fieldValue = entry.getValue();
                if(null != fieldTag && null != fieldValue) {
                    fieldTag = fieldTag.toUpperCase();
                    try {
                        if (fieldTag.startsWith("H")) {
                            String headerTagStr = fieldTag.replace("H", "");
                            header.setString(Integer.parseInt(headerTagStr), fieldValue);
                        } else {
                            message.setString(Integer.parseInt(fieldTag), fieldValue);
                        }
                    } catch (Exception e) {
                        //TODO Log unsupported FieldTag found in Cucumber Data Table
                    }
                }
            }
            Optional<String> senderCompID = header.getOptionalString(SenderCompID.FIELD);
            senderCompID.ifPresent(s -> enrichMessageHeaders(s, message));
            enrichClOrdId(message);
            messageList.add(message);
        }
        return messageList;
    }

}
