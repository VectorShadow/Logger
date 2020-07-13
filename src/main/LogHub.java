package main;

import java.io.FileWriter;
import java.io.IOException;

/**
 * LogHub provides a utility for recording runtime data and writing to external log files.
 */
public class LogHub {
    private static final String PATH_CRASH = "CrashReport";
    //todo - other logpaths
    private static final String TXT = ".txt";

    /**
     * Log an exception as a fatal crash, generate a timestamped report, and terminate the program.
     * @param adminMessage a message provided by the caller to provide context specific information
     * @param exception the exception which caused the call
     */
    public static void logFatalCrash(String adminMessage, Exception exception) {
        String timestampedPath = PATH_CRASH + System.currentTimeMillis() + TXT;
        try {
            FileWriter fileWriter = new FileWriter(timestampedPath);
            fileWriter.write(
                    "[Fatal Crash Report]"
                            + "\nCrash message: " + adminMessage
                            + "\nException message: " + exception.getMessage()
                            + "\nStack trace: " + stringifyStackTrace(exception)
            );
            fileWriter.close();
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
    //todo - other logging functions
}
