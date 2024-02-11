package bg.sofia.uni.fmi.mjt.spotify.server.exceptions;

public class PersistenceServiceException extends Exception {
    public PersistenceServiceException(String msg) {
        super(msg);
    }

    public PersistenceServiceException(String msg, Throwable e) {
        super(msg, e);
    }
}
