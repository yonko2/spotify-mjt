package bg.sofia.uni.fmi.mjt.spotify.client;

import bg.sofia.uni.fmi.mjt.spotify.server.commands.DisconnectCommand;
import bg.sofia.uni.fmi.mjt.spotify.client.commands.PlayClientCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.StopPlaybackCommand;

import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SpotifyClient implements SpotifyClientInterface {
    private SourceDataLine dataLine;
    private SocketChannel mainSocketChannel;
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
        try (SocketChannel socketChannel = connect(SERVER_HOST, SERVER_PORT);
             Scanner scanner = new Scanner(System.in)) {

            this.mainSocketChannel = socketChannel;

            while (true) {
                String clientInput = scanner.nextLine();
                if (!hasConnection || clientInput.equalsIgnoreCase(DisconnectCommand.COMMAND_STRING)) {
                    disconnect();
                    break;
                }

                sendCommandToServer(clientInput, socketChannel);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SocketChannel connect(String serverHost, int serverPort) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(serverHost, serverPort));
        System.out.println("[CONNECTED] Connected to the server.");
        hasConnection = true;
        return socketChannel;
    }

    private void sendCommandToServer(String userInput, SocketChannel socketChannel) throws IOException {
        buffer.clear();
        buffer.put(userInput.getBytes());
        buffer.flip();
        socketChannel.write(buffer);

        if (userInput.equalsIgnoreCase(StopPlaybackCommand.COMMAND_STRING)) {
            dataLine.close();
        }

        String serverResponse = readServerResponse(socketChannel);
        if (serverResponse.contains("encoding")) {
            new PlayClientCommand(serverResponse, this).execute();
            System.out.println("Now playing");
        } else {
            System.out.println("Response from server:" + System.lineSeparator() + serverResponse);
        }

        if (userInput.equalsIgnoreCase(DisconnectCommand.COMMAND_STRING)) {
            disconnect();
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

    @Override
    public void setSourceDataLine(SourceDataLine dataLine) {
        this.dataLine = dataLine;
    }

    @Override
    public void disconnect() throws IOException {
        this.mainSocketChannel.close();
        hasConnection = false;
    }
}