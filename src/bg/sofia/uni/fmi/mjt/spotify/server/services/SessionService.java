package bg.sofia.uni.fmi.mjt.spotify.server.services;

import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.PasswordEncryptionException;
import bg.sofia.uni.fmi.mjt.spotify.server.models.User;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionService {
    private static final int BYTE_SIZE = 16;
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 128;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    private static String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[BYTE_SIZE];
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        return new String(factory.generateSecret(spec).getEncoded());
    }

    public static void register(ConcurrentHashMap<String, User> users, String email, String password)
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

    public static void login(String email, String password, SpotifyServerInterface server)
        throws PasswordEncryptionException {
        if (!server.getUsers().containsKey(email)) {
            throw new IllegalArgumentException("User with this email does not exist");
        }

        User user = server.getUsers().get(email);
        try {
            if (!user.hashPass().equals(hashPassword(password))) {
                throw new IllegalArgumentException("Invalid password");
            }

            server.setLoggedUser(user);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new PasswordEncryptionException("Error while hashing the password", e);
        }
    }
}
