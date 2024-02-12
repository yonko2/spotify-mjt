package bg.sofia.uni.fmi.mjt.spotify.server;

import bg.sofia.uni.fmi.mjt.spotify.common.CommandParserService;
import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.PersistenceServiceException;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import bg.sofia.uni.fmi.mjt.spotify.server.models.User;
import bg.sofia.uni.fmi.mjt.spotify.server.services.PersistenceService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SpotifyServer implements SpotifyServerInterface {
    private final ConcurrentHashMap<String, User> emailToUser = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<User, List<Playlist>> playlists = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<Song>> songs = new ConcurrentHashMap<>();
    private User loggedUser = null;

    public SpotifyServer() {
        try {
            PersistenceService.loadApplicationState(this);
        } catch (PersistenceServiceException e) {
            throw new RuntimeException("An error occurred while loading the data.", e);
        }
    }

    public ConcurrentHashMap<String, User> getUsers() {
        return emailToUser;
    }

    public ConcurrentHashMap<String, List<Song>> getSongs() {
        return songs;
    }

    public ConcurrentHashMap<User, List<Playlist>> getPlaylists() {
        return playlists;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 1024;

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (true) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        System.out.println("Reading from client");
                        SocketChannel sc = (SocketChannel) key.channel();

                        buffer.clear();
                        int r = sc.read(buffer);
                        if (r < 0) {
                            System.out.println("Client has closed the connection");
                            sc.close();
                            continue;
                        }

                        buffer.flip();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);

                        String cmd = new String(bytes, StandardCharsets.UTF_8).trim();
                        CommandResponse cmdResponse = handleClientInput(cmd);

                        buffer.clear();
                        buffer.put(cmdResponse.message().getBytes());
                        buffer.flip();
                        sc.write(buffer);

                    } else if (key.isAcceptable()) {
                        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
                        SocketChannel accept = sockChannel.accept();
                        accept.configureBlocking(false);
                        accept.register(selector, SelectionKey.OP_READ);
                    }

                    keyIterator.remove();
                }

            }

        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the server socket", e);
        }
    }

    private CommandResponse handleClientInput(String cmd) {
        SpotifyCommand command = CommandParserService.parse(cmd, this);
        if (command == null) {
            return new CommandResponse("Invalid command. Please try again.", false);
        }
        return command.execute();
    }

    @Override
    public void setLoggedUser(User user) {
        this.loggedUser = user;
    }

    public static void main(String[] args) {
        SpotifyServerInterface spotifyServer = new SpotifyServer();
        spotifyServer.start();
    }
}