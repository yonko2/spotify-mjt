package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.PasswordEncryptionException;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.SessionServiceException;
import bg.sofia.uni.fmi.mjt.spotify.server.services.SessionService;

import java.nio.channels.SelectionKey;

public class LoginCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "login";
    private final String email;
    private final String password;
    private final SpotifyServerInterface server;
    private final SelectionKey selectionKey;
    public static final CommandResponse SUCCESS_RESPONSE = new CommandResponse("Logged in successfully", true);

    public LoginCommand(String email, String password, SpotifyServerInterface server, SelectionKey selectionKey) {
        this.email = email;
        this.password = password;
        this.server = server;
        this.selectionKey = selectionKey;
    }

    @Override
    public CommandResponse execute() {
        try {
            SessionService.login(email, password, server, selectionKey);
            return SUCCESS_RESPONSE;
        } catch (SessionServiceException | PasswordEncryptionException e) {
            return new CommandResponse("Error: " + e.getMessage(), false);
        }
    }
}
