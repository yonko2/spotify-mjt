package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.PasswordEncryptionException;
import bg.sofia.uni.fmi.mjt.spotify.server.services.SessionService;

public class RegisterCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "register";
    private final String email;
    private final String password;
    private final SpotifyServerInterface server;

    public RegisterCommand(String email, String password, SpotifyServerInterface server) {
        this.email = email;
        this.password = password;
        this.server = server;
    }

    @Override
    public CommandResponse execute() {
        try {
            SessionService.register(server.getUsers(), email, password);
            return new CommandResponse("User registered successfully", true);
        } catch (PasswordEncryptionException e) {
            return new CommandResponse("Error: " + e.getMessage(), false);
        }
    }
}
