package bg.sofia.uni.fmi.mjt.spotify.server.models.io;

import bg.sofia.uni.fmi.mjt.spotify.common.models.io.AudioFormatSerializable;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AudioFormatSerializableTest {
    @Test
    void testToAudioFormatSuccess() {
        AudioFormatSerializable audioFormatSerializable = new AudioFormatSerializable(
            "PCM_SIGNED", 44100.0f, 16, 2, 4, 44100.0f, false, 1234);
        AudioFormat audioFormat = AudioFormatSerializable.toAudioFormat(audioFormatSerializable);

        assertEquals("PCM_SIGNED", audioFormat.getEncoding().toString());
        assertEquals(44100.0f, audioFormat.getSampleRate());
        assertEquals(16, audioFormat.getSampleSizeInBits());
        assertEquals(2, audioFormat.getChannels());
        assertEquals(4, audioFormat.getFrameSize());
        assertEquals(44100.0f, audioFormat.getFrameRate());
        assertFalse(audioFormat.isBigEndian());
    }
}
