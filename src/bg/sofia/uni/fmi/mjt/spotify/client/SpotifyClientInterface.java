package bg.sofia.uni.fmi.mjt.spotify.client;

import javax.sound.sampled.SourceDataLine;
import java.io.IOException;

public interface SpotifyClientInterface {
    void start();

    SourceDataLine getSourceDataLine();

    void setSourceDataLine(SourceDataLine dataLine);

    void disconnect() throws IOException;
}
