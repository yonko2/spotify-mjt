package bg.sofia.uni.fmi.mjt.spotify.common;

import bg.sofia.uni.fmi.mjt.spotify.client.commands.DisconnectCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.AddSongToPlaylistCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.CreatePlaylistCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.LoginCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.PlayServerCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.RegisterCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.SearchCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.ShowPlaylistCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.StopPlaybackCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.TopCommand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParserService {
    private static final Pattern REGISTER_PATTERN =
        Pattern.compile(RegisterCommand.COMMAND_STRING + " (?<email>\\S+) (?<password>\\S+)");
    private static final Pattern LOGIN_PATTERN = Pattern.compile(LoginCommand.COMMAND_STRING + " (?<password>\\S+)");
    private static final Pattern DISCONNECT_PATTERN = Pattern.compile(DisconnectCommand.COMMAND_STRING);
    private static final Pattern STOP_PLAYBACK_PATTERN = Pattern.compile(StopPlaybackCommand.COMMAND_STRING);
    private static final Pattern SEARCH_PATTERN = Pattern.compile(SearchCommand.COMMAND_STRING + " (?<words>\\S+)");
    private static final Pattern TOP_PATTERN = Pattern.compile(TopCommand.COMMAND_STRING + " (?<number>\\d+)");
    private static final Pattern CREATE_PLAYLIST_PATTERN =
        Pattern.compile(CreatePlaylistCommand.COMMAND_STRING + " (?<name>\\S+)");
    private static final Pattern ADD_SONG_TO_PATTERN =
        Pattern.compile(AddSongToPlaylistCommand.COMMAND_STRING + " (?<name>\\S+) (?<song>\\S+)");
    private static final Pattern SHOW_PLAYLIST_PATTERN =
        Pattern.compile(ShowPlaylistCommand.COMMAND_STRING + " (?<name>\\S+)");
    private static final Pattern PLAY_PATTERN = Pattern.compile(PlayServerCommand.COMMAND_STRING + " (?<name>.+)");

    public static SpotifyCommand parse(String command, SpotifyServerInterface server) {
        return switch (command.split("\\s+")[0]) {
            case RegisterCommand.COMMAND_STRING -> parseRegisterCommand(command, server);
            case LoginCommand.COMMAND_STRING -> parseLoginCommand(command, server);
            case DisconnectCommand.COMMAND_STRING -> parseDisconnectCommand(command, server);
            case StopPlaybackCommand.COMMAND_STRING -> parseStopPlaybackCommand(command, server);
            case SearchCommand.COMMAND_STRING -> parseSearchCommand(command, server);
            case TopCommand.COMMAND_STRING -> parseTopCommand(command, server);
            case CreatePlaylistCommand.COMMAND_STRING -> parseCreatePlaylistCommand(command, server);
            case AddSongToPlaylistCommand.COMMAND_STRING -> parseAddSongToPlaylistCommand(command, server);
            case ShowPlaylistCommand.COMMAND_STRING -> parseShowPlaylistCommand(command, server);
            case PlayServerCommand.COMMAND_STRING -> parsePlayCommand(command, server);
            default -> throw new IllegalArgumentException("Invalid command");
        };
    }

    private static SpotifyCommand parseStopPlaybackCommand(String command, SpotifyServerInterface server) {
        Matcher matcher = STOP_PLAYBACK_PATTERN.matcher(command);
        if (matcher.matches()) {
            return new StopPlaybackCommand(server);
        }
        return null;
    }

    private static SpotifyCommand parseRegisterCommand(String command, SpotifyServerInterface server) {
        Matcher matcher = REGISTER_PATTERN.matcher(command);
        if (matcher.matches()) {
            return new RegisterCommand(matcher.group("email"), matcher.group("password"), server);
        }
        return null;
    }

    private static SpotifyCommand parseLoginCommand(String command, SpotifyServerInterface server) {
        Matcher matcher = LOGIN_PATTERN.matcher(command);
        if (matcher.matches()) {
            return new LoginCommand(matcher.group("email"), matcher.group("password"), server);
        }
        return null;
    }

    private static SpotifyCommand parseDisconnectCommand(String command, SpotifyServerInterface server) {
        Matcher matcher = DISCONNECT_PATTERN.matcher(command);
        if (matcher.matches()) {
            return new DisconnectCommand(server);
        }
        return null;
    }

    private static SpotifyCommand parseSearchCommand(String command, SpotifyServerInterface server) {
        Matcher matcher = SEARCH_PATTERN.matcher(command);
        if (matcher.matches()) {
            return new SearchCommand(matcher.group("words"), server);
        }
        return null;
    }

    private static SpotifyCommand parseTopCommand(String command, SpotifyServerInterface server) {
        Matcher matcher = TOP_PATTERN.matcher(command);
        if (matcher.matches()) {
            return new TopCommand(Integer.parseInt(matcher.group("number")), server);
        }
        return null;
    }

    private static SpotifyCommand parseCreatePlaylistCommand(String command, SpotifyServerInterface server) {
        Matcher matcher = CREATE_PLAYLIST_PATTERN.matcher(command);
        if (matcher.matches()) {
            return new CreatePlaylistCommand(matcher.group("name"), server);
        }
        return null;
    }

    private static SpotifyCommand parseAddSongToPlaylistCommand(String command, SpotifyServerInterface server) {
        Matcher matcher = ADD_SONG_TO_PATTERN.matcher(command);
        if (matcher.matches()) {
            return new AddSongToPlaylistCommand(matcher.group("name"), matcher.group("song"), server);
        }
        return null;
    }

    private static SpotifyCommand parseShowPlaylistCommand(String command, SpotifyServerInterface server) {
        Matcher matcher = SHOW_PLAYLIST_PATTERN.matcher(command);
        if (matcher.matches()) {
            return new ShowPlaylistCommand(matcher.group("name"), server);
        }
        return null;
    }

    private static SpotifyCommand parsePlayCommand(String command, SpotifyServerInterface server) {
        Matcher matcher = PLAY_PATTERN.matcher(command);
        if (matcher.matches()) {
            return new PlayServerCommand(matcher.group("name"), server);
        }
        return null;
    }
}
