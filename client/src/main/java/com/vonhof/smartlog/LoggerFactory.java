package com.vonhof.smartlog;


import com.vonhof.smartlog.store.LoggerStore;
import com.vonhof.smartlog.subscriber.LoggerSubscriber;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoggerFactory {

    private static volatile Level defaultLevel = Level.DEBUG;

    private static Map<String,Level> prefixLevels = new ConcurrentHashMap<String, Level>();

    private static Map<String,Level> tagLevels = new ConcurrentHashMap<String, Level>();

    private static LoggerStore store;

    private static List<LoggerSubscriber> subscribers = new ArrayList<LoggerSubscriber>();

    private static StackTraceElement[] getTrace(LocationInfo location) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        List<StackTraceElement> out = new LinkedList<StackTraceElement>();

        boolean first = true;
        boolean found = location == null;

        for(StackTraceElement elm : stackTrace) {
            if (first || elm.getClassName().startsWith("com.vonhof.smartlog.")) {
                first = false;
                continue;
            }

            if (!found) {
                if (location.getClassName().equals(elm.getClassName())
                        && location.getLineNumber() == elm.getLineNumber()
                        && location.getFileName().equals(elm.getFileName())) {
                    found = true;
                } else {
                    continue;
                }
            }

            out.add(elm);
        }


        return out.toArray(new StackTraceElement[0]);
    }

    public static void write(Level lvl, Class clz, String[] tags, String msg, Object ... args) {
        Object[] formatArgs = args;
        Throwable ex = null;
        LocationInfo location = null;

        if (args.length > 0 && args[args.length-1] instanceof Throwable) {
            formatArgs = Arrays.copyOf(args,args.length-1);
            ex = (Throwable) args[args.length-1];
        } else if (args.length > 0 && args[args.length-1] instanceof LocationInfo) {
            formatArgs = Arrays.copyOf(args,args.length-1);
            location = (LocationInfo) args[args.length-1];
        }

        LogEntry logEntry = null;
        if (ex != null) {
            logEntry = new LogEntry(lvl, clz, tags, msg, formatArgs, ex);
        } else {
            logEntry = new LogEntry(lvl, clz, tags, msg, formatArgs, getTrace(location));
        }

        String author = getAuthor(clz, logEntry);

        if (store != null) {
            store.write(author, logEntry);
        }

        if (!subscribers.isEmpty()) {

            for(LoggerSubscriber subscriber : subscribers) {
                subscriber.logged(author, logEntry);
            }
        }
    }

    private static String getAuthor(Class target, LogEntry entry) {
        try {
            if (entry.getTrace().length < 1) return "";
            StackTraceElement stackTraceElement = entry.getTrace()[0];

            Class<?> registryClass = target.getClassLoader().loadClass(target.getName() + "__SLAUTHORS");
            Field authors = registryClass.getDeclaredField("authors");
            authors.setAccessible(true);

            String[] authorList = (String[]) authors.get(null);
            if (authorList == null) {
                return null;
            }

            int line = stackTraceElement.getLineNumber()-1; //Lines are zero based in author list

            if (authorList.length <= line) {
                return null;
            }
            for (int i = line; i > -1; i--) {
                if (authorList[i] != null) return authorList[i];
            }
            return null;
        } catch (Exception e) {
            return "";
        }
    }

    public static void addSubscriber(LoggerSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public static void setStore(LoggerStore store) {
        LoggerFactory.store = store;
    }

    public static void setLogLevel(Level lvl) {
        defaultLevel = lvl;
    }

    public static void setLogLevelByPrefix(String prefix, Level lvl) {
        prefixLevels.put(prefix, lvl);
    }

    public static void setLogLevelByTag(String tag, Level lvl) {
        tagLevels.put(tag, lvl);
    }

    public static Level getLogLevel(Class clz, String ... tags) {
        for(String tag : tags) {
            if (tagLevels.containsKey(tag)) {
                return tagLevels.get(tag);
            }
        }

        String parts[] = clz.getName().split("\\.");
        Level lvl = defaultLevel;

        String prefix = "";
        for(String part : parts) {
            prefix += part;
            if (prefixLevels.containsKey(prefix)) {
                lvl = prefixLevels.get(prefix);
            }

            prefix += ".";
        }

        return lvl;
    }

    public static Logger getLogger(Class clz, String ... tags) {
        return new Logger(clz,tags);
    }


}
