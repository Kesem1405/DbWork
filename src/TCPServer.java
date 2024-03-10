import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TCPServer {

    public static void main(String[] args) throws IOException {
        Sql sql = new Sql();
        ServerSocket serverSocket = new ServerSocket();
        final int PORT = 3306;
        try {
            serverSocket.setReuseAddress(true);
            int port = 1234;
            serverSocket.bind(new InetSocketAddress(port));
            System.out.println("Server started on port " + PORT);
            Sql.connectingToSQL();
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected to client");
                new socketHandler(clientSocket, sql).start();
            } catch (IOException e) {
                System.out.println("I/O error: " + e.getMessage());
            }
        }
    }
}