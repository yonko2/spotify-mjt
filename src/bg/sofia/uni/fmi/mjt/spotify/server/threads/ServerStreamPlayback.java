package bg.sofia.uni.fmi.mjt.spotify.server.threads;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

public class ServerStreamPlayback extends Thread {
    public static final int PORT = 6666;
    public static final int BUFFER_SIZE = 2048;
    private final Path path;

    public ServerStreamPlayback(Path path) {
        this.path = path;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT);
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
            throw new RuntimeException(e);
        }
    }
}
