package bg.sofia.uni.fmi.mjt.spotify.server.exceptions;

public class PlaybackServiceException extends Exception {
    public PlaybackServiceException(String msg) {
        super(msg);
    }

    public PlaybackServiceException(String msg, Throwable e) {
        super(msg, e);
    }
}
