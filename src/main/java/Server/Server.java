package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Server {
    private static final int PORT = 1234;

    public static void main(String[] args) {
        try {
            // Create a server socket
            Socket client1Socket;
            Socket client2Socket;
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Server started, waiting for connections...");

                // Accept the first client connection
                client1Socket = serverSocket.accept();
                System.out.println("Client 1 connected");

                // Wait for the second client connection
                client2Socket = serverSocket.accept();
                System.out.println("Client 2 connected");

                // Create threads for each client
                Thread client1Thread = new Thread(new ClientHandler(client1Socket, client2Socket));
                Thread client2Thread = new Thread(new ClientHandler(client2Socket, client1Socket));
                client1Thread.start();
                client2Thread.start();
            }

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket incomingClientSocket;
    private final Socket outgoingClientSocket;
    private static final String PATTERN = "[^A-Za-z0-9+/=]";

    public ClientHandler(Socket incomingClientSocket, Socket outgoingClientSocket) {
        this.incomingClientSocket = incomingClientSocket;
        this.outgoingClientSocket = outgoingClientSocket;
    }

    @Override
    public void run() {
        try {
            System.out.println("Waiting for data from incoming client...");

            // Receive data from incoming client and send it to outgoing client
            ObjectInputStream objectInputStream = new ObjectInputStream(incomingClientSocket.getInputStream());
            DataInputStream dataInputStream = new DataInputStream(incomingClientSocket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outgoingClientSocket.getOutputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(outgoingClientSocket.getOutputStream());

            PublicKey publicKey = (PublicKey) objectInputStream.readObject();
            System.out.println("Received public key from incoming client");
            String message = dataInputStream.readUTF();
            System.out.println("Received message from incoming client: " + message);
            String digitalSignature = dataInputStream.readUTF();
            System.out.println("Received digital signature from incoming client: " + digitalSignature);

            //An ability to change the digital signature
            System.out.println("Would you like to change the value of the digital signature? (Type in + for yes, - for no)");
            Scanner in = new Scanner(System.in);
            String result = in.nextLine();
            if (result.equals("+")) {
                // Generate the digital signature for the text
                byte[] signatureBytes = digitalSignature.getBytes(UTF_8);

                // Attack: changing the first byte of the digital signature
                signatureBytes[0] = (byte) ~signatureBytes[0];
                digitalSignature = new String(signatureBytes, UTF_8);
                digitalSignature = digitalSignature.replaceAll(PATTERN, "A");
            }

            // Send data to outgoing client
            objectOutputStream.writeObject(publicKey);
            objectOutputStream.flush();
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
            dataOutputStream.writeUTF(digitalSignature);
            dataOutputStream.flush();
            System.out.println("Sent data to outgoing client");

            // Close the streams and sockets
            objectInputStream.close();
            dataInputStream.close();
            objectOutputStream.close();
            dataOutputStream.close();
            incomingClientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error handling client: " + e.getMessage());
        }
    }
}