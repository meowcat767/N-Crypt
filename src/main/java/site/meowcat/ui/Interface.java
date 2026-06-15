package site.meowcat.ui;

import java.awt.*;
import javax.swing.*;

import site.meowcat.manager.KeyManager;

public class Interface {
    private JPanel contentFrame;
    private JPanel menuBarPanel;
    private JToolBar menuBar;
    private JTabbedPane tabbedPane1;
    private JTextArea secretKeyArea;
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

    private final KeyManager keyManager = new KeyManager();

    public Interface() {

        secretKeyArea.setEditable(false);

        Font monospacedFont = new Font("Monospaced", Font.PLAIN, 12);
        secretKeyArea.setFont(monospacedFont);

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

        generateKeysButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    contentFrame,
                    "Generate a new secret key? Unsaved keys will be lost.",
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
    }

    public JPanel getContentPane() {
        return contentFrame;
    }
}
