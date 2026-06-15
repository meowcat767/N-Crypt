package site.meowcat.manager;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class KeyManager {

    private SecretKey secretKey;
    private PublicKey recipientPublicKey;

    public void generateKey() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256);

        secretKey = generator.generateKey();
    }

    public void generateRSAKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        // For the sake of this tool, we might want to be able to generate our own pairs too
        // but the requirement says "Recipient Public Key".
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setRecipientPublicKey(PublicKey publicKey) {
        this.recipientPublicKey = publicKey;
    }

    public PublicKey getRecipientPublicKey() {
        return recipientPublicKey;
    }

    public String getSecretKeyString() {
        if (secretKey == null) return "";
        return formatKey(Base64.getEncoder().encodeToString(secretKey.getEncoded()), "AES SECRET KEY");
    }

    public String formatKey(String encodedKey, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN ").append(type).append("-----\n");
        for (int i = 0; i < encodedKey.length(); i += 64) {
            int end = Math.min(i + 64, encodedKey.length());
            sb.append(encodedKey, i, end).append("\n");
        }
        sb.append("-----END ").append(type).append("-----");
        return sb.toString();
    }

    public boolean hasKey() {
        return secretKey != null;
    }
}