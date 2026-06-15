package site.meowcat.manager;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Properties;

public class KeyManager {

    private SecretKey secretKey;
    private PublicKey recipientPublicKey;
    private PrivateKey privateKey;

    public void saveKeys(File file) throws IOException {
        Properties props = new Properties();
        if (secretKey != null) {
            props.setProperty("aes.key", Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        }
        if (recipientPublicKey != null) {
            props.setProperty("rsa.public", Base64.getEncoder().encodeToString(recipientPublicKey.getEncoded()));
        }
        if (privateKey != null) {
            props.setProperty("rsa.private", Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        }
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
            props.store(fos, "ShieldCrypt Keys");
        }
    }

    public void loadKeys(File file) throws Exception {
        Properties props = new Properties();
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
            props.load(fis);
        }

        String aesBase64 = props.getProperty("aes.key");
        if (aesBase64 != null) {
            byte[] decodedKey = Base64.getDecoder().decode(aesBase64);
            this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        }

        String rsaPublicBase64 = props.getProperty("rsa.public");
        if (rsaPublicBase64 != null) {
            byte[] decodedKey = Base64.getDecoder().decode(rsaPublicBase64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            this.recipientPublicKey = kf.generatePublic(spec);
        }

        String rsaPrivateBase64 = props.getProperty("rsa.private");
        if (rsaPrivateBase64 != null) {
            byte[] decodedKey = Base64.getDecoder().decode(rsaPrivateBase64);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            this.privateKey = kf.generatePrivate(spec);
        }
    }

    public void generateKey() throws Exception {
        javax.crypto.KeyGenerator generator = javax.crypto.KeyGenerator.getInstance("AES");
        generator.init(256);

        secretKey = generator.generateKey();
    }

    public void generateRSAKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(3072);
        KeyPair pair = generator.generateKeyPair();
        this.recipientPublicKey = pair.getPublic();
        this.privateKey = pair.getPrivate();
    }

    public String getPrivateKeyString() {
        if (privateKey == null) return "";
        return formatKey(Base64.getEncoder().encodeToString(privateKey.getEncoded()), "PRIVATE KEY");
    }

    public String getRecipientPublicKeyString() {
        if (recipientPublicKey == null) return "";
        return formatKey(Base64.getEncoder().encodeToString(recipientPublicKey.getEncoded()), "PUBLIC KEY");
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

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public boolean hasKey() {
        return secretKey != null;
    }
}