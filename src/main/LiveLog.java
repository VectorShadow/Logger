package main;

import java.io.FileWriter;
import java.io.IOException;

import static main.LogHub.*;

/**
 * LiveLog maintains an open file and writes log data to it as desired.
 */
public class LiveLog {

    public enum LogEntryPriority {
        INFO, //trivial but potentially useful operation data
        ALERT, //important operation data
        DEBUG, //debug specific data
        WARNING, //indicates a gamestate or occurrence which might potentially indicate a problem
        ERROR, //indicates a gamestate or occurrence which indicates a problem
        FATAL_ERROR //indicates a gamestate or occurrence which is fatal to continued operation
    }

    private static LogEntryPriority consoleOutLevel = LogEntryPriority.ALERT;

    private static LogEntryPriority logOutLevel = LogEntryPriority.INFO;

    private FileWriter FILE_WRITER = null;

    private static LiveLog instance = null;

    private static LiveLog getInstance() {
        return instance == null ? instance = new LiveLog() : instance;
    }

    private LiveLog()  {
        try {
            FILE_WRITER = new FileWriter(getTimeStampedPath(PATH_LIVE_LOG, TXT));
            Runtime.getRuntime().addShutdownHook(
                    new Thread(
                            () -> {
                                try {
                                    FILE_WRITER.close();
                                } catch (IOException e) {
                                    //nothing to do here
                                }
                            }
                    )
            );
        } catch (IOException e) {
            // This should not occur, but if it does, we have nothing to do here - if FILE_WRITER is not assigned here,
            // it will be null and crash as soon as we try to call log(), which will alert us to a problem.
        }
    }

    public static void log(String message, LogEntryPriority logEntryPriority) {
        String logEntry = logEntryPriority + "@" + formatTime() + " - " + message + "\n";
        if (logEntryPriority.compareTo(logOutLevel) >= 0) {
            try {
                getInstance().FILE_WRITER.write(logEntry);
            } catch (IOException e) {
                //nothing to do here
            }
        }
        if (logEntryPriority.compareTo(consoleOutLevel) >= 0)
            System.out.println(logEntry);
    }
    
    public static void setConsoleOutLevel(LogEntryPriority ep) {
        consoleOutLevel = ep;
    }

    public static void setLogOutLevel(LogEntryPriority ep) {
        logOutLevel = ep;
    }

    public static void stop() {
        if (!isActive()) return;
        try {
            getInstance().FILE_WRITER.close();
            instance = null;
        } catch (IOException e) {
            //nothing to do here
        }
    }
    static boolean isActive() {
        return instance != null;
    }

}
