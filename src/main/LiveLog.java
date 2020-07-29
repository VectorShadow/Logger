package main;

import java.io.FileWriter;
import java.io.IOException;

import static main.LogHub.*;

/**
 * LiveLog maintains an open file and writes log data to it as desired.
 */
public class LiveLog {

    public enum LogEntryPriority {
        INFO,
        ALERT,
        WARNING,
        ERROR,
        FATAL_ERROR
    }

    private static LogEntryPriority consoleOutLevel = LogEntryPriority.ALERT;

    private final FileWriter FILE_WRITER;

    private static LiveLog instance = null;

    private static LiveLog getInstance() throws IOException {
        return instance == null ? instance = new LiveLog() : instance;
    }

    private LiveLog() throws IOException {
        FILE_WRITER = new FileWriter(getTimeStampedPath(PATH_LIVE_LOG, TXT));
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                        try {
                            FILE_WRITER.close();
                        } catch (IOException e) {
                            //nothing to do here
                        }
                    }
                )
        );
    }

    public static void log(String message, LogEntryPriority logEntryPriority) throws IOException {
        String logEntry = logEntryPriority + "@" + formatTime() + " - " + message;
        getInstance().FILE_WRITER.write(logEntry);
        if (logEntryPriority.compareTo(consoleOutLevel) >= 0)
            System.out.println(logEntry);
    }
    
    public static void setConsoleOutLevel(LogEntryPriority ep) {
        consoleOutLevel = ep;
    }
    
    static boolean isActive() {
        return instance != null;
    }
}
