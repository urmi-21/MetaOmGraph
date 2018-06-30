package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class MetNet3LoginDialog {
    private static boolean success;

    public MetNet3LoginDialog() {
    }

    public static boolean showDialog(Component parent) {
        final JDialog dialog = new JDialog();
        final JTextField userField = new JTextField();
        final JPasswordField passField = new JPasswordField();
        JLabel userLabel = new JLabel("Username: ");
        JLabel passLabel = new JLabel("Password: ");
        dialog.getContentPane().setLayout(new java.awt.GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = 2;
        try {
            dialog.add(new WelcomeHeader("Log in to MetNet3", ImageIO.read(MetNet3LoginDialog.class.getResourceAsStream("/resource/tango/Unlocked.png"))), c);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridy = 1;
        c.anchor = 13;
        c.fill = 0;
        c.weightx = 0.0D;
        c.weighty = 0.5D;
        dialog.getContentPane().add(userLabel, c);
        c.gridy = 2;
        dialog.getContentPane().add(passLabel, c);
        c.gridy = 1;
        c.gridx = 1;
        c.weightx = 1.0D;
        c.anchor = 17;
        c.fill = 2;
        dialog.getContentPane().add(userField, c);
        c.gridy = 2;
        dialog.getContentPane().add(passField, c);
        c.gridy = 3;
        c.gridwidth = 2;
        dialog.getContentPane().add(new JLabel(" "), c);
        JPanel buttonPanel = new JPanel();

        JButton okButton = new JButton("OK");
        ActionListener loginAction = new ActionListener() {
            private JLabel statusLabel = null;

            public void actionPerformed(ActionEvent e) {
                if (statusLabel == null) {
                    statusLabel = new JLabel("Logging in...");
                    c.gridx = 0;
                    c.gridy = 3;
                    c.gridwidth = 2;
                    c.anchor = 10;
                    c.fill = 0;

                    dialog.getContentPane().add(statusLabel, c);
                } else {
                    statusLabel.setText("Logging in...");
                }

                if (edu.iastate.metnet.my.My.Authenticate(userField.getText(), new String(passField.getPassword()))) {
                    System.out.println("Login successful");
                    MetNet3LoginDialog.success = true;
                    dialog.dispose();
                } else {
                    statusLabel.setText("Invalid username/password");
                }
            }
        };
        okButton.addActionListener(loginAction);
        passField.addActionListener(loginAction);
        userField.addActionListener(loginAction);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MetNet3LoginDialog.success = false;
                dialog.dispose();
            }
        });
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        c.gridy = 4;
        c.gridx = 0;
        c.gridwidth = 2;
        dialog.getContentPane().add(buttonPanel, c);
        dialog.setModal(true);
        dialog.setSize(300, 200);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(parent);
        success = false;
        dialog.setVisible(true);
        return success;
    }

    public static void main(String[] args) {
        showDialog(null);
    }
}
