package bg.sofia.uni.fmi.mjt.spotify.server.exceptions;

public class SessionServiceException extends Exception {
    public SessionServiceException(String msg) {
        super(msg);
    }

    public SessionServiceException(String msg, Throwable e) {
        super(msg, e);
    }
}
