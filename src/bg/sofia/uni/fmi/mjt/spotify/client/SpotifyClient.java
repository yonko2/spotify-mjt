package bg.sofia.uni.fmi.mjt.spotify.client;

import bg.sofia.uni.fmi.mjt.spotify.client.commands.DisconnectCommand;
import bg.sofia.uni.fmi.mjt.spotify.client.commands.PlayClientCommand;
import bg.sofia.uni.fmi.mjt.spotify.client.threads.ClientStreamPlayback;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.StopPlaybackCommand;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class SpotifyClient implements SpotifyClientInterface {
    private ClientStreamPlayback currentPlaybackThread;
    private static final List<String> COMMANDS_LIST = List.of(
        PlayClientCommand.COMMAND_STRING,
        DisconnectCommand.COMMAND_STRING
    );
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 512;
    private static ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private boolean hasConnection = true;

    public static void main(String[] args) {
        SpotifyClientInterface client = new SpotifyClient();
        client.start();
    }

    public void start() {
        try (SocketChannel socketChannel = initConnection(SERVER_HOST, SERVER_PORT);
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                String clientInput = scanner.nextLine();
                if (!hasConnection || clientInput.equalsIgnoreCase(DisconnectCommand.COMMAND_STRING)) {
                    endConnection(socketChannel);
                    break;
                }

//                if (COMMANDS_LIST.contains(clientInput.toLowerCase().split("\\s+")[0])) {
                sendCommandToServer(clientInput, socketChannel);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SocketChannel initConnection(String serverHost, int serverPort) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(serverHost, serverPort));
        System.out.println("[CONNECTED] Connected to the server.");
        hasConnection = true;
        return socketChannel;
    }

    private void endConnection(SocketChannel socketChannel) throws IOException {
        socketChannel.close();
        System.out.println("[DISCONNECTED] Disconnected from the server.");
        hasConnection = false;
    }

    private void sendCommandToServer(String userInput, SocketChannel socketChannel) throws IOException {
        buffer.clear();
        buffer.put(userInput.getBytes());
        buffer.flip();
        socketChannel.write(buffer);

        if (userInput.equalsIgnoreCase(StopPlaybackCommand.COMMAND_STRING)) {
            currentPlaybackThread.interrupt();
        }

        String serverResponse = readServerResponse(socketChannel);
        if (serverResponse.contains("encoding")) {
            new PlayClientCommand(serverResponse, this).execute();
            System.out.println("Now playing");
        } else {
            System.out.println("Response from server:" + System.lineSeparator() + serverResponse);
        }
    }

    private String readServerResponse(SocketChannel socketChannel) throws IOException {
        buffer.clear();
        socketChannel.read(buffer);
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        return new String(bytes, StandardCharsets.UTF_8);
    }

    public ClientStreamPlayback getCurrentPlaybackThread() {
        return currentPlaybackThread;
    }

    public void setCurrentPlaybackThread(ClientStreamPlayback currentPlaybackThread) {
        this.currentPlaybackThread = currentPlaybackThread;
    }
}