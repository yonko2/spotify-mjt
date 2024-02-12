package bg.sofia.uni.fmi.mjt.spotify.client.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.PersistenceServiceException;
import bg.sofia.uni.fmi.mjt.spotify.server.services.PersistenceService;

public class DisconnectCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "disconnect";
    private final SpotifyServerInterface spotifyServer;

    public DisconnectCommand(SpotifyServerInterface spotifyServer) {
        this.spotifyServer = spotifyServer;
    }

    @Override
    public CommandResponse execute() {
        try {
            PersistenceService.saveApplicationState(spotifyServer);
            return new CommandResponse("Disconnected successfully", true);
        } catch (PersistenceServiceException e) {
            return new CommandResponse("There was a problem while trying to disconnect", false);
        }
    }
}
