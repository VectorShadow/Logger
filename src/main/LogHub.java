package main;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LogHub provides a utility for recording runtime data and writing to external log files.
 */
public class LogHub {
    static final String PATH_CRASH = "CrashReport";
    static final String PATH_LIVE_LOG = "LiveLog";
    //todo - other logpaths?
    public static final String TXT = "txt";

    /**
     * Log an exception as a fatal crash, generate a timestamped report, and terminate the program.
     * @param adminMessage a message provided by the caller to provide context specific information
     * @param exception the exception which caused the call
     */
    public static void logFatalCrash(String adminMessage, Exception exception) {
        String crashDump =
                "[Fatal Crash Report]"
                + "\nCrash message: " + adminMessage
                + "\nException message: " + exception.getMessage()
                + "\nStack trace: " + stringifyStackTrace(exception);
        try {
            if (LiveLog.isActive()) {
                LiveLog.log(crashDump, LiveLog.LogEntryPriority.FATAL_ERROR);
            } else {
                FileWriter fileWriter = new FileWriter(getTimeStampedPath(PATH_CRASH, TXT));
                fileWriter.write(crashDump);
                fileWriter.close();
            }
        } catch (IOException ioe) {
            System.exit(-2);
        }
        System.exit(-1);
    }

    private static String stringifyStackTrace(Exception exception) {
        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        StringBuilder stringBuilder = new StringBuilder();
        for (StackTraceElement ste : stackTraceElements) {
            stringBuilder.append("\n").append(ste);
        }
        return stringBuilder.toString();
    }

    public static String getTimeStampedPath(String preface, String extension) {
        return preface + "_" + System.currentTimeMillis() + "." + extension;
    }

    public static String formatTime() {
        return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date(System.currentTimeMillis()));
    }
    //todo - other logging functions
}
