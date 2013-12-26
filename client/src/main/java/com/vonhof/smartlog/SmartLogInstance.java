package com.vonhof.smartlog;


import com.vonhof.smartlog.registry.AuthorRegistryReader;
import com.vonhof.smartlog.store.LoggerStore;
import com.vonhof.smartlog.store.SmartLogServerStore;
import com.vonhof.smartlog.subscriber.LoggerSubscriber;
import com.vonhof.smartlog.vcs.VCSFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SmartLogInstance {

    private final LoggerStore store;

    private Level defaultLevel = Level.DEBUG;

    private Map<String,Level> prefixLevels = new ConcurrentHashMap<String, Level>();
    private Map<String,Level> tagLevels = new ConcurrentHashMap<String, Level>();
    private Map<String, String> authorCache = new ConcurrentHashMap<String, String>();

    private List<LoggerSubscriber> subscribers = new ArrayList<LoggerSubscriber>();

    private String basePath = "src/main/java"; //Base dir of java classes, defaults to maven structure
    private boolean debug;

    public SmartLogInstance() {
        this(new SmartLogServerStore());
    }

    public SmartLogInstance(LoggerStore store) {
        this.store = store;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    private StackTraceElement[] getTrace(LocationInfo location) {
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

    public String write(Level lvl, Class clz, String[] tags, String msg, Object... args) {
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

        return author;
    }

    private String getAuthor(Class target, LogEntry entry) {
        if (entry.getTrace().length < 1) {
            return null;
        }

        StackTraceElement stackTraceElement = entry.getTrace()[0];
        int line = stackTraceElement.getLineNumber()-1; //Lines are zero based in author list

        String cacheKey = getAuthorCacheKey(target, line);

        if (authorCache.containsKey(cacheKey)) {
            return authorCache.get(cacheKey);
        }

        String author = getAuthorFromRegistry(target, line);
        if (author == null) {
            author = getAuthorFromVCS(stackTraceElement.getClassName(), stackTraceElement.getLineNumber());
        }
        if (author == null) {
            author = "";
        }
        authorCache.put(cacheKey, author);

        return author;
    }

    private String getAuthorCacheKey(Class target, int line ) {
        return target.toString() + ":" + line;
    }

    private String getAuthorFromVCS(String className, int line) {
        String filePath = basePath + "/" + className.replaceAll("\\.","/") + ".java";
        File file = new File(filePath);
        if (!file.exists()) {
            debugMsg("File not found: " + file.getAbsolutePath());
            return null;
        }

        try {
            return VCSFactory.getRepository(file).resolveAuthor(file,line);
        } catch (Exception e) {
            return null;
        }
    }


    private void debugMsg(String msg) {
        if (!debug) return;
        System.out.println(msg);
    }

    private String getAuthorFromRegistry(Class target, int line) {
        return new AuthorRegistryReader(target).getAuthor(line);
    }

    public void addSubscriber(LoggerSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void setLogLevel(Level lvl) {
        defaultLevel = lvl;
    }

    public void setLogLevelByPrefix(String prefix, Level lvl) {
        prefixLevels.put(prefix, lvl);
    }

    public void setLogLevelByTag(String tag, Level lvl) {
        tagLevels.put(tag, lvl);
    }

    public Level getLogLevel(Class clz, String ... tags) {
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
}
