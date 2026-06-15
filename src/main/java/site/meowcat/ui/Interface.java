package site.meowcat.ui;

import java.awt.*;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Properties;

import site.meowcat.manager.KeyManager;
import site.meowcat.manager.CryptoManager;

public class Interface {
    private JPanel contentFrame;
    private JPanel menuBarPanel;
    private JToolBar menuBar;
    private JTabbedPane tabbedPane1;
    private JTextArea secretKeyArea;
    private JTextArea recipientPublicKeyArea;
    private JPanel textEncryptPanel;
    private JTextArea textIOArea;
    private JButton encryptButton;
    private JButton decryptButton;
    private JPanel imageEncryptPanel;
    private JButton selectImageButton;
    private JLabel imagePathLabel;
    private JLabel imagePreviewLabel;
    private JButton encryptImageButton;
    private JButton decryptImageButton;
    private JButton openButton;
    private JButton saveButton;
    private JButton exitButton;
    private JPanel fileEncryptPanel;
    private JButton selectFileButton;
    private JLabel filePathLabel;
    private JButton encryptFileButton;
    private JButton decryptFileButton;
    private JButton generateKeysButton;
    private JButton generateRSAKeyPairButton;

    private final KeyManager keyManager = new KeyManager();

    private static final String CONFIG_FILE = "shieldcrypt.properties";
    private static final String LAST_KEY_FILE_PROP = "last.key.file";

    public Interface() {

        secretKeyArea.setEditable(false);
        recipientPublicKeyArea.setEditable(true);

        Font monospacedFont = new Font("Monospaced", Font.PLAIN, 12);
        secretKeyArea.setFont(monospacedFont);
        recipientPublicKeyArea.setFont(monospacedFont);

        loadLastKeyFile();

        saveButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("keys.sc"));
            int result = fileChooser.showSaveDialog(contentFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    keyManager.saveKeys(selectedFile);
                    saveLastKeyFilePath(selectedFile.getAbsolutePath());
                    JOptionPane.showMessageDialog(contentFrame, "Keys saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(contentFrame, "Error saving keys: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        openButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(contentFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    keyManager.loadKeys(selectedFile);
                    saveLastKeyFilePath(selectedFile.getAbsolutePath());
                    updateKeyAreas();
                    JOptionPane.showMessageDialog(contentFrame, "Keys loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(contentFrame, "Error loading keys: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        selectImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(contentFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                imagePathLabel.setText(fileChooser.getSelectedFile().getAbsolutePath());
                // TODO: Image Preview Logic
            }
        });
        selectFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(contentFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                filePathLabel.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        exitButton.addActionListener(e -> System.exit(0));

        encryptFileButton.addActionListener(e -> {
            String filePath = filePathLabel.getText();
            if (filePath.equals("No file selected")) {
                JOptionPane.showMessageDialog(contentFrame, "Please select a file first.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String publicKeyPem = recipientPublicKeyArea.getText().trim();
            if (publicKeyPem.isEmpty()) {
                JOptionPane.showMessageDialog(contentFrame, "Please provide a recipient public key.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // 1. Generate AES Key
                keyManager.generateKey();
                secretKeyArea.setText(keyManager.getSecretKeyString());

                // 2. Load Recipient Public Key
                java.security.PublicKey publicKey = CryptoManager.loadRSAPublicKey(publicKeyPem);

                // 3. Encrypt File with AES
                File inputFile = new File(filePath);
                File encryptedFile = new File(filePath + ".enc");
                CryptoManager.encryptFile(inputFile, encryptedFile, keyManager.getSecretKey());

                // 4. Encrypt AES Key with Recipient Public Key
                byte[] encryptedKey = CryptoManager.encryptAESKey(keyManager.getSecretKey(), publicKey);
                File keyFile = new File(filePath + ".key");
                Files.write(keyFile.toPath(), encryptedKey);

                JOptionPane.showMessageDialog(contentFrame,
                        "File encrypted successfully!\nEncrypted file: " + encryptedFile.getName() + "\nEncrypted key: " + keyFile.getName(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(contentFrame, "Error during encryption: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        generateKeysButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    contentFrame,
                    "Generate a new AES secret key? Unsaved keys will be lost.",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );

            if (result != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                keyManager.generateKey();
                secretKeyArea.setText(keyManager.getSecretKeyString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        contentFrame,
                        "Error generating key: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        generateRSAKeyPairButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    contentFrame,
                    "Generate a new RSA key pair? Unsaved keys will be lost.",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );

            if (result != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                keyManager.generateRSAKeyPair();
                recipientPublicKeyArea.setText(keyManager.getRecipientPublicKeyString());
                // For now, let's show a message that the private key was also generated, 
                // even if we don't have a dedicated field for it yet in this tab.
                JOptionPane.showMessageDialog(contentFrame, 
                        "RSA Key Pair generated successfully!\nPublic Key set in the field.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        contentFrame,
                        "Error generating RSA keys: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    public JPanel getContentPane() {
        return contentFrame;
    }

    private void updateKeyAreas() {
        secretKeyArea.setText(keyManager.getSecretKeyString());
        recipientPublicKeyArea.setText(keyManager.getRecipientPublicKeyString());
    }

    private void loadLastKeyFile() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
                String lastKeyFile = props.getProperty(LAST_KEY_FILE_PROP);
                if (lastKeyFile != null) {
                    File keyFile = new File(lastKeyFile);
                    if (keyFile.exists()) {
                        keyManager.loadKeys(keyFile);
                        updateKeyAreas();
                    }
                }
            } catch (Exception e) {
                System.err.println("Could not load last key file: " + e.getMessage());
            }
        }
    }

    private void saveLastKeyFilePath(String path) {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
            } catch (Exception e) {
                // ignore
            }
        }
        props.setProperty(LAST_KEY_FILE_PROP, path);
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            props.store(fos, "ShieldCrypt Config");
        } catch (Exception e) {
            System.err.println("Could not save last key file path: " + e.getMessage());
        }
    }
}
