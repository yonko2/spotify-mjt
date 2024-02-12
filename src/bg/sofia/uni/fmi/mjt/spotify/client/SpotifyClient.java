package bg.sofia.uni.fmi.mjt.spotify.client;

import bg.sofia.uni.fmi.mjt.spotify.server.commands.DisconnectCommand;
import bg.sofia.uni.fmi.mjt.spotify.client.commands.PlayClientCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.commands.LoginCommand;
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
    private boolean logged = false;

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

                sendCommandToServer(clientInput, socketChannel);

                if (clientInput.equalsIgnoreCase(DisconnectCommand.COMMAND_STRING)) {
                    disconnect();
                    break;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SocketChannel connect(String serverHost, int serverPort) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(serverHost, serverPort));
        System.out.println("Connected to the server.");
        printHelpMenu(false);
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
            System.out.println(serverResponse);
        }

        if (serverResponse.equals(LoginCommand.SUCCESS_RESPONSE.message())) {
            logged = true;
            printHelpMenu(logged);
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
    }

    private static void printHelpMenu(boolean logged) {
        if (logged) {
            System.out.println("""
                
                Available commands:
                play <song>
                stop
                search <words>
                top <number>
                create-playlist <name_of_the_playlist>
                add-song-to <name_of_the_playlist>/<song>
                show-playlist <name_of_the_playlist>
                disconnect
                """);
        } else {
            System.out.println("""
                
                Available commands:
                register <username> <password>
                login <username> <password>
                disconnect
                """);
        }
    }
}