package RSAalgorithm;

import java.security.*;
import java.util.Base64;

public class RSASignature {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Signature signature;
    private String message;

    public RSASignature() throws NoSuchAlgorithmException {
        // Generate a new key pair for RSA encryption and decryption
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Get the private and public keys from the key pair
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        // Create a signature object using SHA-256 with RSA encryption
        signature = Signature.getInstance("SHA256withRSA");
    }

    public String sign(String message) throws InvalidKeyException, SignatureException {
        this.message = message;
        // Initialize the signature object with the private key
        signature.initSign(privateKey);

        // Update the signature object with the message bytes
        byte[] messageBytes = message.getBytes();
        signature.update(messageBytes);

        // Generate the digital signature for the message
        byte[] digitalSignature = signature.sign();

        // Encode the digital signature as a Base64 string and return it
        return Base64.getEncoder().encodeToString(digitalSignature);
    }

    public boolean verify(PublicKey publicKey, String message, String signatureString) throws InvalidKeyException, SignatureException {
        // Decode the Base64-encoded digital signature string
        byte[] digitalSignature = Base64.getDecoder().decode(signatureString);

        // Initialize the signature object with the public key
        signature.initVerify(publicKey);

        // Update the signature object with the message bytes
        byte[] messageBytes = message.getBytes();
        signature.update(messageBytes);

        // Verify the digital signature for the message using the public key
        return signature.verify(digitalSignature);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getMessage() {
        return message;
    }

    public Signature getSignature() {
        return signature;
    }
}
