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

        assertEquals("PCM_SIGNED", audioFormat.getEncoding().toString(), "Test to audio format success");
        assertEquals(44100.0f, audioFormat.getSampleRate(), "Test to audio format success");
        assertEquals(16, audioFormat.getSampleSizeInBits(), "Test to audio format success");
        assertEquals(2, audioFormat.getChannels(), "Test to audio format success");
        assertEquals(4, audioFormat.getFrameSize(), "Test to audio format success");
        assertEquals(44100.0f, audioFormat.getFrameRate(), "Test to audio format success");
        assertFalse(audioFormat.isBigEndian(), "Test to audio format success");
    }
}
