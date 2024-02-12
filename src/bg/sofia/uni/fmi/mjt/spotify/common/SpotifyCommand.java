package bg.sofia.uni.fmi.mjt.spotify.common;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;

public interface SpotifyCommand {
    CommandResponse execute();
}
