package bg.sofia.uni.fmi.mjt.spotify.common.models.io;

import javax.sound.sampled.AudioFormat;
import java.io.Serializable;

public record AudioFormatSerializable(String encoding, float sampleRate, int sampleSizeInBits, int channels,
                                      int frameSize, float frameRate, boolean bigEndian) implements Serializable {
    public static AudioFormatSerializable of(AudioFormat audioFormat) {
        return new AudioFormatSerializable(audioFormat.getEncoding().toString(),
            audioFormat.getSampleRate(),
            audioFormat.getSampleSizeInBits(),
            audioFormat.getChannels(),
            audioFormat.getFrameSize(),
            audioFormat.getFrameRate(),
            audioFormat.isBigEndian());
    }

    public static AudioFormat toAudioFormat(AudioFormatSerializable audioFormatSerializable) {
        return new AudioFormat(
            new AudioFormat.Encoding(audioFormatSerializable.encoding()),
            audioFormatSerializable.sampleRate(),
            audioFormatSerializable.sampleSizeInBits(),
            audioFormatSerializable.channels(),
            audioFormatSerializable.frameSize(),
            audioFormatSerializable.frameRate(),
            audioFormatSerializable.bigEndian()
        );
    }
}
