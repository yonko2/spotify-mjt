package bg.sofia.uni.fmi.mjt.spotify.server.services;

import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.PasswordEncryptionException;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.SessionServiceException;
import bg.sofia.uni.fmi.mjt.spotify.server.models.User;

import java.nio.channels.SelectionKey;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class SessionService {
    public static final int HEX_CONST = 0xFF;
    public static final String ALGORITHM = "MD5";

    private static String hashPassword(String password)
        throws NoSuchAlgorithmException, InvalidKeySpecException, PasswordEncryptionException {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance(ALGORITHM);
            digest.update(password.getBytes());
            byte[] messageDigest = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) hexString.append(Integer.toHexString(HEX_CONST & b));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new PasswordEncryptionException("Error while hashing the password", e);
        }
    }

    public static void register(ConcurrentMap<String, User> users, String email, String password)
        throws PasswordEncryptionException {
        if (users.containsKey(email)) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        try {
            users.put(email, new User(UUID.randomUUID(), email, hashPassword(password), new ArrayList<>()));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new PasswordEncryptionException("Error while hashing the password", e);
        }
    }

    public static void login(String email, String password, SpotifyServerInterface server, SelectionKey selectionKey)
        throws PasswordEncryptionException, SessionServiceException {
        if (!server.getUsers().containsKey(email)) {
            throw new SessionServiceException("User with this email does not exist");
        }

        User user = server.getUsers().get(email);
        if (server.getSelectionKeyToUser().containsValue(user)) {
            throw new SessionServiceException("User is already logged in");
        }

        try {
            if (!user.hashPass().equals(hashPassword(password))) {
                throw new SessionServiceException("Invalid password");
            }

            server.getSelectionKeyToUser().put(selectionKey, user);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new PasswordEncryptionException("Error while hashing the password", e);
        }
    }
}
