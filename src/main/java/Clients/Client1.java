package Clients;

import RSAalgorithm.RSASignature;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Scanner;

public class Client1 {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1234;
    public static void main(String[] args) {
        try {
            // Connect to Server
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to server!");

            Scanner in = new Scanner(System.in);
            System.out.println("Type in a message you want to send:");
            String message = in.nextLine();

            RSASignature rsa = new RSASignature();
            String signature = rsa.sign(message);
            PublicKey publicKey = rsa.getPublicKey();

            // Send Public Key
            ObjectOutputStream publicKeyObjectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            publicKeyObjectOutputStream.writeObject(publicKey);
            publicKeyObjectOutputStream.flush();
            System.out.println("Sent Public Key");

            // Send Message
            DataOutputStream messageDataOutputStream = new DataOutputStream(socket.getOutputStream());
            messageDataOutputStream.writeUTF(message);
            messageDataOutputStream.flush();
            System.out.println("Sent Message: " + message);

            // Send Digital Signature
            DataOutputStream signatureDataOutputStream = new DataOutputStream(socket.getOutputStream());
            signatureDataOutputStream.writeUTF(signature);
            signatureDataOutputStream.flush();
            System.out.println("Sent Digital Signature: " + signature);

            // Close Connections
            publicKeyObjectOutputStream.close();
            messageDataOutputStream.close();
            signatureDataOutputStream.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
