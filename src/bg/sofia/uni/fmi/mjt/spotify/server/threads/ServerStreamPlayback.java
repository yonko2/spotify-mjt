package bg.sofia.uni.fmi.mjt.spotify.server.threads;

import bg.sofia.uni.fmi.mjt.spotify.server.logger.SpotifyLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

public class ServerStreamPlayback extends Thread {
    public static final int BUFFER_SIZE = 2048;
    private final Path path;
    private final int port;
    private final Runnable onFinish;

    public ServerStreamPlayback(Path path, int port, Runnable onFinish) {
        this.path = path;
        this.port = port;
        this.onFinish = onFinish;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port);
             FileInputStream in = new FileInputStream(path.toString())) {
            if (serverSocket.isBound()) {
                Socket client = serverSocket.accept();
                OutputStream out = client.getOutputStream();

                byte[] buffer = new byte[BUFFER_SIZE];
                int count;
                while ((count = in.read(buffer)) != -1)
                    out.write(buffer, 0, count);

            }
        } catch (IOException e) {
            SpotifyLogger.getInstance().log(e.getMessage());
        }

        onFinish.run();
    }
}
