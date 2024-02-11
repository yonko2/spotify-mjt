package bg.sofia.uni.fmi.mjt.spotify.server.exceptions;

public class PasswordEncryptionException extends Exception {
    public PasswordEncryptionException(String msg) {
        super(msg);
    }

    public PasswordEncryptionException(String msg, Throwable e) {
        super(msg, e);
    }
}
