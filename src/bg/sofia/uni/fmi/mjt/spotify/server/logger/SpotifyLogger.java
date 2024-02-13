package bg.sofia.uni.fmi.mjt.spotify.server.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SpotifyLogger implements Logger {
    private static final String FILE_PATH = "src/bg/sofia/uni/fmi/mjt/spotify/server/logger/log.txt";
    private static Logger instance;
    private PrintWriter writer;

    private SpotifyLogger() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    System.err.println("Couldn't create log file");
                }
            } catch (IOException e) {
                System.err.println("Error creating log file");
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(FILE_PATH, true);
            writer = new PrintWriter(fileWriter);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (SpotifyLogger.class) {
                if (instance == null) {
                    instance = new SpotifyLogger();
                }
            }
        }
        return instance;
    }

    @Override
    public void log(String message) {
        writer.println(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " " + message);
        writer.flush();
    }

}
