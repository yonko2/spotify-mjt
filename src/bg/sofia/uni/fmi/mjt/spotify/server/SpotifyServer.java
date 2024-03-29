package bg.sofia.uni.fmi.mjt.spotify.server;

import bg.sofia.uni.fmi.mjt.spotify.common.CommandParserService;
import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.PersistenceServiceException;
import bg.sofia.uni.fmi.mjt.spotify.server.logger.SpotifyLogger;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import bg.sofia.uni.fmi.mjt.spotify.server.models.User;
import bg.sofia.uni.fmi.mjt.spotify.server.threads.ServerStreamPlayback;
import bg.sofia.uni.fmi.mjt.spotify.server.services.PersistenceService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
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
import java.util.concurrent.ConcurrentMap;

public class SpotifyServer implements SpotifyServerInterface {
    private final ConcurrentMap<String, User> emailToUser = new ConcurrentHashMap<>();
    private final ConcurrentMap<User, List<Playlist>> playlists = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, List<Song>> songs = new ConcurrentHashMap<>();
    private final ConcurrentMap<SelectionKey, User> selectionKeyToUser = new ConcurrentHashMap<>();
    private final ConcurrentMap<SelectionKey, ServerStreamPlayback> playbackThreads = new ConcurrentHashMap<>();

    public SpotifyServer() {
        try {
            PersistenceService.loadApplicationState(this);
        } catch (PersistenceServiceException e) {
            SpotifyLogger.getInstance().log(e.getMessage());
        }
    }

    public ConcurrentMap<String, User> getUsers() {
        return emailToUser;
    }

    public ConcurrentMap<String, List<Song>> getSongs() {
        return songs;
    }

    public ConcurrentMap<User, List<Playlist>> getPlaylists() {
        return playlists;
    }

    @Override
    public ConcurrentMap<SelectionKey, User> getSelectionKeyToUser() {
        return this.selectionKeyToUser;
    }

    public static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 1024;
    private ServerSocketChannel serverSocketChannel;

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);

            this.serverSocketChannel = serverSocketChannel;

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (true) {
                handleChannels(selector, buffer);
            }

        } catch (IOException e) {
            System.out.println("There is a problem with the server socket");
            SpotifyLogger.getInstance().log(e.getMessage());
        } finally {
            try {
                PersistenceService.saveApplicationState(this);
            } catch (PersistenceServiceException e) {
                SpotifyLogger.getInstance().log(e.getMessage());
            }
        }
    }

    private void handleChannels(Selector selector, ByteBuffer buffer) throws IOException {
        int readyChannels = selector.select();
        if (readyChannels == 0) {
            return;
        }

        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            if (key.isReadable()) {
                if (handleKeyReadable(key, buffer)) {
                    continue;
                }
            } else if (key.isAcceptable()) {
                handleKeyAcceptable(key, selector);
            }

            keyIterator.remove();
        }

    }

    private static void handleKeyAcceptable(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private boolean handleKeyReadable(SelectionKey key, ByteBuffer buffer) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        buffer.clear();
        int r = 0;
        try {
            sc.read(buffer);
        } catch (SocketException e) {
            System.out.println("Client has closed the connection");
            SpotifyLogger.getInstance().log(e.getMessage());
            return true;
        }
        if (r < 0) {
            System.out.println("Client has closed the connection");

            sc.close();
            return true;
        }

        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        String cmd = new String(bytes, StandardCharsets.UTF_8).trim();
        CommandResponse cmdResponse = handleClientInput(cmd, key);
        SpotifyLogger.getInstance().log(cmdResponse.message());
        buffer.clear();
        buffer.put(cmdResponse.message().getBytes());
        buffer.flip();
        sc.write(buffer);
        return false;
    }

    private CommandResponse handleClientInput(String cmd, SelectionKey selectionKey) {
        SpotifyCommand command = CommandParserService.parse(cmd, this, selectionKey);
        if (command == null) {
            return new CommandResponse("Invalid command. Please try again.", false);
        }
        return command.execute();
    }

    public void shutdown() {
        if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
            try {
                serverSocketChannel.close();
            } catch (IOException ioe) {
                SpotifyLogger.getInstance().log(ioe.getMessage());
                throw new RuntimeException(ioe);
            }
        }
    }

    public static void main(String[] args) {
        SpotifyServerInterface spotifyServer = new SpotifyServer();
        spotifyServer.start();
    }

    @Override
    public ConcurrentMap<SelectionKey, ServerStreamPlayback> getPlaybackThreads() {
        return playbackThreads;
    }
}
