package site.meowcat.ui;

import javax.swing.*;

public class Interface {
    private JPanel contentFrame;
    private JPanel menuBarPanel;
    private JToolBar menuBar;
    private JTabbedPane tabbedPane1;
    private JTextArea privateKeyArea;
    private JTextArea publicKeyArea;
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

    public Interface() {
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
    }

    public JPanel getContentPane() {
        return contentFrame;
    }
}
