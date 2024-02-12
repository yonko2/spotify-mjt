package bg.sofia.uni.fmi.mjt.spotify.server.threads;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

public class ServerStreamPlayback extends Thread {
    private final Path path;

    public ServerStreamPlayback(Path path) {
        this.path = path;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(6666);
             FileInputStream in = new FileInputStream(path.toString())) {
            if (serverSocket.isBound()) {
                Socket client = serverSocket.accept();
                OutputStream out = client.getOutputStream();

                byte[] buffer = new byte[2048];
                int count;
                while ((count = in.read(buffer)) != -1)
                    out.write(buffer, 0, count);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
