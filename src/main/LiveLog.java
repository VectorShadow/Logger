package main;

import java.io.FileWriter;
import java.io.IOException;

/**
 * LiveLog maintains an open file and writes log data to it as desired.
 */
public class LiveLog {

    private static final String PATH_LIVE_LOG = "LiveLog";

    private final FileWriter FILE_WRITER;

    private static LiveLog instance = null;

    public static void start() throws IOException {
        instance = new LiveLog();
    }

    private LiveLog() throws IOException {
        FILE_WRITER = new FileWriter(LogHub.getTimeStampedPath(PATH_LIVE_LOG, LogHub.TXT));
    }

    public static void log(String message) throws IOException {
        instance.FILE_WRITER.write(System.currentTimeMillis() + ": " + message);
    }

    public static void end() throws IOException {
        instance.FILE_WRITER.close();
        instance = null;
    }
}
