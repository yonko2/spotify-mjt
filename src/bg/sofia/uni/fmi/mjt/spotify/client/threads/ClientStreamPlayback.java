package bg.sofia.uni.fmi.mjt.spotify.client.threads;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.Socket;

public class ClientStreamPlayback extends Thread {
    public ClientStreamPlayback(AudioFormat audioFormat) {

    }

    @Override
    public void run() {
//        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
//
//        SourceDataLine dataLine = (SourceDataLine) AudioSystem.getLine(info);
//        dataLine.open();
//        dataLine.start(); // Имайте предвид, че SourceDataLine.start() пуска нова нишка. За повече информация, може да проверите имплементацията.


        try (Socket socket = new Socket("localhost", 6666)) {
            if (socket.isConnected()) {
                InputStream in = new BufferedInputStream(socket.getInputStream());
                play(in);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static synchronized void play(final InputStream in) throws Exception {
        AudioInputStream ais = AudioSystem.getAudioInputStream(in);
        try (Clip clip = AudioSystem.getClip()) {
            clip.open(ais);
            clip.start();
            Thread.sleep(100); // given clip.drain a chance to start
            clip.drain();
        }
    }
}
