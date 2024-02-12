package bg.sofia.uni.fmi.mjt.spotify.client.threads;

import bg.sofia.uni.fmi.mjt.spotify.client.SpotifyClientInterface;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.BufferedInputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientStreamPlayback extends Thread {
    private static final int BUFFER_SIZE = 4096;
    public static final int PORT = 6666;
    private final AudioFormat audioFormat;
    private final SpotifyClientInterface spotifyClient;

    public ClientStreamPlayback(AudioFormat audioFormat, SpotifyClientInterface spotifyClient) {
        this.audioFormat = audioFormat;
        this.spotifyClient = spotifyClient;
    }

    @Override
    public void run() {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

        try (Socket socket = new Socket("localhost", PORT);
             SourceDataLine dataLine = (SourceDataLine) AudioSystem.getLine(info);
             AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                 new BufferedInputStream(socket.getInputStream()));
        ) {
            this.spotifyClient.setSourceDataLine(dataLine);

            dataLine.open();
            dataLine.start();

            if (socket.isConnected()) {
                byte[] bufferBytes = new byte[BUFFER_SIZE];
                int readBytes = -1;
                while ((readBytes = audioInputStream.read(bufferBytes)) != -1) {
                    dataLine.write(bufferBytes, 0, readBytes);
                }

                dataLine.drain();
            }
        } catch (SocketException se) {
            System.out.println("stopped playback");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
