package com.vonhof.smartlog.impl;

import com.vonhof.smartlog.LogEntry;
import com.vonhof.smartlog.LoggerStore;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SmartLogServerStore implements LoggerStore {
    private static final Logger log = Logger.getLogger(SmartLogServerStore.class.getName());

    private ObjectMapper om = new ObjectMapper();

    @Override
    public void write(String author, LogEntry logEntry) {
        LogEntryDTO dto = toDTO(author, logEntry);

        try {
            send(dto);
        } catch (IOException e) {
            log.log(Level.WARNING, "Failed to send smart log entry", e );
        }

    }

    private void send(LogEntryDTO entry) throws IOException {
        URL url = new URL("http://localhost:8844/rest/log/save");

        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("PUT");
        om.writeValue(httpCon.getOutputStream(), entry);
        httpCon.getOutputStream().flush();
        httpCon.getOutputStream().close();
        httpCon.disconnect();
    }

    private LogEntryDTO toDTO(String author, LogEntry logEntry) {
        LogEntryDTO dto = new LogEntryDTO();
        dto.setAuthor(author);

        String[] args = new String[logEntry.getArgs().length];
        for(int i = 0; i < logEntry.getArgs().length; i++) {
            args[i] = logEntry.getArgs()[i].toString();
        }
        dto.setArgs(args);
        dto.setTags(logEntry.getTags());
        dto.setClazz(logEntry.getClz().getName());
        dto.setMessage(logEntry.getMsg());

        StackTraceDTO[] trace = new StackTraceDTO[logEntry.getTrace().length];

        for(int i = 0; i < logEntry.getTrace().length ; i++) {
            trace[i] = toDTO(logEntry.getTrace()[i]);
        }

        dto.setTrace(trace);

        return dto;
    }

    private StackTraceDTO toDTO(StackTraceElement stackTraceElement) {
        StackTraceDTO out = new StackTraceDTO();
        out.setClazz(stackTraceElement.getClassName());
        out.setLine(stackTraceElement.getLineNumber());
        out.setMethod(stackTraceElement.getMethodName());
        out.setFile(stackTraceElement.getFileName());
        return out;
    }
}
