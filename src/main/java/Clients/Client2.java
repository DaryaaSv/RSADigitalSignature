package Clients;

import RSAalgorithm.RSASignature;

import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.PublicKey;

class Client2 {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1234;

    public static void main(String[] args) {
        try {
            // Connect to the server
            Socket serverSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to server!");

            // Receive the public key, message, and digital signature from the server
            ObjectInputStream objectInputStream = new ObjectInputStream(serverSocket.getInputStream());
            DataInputStream dataInputStream = new DataInputStream(serverSocket.getInputStream());
            PublicKey publicKey = (PublicKey) objectInputStream.readObject();
            String message = dataInputStream.readUTF();
            String digitalSignature = dataInputStream.readUTF();
            System.out.println("Received message from server: " + message);
            System.out.println("Received signature from server: " + digitalSignature);

            // Verify the digital signature
            RSASignature rsa = new RSASignature();
            System.out.println("Digital signature is valid: " + rsa.verify(publicKey, message, digitalSignature));

            // Close the connections
            objectInputStream.close();
            objectInputStream.close();
            serverSocket.close();
        } catch (Exception e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
    }
}