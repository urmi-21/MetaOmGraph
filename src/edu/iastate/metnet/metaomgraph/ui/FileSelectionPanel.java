package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;


public class FileSelectionPanel
        extends JPanel {
    private JTextField[] fields;
    private JButton[] buttons;
    private String[] names;
    private boolean isOpenMode;

    public static void main(String[] args) {
        String[] names = {"file1", "something", "something longer"};
        String[] extensions = {"xml", "txt", "html"};
        String[] descriptions = {"XML files", "Text files", "HTML files"};
        String[] defaultFiles = {"test1.xml", "something.txt"};
        FileSelectionPanel panel = new FileSelectionPanel(names, defaultFiles, extensions, descriptions);
        JFrame f = new JFrame("FileSelectionPanel test");
        f.getContentPane().add(panel, "South");
        f.getContentPane().add(new JLabel("second!"), "South");
        f.pack();
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }

    public FileSelectionPanel(String name, String defaultFile, String extension, String description) {
        this(new String[]{name}, new String[]{defaultFile}, new String[]{extension}, new String[]{description});
    }

    public FileSelectionPanel(String[] names, String[] defaultFiles, String[] extensions, String[] descriptions) {
        super(new GridBagLayout());
        fields = new JTextField[names.length];
        buttons = new JButton[names.length];
        this.names = names;
        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < names.length; i++) {
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 0.0D;
            c.anchor = 13;
            c.fill = 0;
            JLabel label = new JLabel(names[i]);
            add(label, c);
            c.gridx = 1;
            c.fill = 2;
            c.weightx = 1.0D;
            fields[i] = new JTextField();
            if (defaultFiles[i] != null) {
                fields[i].setText(defaultFiles[i]);
            }
            add(fields[i], c);
            c.gridx = 2;
            c.weightx = 0.0D;
            buttons[i] = new JButton("Browse...");
            buttons[i].addActionListener(new FileBrowseListener(extensions[i], descriptions[i],
                    fields[i], this));
            add(buttons[i], c);
        }
    }

    public File[] getFiles() {
        File[] result = new File[fields.length];
        for (int i = 0; i < result.length; i++) {
            if (fields[i].getText().trim().equals("")) {
                result[i] = null;
            } else {
                result[i] = new File(fields[i].getText());
            }
        }
        return result;
    }

    public File getFile(int index) {
        if (fields[index].getText().trim().equals("")) {
            return null;
        }
        return new File(fields[index].getText());
    }

    public boolean checkFiles() {
        File[] files = getFiles();
        Color defaultbg = new JTextField().getBackground();
        for (JTextField thisField : fields) {
            thisField.setBackground(defaultbg);
        }
        for (int i = 0; i < files.length; i++) {
            if ((files[i] == null) || (files[i].getName().equals(""))) {
                JOptionPane.showMessageDialog(getParent(),
                        names[i] + " must contain a valid file.", "Error",
                        0);
                fields[i].setBackground(new Color(255, 200, 200));
                return false;
            }
            if (files[i].isDirectory()) {
                JOptionPane.showMessageDialog(getParent(), names[i] + " must not be a directory.",
                        "Error", 0);
                fields[i].setBackground(new Color(255, 200, 200));
                return false;
            }
            if ((files[i].exists()) && (!files[i].canWrite())) {
                JOptionPane.showMessageDialog(getParent(), "Unable to write to " + names[i],
                        "Error", 0);
                fields[i].setBackground(new Color(255, 200, 200));
                return false;
            }
            if ((files[i].getParentFile() == null) || (!files[i].getParentFile().canWrite())) {
                JOptionPane.showMessageDialog(getParent(), "Unable to write to the directory " +
                        files[i].getParent(), "Error", 0);
                fields[i].setBackground(new Color(255, 200, 200));
                return false;
            }
            for (int j = 0; j < i; j++) {
                if ((files[i].equals(files[j])) && (!isOpenMode())) {
                    JOptionPane.showMessageDialog(getParent(), names[i] +
                                    " must be a different file than " + names[j], "Error",
                            0);
                    fields[i].setBackground(new Color(255, 200, 200));
                    fields[j].setBackground(new Color(255, 200, 200));
                }
            }
        }

        return true;
    }

    public void setOpenMode(boolean openMode) {
        isOpenMode = openMode;
    }

    public boolean isOpenMode() {
        return isOpenMode;
    }

    public void setEnabled(boolean enabled) {
        for (int i = 0; i < fields.length; i++) {
            fields[i].setEnabled(enabled);
            buttons[i].setEnabled(enabled);
        }
    }

    private static class FileBrowseListener implements ActionListener {
        private String ext;
        private String descrip;
        private JTextField field;
        private FileSelectionPanel myPanel;

        public FileBrowseListener(String extension, String description, JTextField field, FileSelectionPanel myPanel) {
            ext = extension;
            descrip = description;
            this.field = field;
            this.myPanel = myPanel;
        }

        public void actionPerformed(ActionEvent e) {
            File source = null;


            FileFilter filter = null;
            if (ext != null) {
                filter = Utils.createFileFilter(ext, descrip);
            }


            if (!myPanel.isOpenMode()) {
                source = Utils.chooseFileToSave(filter, ext, field.getParent(), true);
            } else {
                source = Utils.chooseFileToOpen(filter, field.getParent());
            }
            if (source != null) {
                field.setText(source.getAbsolutePath());
            }
        }
    }
}
