package bg.sofia.uni.fmi.mjt.spotify.server.models;

import java.util.UUID;

public class Song {
    private final UUID uuid;
    private final String title;
    private final String album;
    private final String artist;
    private final int timeSeconds;

    public UUID getUuid() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public int getTimeSeconds() {
        return timeSeconds;
    }

    public int getStreams() {
        return streams;
    }

    public String getSourceFilepath() {
        return sourceFilepath;
    }

    private int streams;
    private final String sourceFilepath;
    public Song(UUID uuid, String title, String album, String artist, int timeSeconds, int streams,
                String sourceFilepath) {

        this.uuid = uuid;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.timeSeconds = timeSeconds;
        this.streams = streams;
        this.sourceFilepath = sourceFilepath;
    }

    public void increaseStreams() {
        streams++;
    }

    public static Song of(String title, String album, String artist, int timeSeconds, String sourceFilepath) {
        return new Song(UUID.randomUUID(), title, album, artist, timeSeconds, 0, sourceFilepath);
    }
}
