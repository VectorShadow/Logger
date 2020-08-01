package main;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LogHub provides a utility for recording runtime data and writing to external log files.
 */
public class LogHub {
    private static final String LOG_DIRECTORY = "./logs";
    private static final String PATH_CRASH = "CrashReport";
    private static final String PATH_ERROR = "ErrorReport";
    static final String PATH_LIVE_LOG = "LiveLog";
    //todo - other logpaths?
    static final String TXT = "txt";

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
        if (LiveLog.isActive()) {
            LiveLog.log(crashDump, LiveLog.LogEntryPriority.FATAL_ERROR);
        } else {
            try {
                FileWriter fileWriter = new FileWriter(getTimeStampedPath(PATH_CRASH, TXT));
                fileWriter.write(crashDump);
                fileWriter.close();
            } catch (IOException ioe) {
                System.exit(-2);
            }
        }
        System.exit(-1);
    }

    /**
     * Log an exception as a non-fatal error, generate and log a timestamped report.
     * @param adminMessage a message provided by the caller to provide context specific information
     * @param exception the exception which caused the call
     */
    public static void logNonFatalError(String adminMessage, Exception exception) {
        String errorDump =
                "[Non-Fatal Error Report]"
                        + "\nError message: " + adminMessage
                        + "\nException message: " + exception.getMessage()
                        + "\nStack trace: " + stringifyStackTrace(exception);
        if (LiveLog.isActive()) {
            LiveLog.log(errorDump, LiveLog.LogEntryPriority.ERROR);
        } else {
            try {
                FileWriter fileWriter = new FileWriter(getTimeStampedPath(PATH_ERROR, TXT));
                fileWriter.write(errorDump);
                fileWriter.close();
            } catch (IOException ioe) {
                //nothing to do here
            }
        }
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
        if (!Files.exists(Paths.get(LOG_DIRECTORY))) {
            try {
                Files.createDirectory(Paths.get(LOG_DIRECTORY));
            } catch (IOException e) {
                //nothing to do here - if this happens we have no log directory to write to!
            }
        }
        return LOG_DIRECTORY + "/" + preface + "_" + System.currentTimeMillis() + "." + extension;
    }

    public static String formatTime() {
        return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date(System.currentTimeMillis()));
    }

    public static Path getDirectoryPath() {
        return Paths.get(LOG_DIRECTORY);
    }
    //todo - other logging functions
}
