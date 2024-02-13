package bg.sofia.uni.fmi.mjt.spotify.common;

import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.AddSongToPlaylistCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.CreatePlaylistCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.DisconnectCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.LoginCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.PlayServerCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.RegisterCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.SearchCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.ShowPlaylistCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.StopPlaybackCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.TopCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.models.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.channels.SelectionKey;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommandParserServiceTest {
    private static final List<User> usersList = List.of(
        new User(UUID.randomUUID(), "email1", "cc175b9c0f1b6a831c399e269772661", List.of()),
        new User(UUID.randomUUID(), "email2", "92eb5ffee6ae2fec3ad71c777531578f", List.of())
    );

    private static final SpotifyServerInterface server = mock(SpotifyServerInterface.class);
    private static final ConcurrentHashMap<SelectionKey, User> selectionKeyToUser = new ConcurrentHashMap<>();
    private static final List<SelectionKey> selectionKeyList =
        List.of(mock(SelectionKey.class), mock(SelectionKey.class));

    @BeforeAll
    static void setUp() {
        when(server.getSelectionKeyToUser()).thenReturn(selectionKeyToUser);

        selectionKeyToUser.put(selectionKeyList.get(1), usersList.get(1));
    }

    @Test
    void testParseNoSuchCommand() {
        assertNull(CommandParserService.parse("NoSuchCommand", server, selectionKeyList.get(1)));
        assertNull(CommandParserService.parse("NoSuchCommand", server, selectionKeyList.get(0)));
    }

    @Test
    void testParseNotLoggedCommandsSuccess() {
        assertInstanceOf(LoginCommand.class,
            CommandParserService.parse("login a a", server, selectionKeyList.get(0)));
        assertInstanceOf(RegisterCommand.class,
            CommandParserService.parse("register a a", server, selectionKeyList.get(0)));
    }

    @Test
    void testParseLoggedCommandsSuccess() {
        assertInstanceOf(DisconnectCommand.class,
            CommandParserService.parse("disconnect", server, selectionKeyList.get(1)));
        assertInstanceOf(StopPlaybackCommand.class,
            CommandParserService.parse("stop", server, selectionKeyList.get(1)));
        assertInstanceOf(SearchCommand.class,
            CommandParserService.parse("search test", server, selectionKeyList.get(1)));
        assertInstanceOf(TopCommand.class,
            CommandParserService.parse("top 1", server, selectionKeyList.get(1)));
        assertInstanceOf(CreatePlaylistCommand.class,
            CommandParserService.parse("create-playlist test", server, selectionKeyList.get(1)));
        assertInstanceOf(AddSongToPlaylistCommand.class,
            CommandParserService.parse("add-song-to test/test", server, selectionKeyList.get(1)));
        assertInstanceOf(ShowPlaylistCommand.class,
            CommandParserService.parse("show-playlist test", server, selectionKeyList.get(1)));
        assertInstanceOf(PlayServerCommand.class,
            CommandParserService.parse("play test", server, selectionKeyList.get(1)));
    }
}
