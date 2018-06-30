package edu.iastate.metnet.soft;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmHelpListener;
import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ProgressMonitor;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import net.iharder.dnd.FileDrop;
import net.iharder.dnd.FileDrop.Listener;

public class SOFTParserOptionPanel extends JPanel implements ActionListener, ListSelectionListener, ListCellRenderer {
    public static String ADD_FILE_COMMAND = "Add a file";

    public static String REMOVE_FILE_COMMAND = "Remove a file";

    public static String OK_COMMAND = "OK";

    public static String CANCEL_COMMAND = "CANCEL";

    public static String DESTINATION_BROWSE_COMMAND = "Pick a destination";

    private JList fileList;

    private DefaultListModel fileListModel;

    private JButton fileAddButton;

    private JButton fileRemoveButton;

    private JRadioButton sortSeriesRadio;

    private JRadioButton sortPlatformRadio;

    private JTable rowIDTable;

    private NoneditableTableModel rowIDTableModel;

    private JScrollPane rowIDTablePane;

    private JScrollPane fileListPane;

    private boolean isOK;
    private JDialog dialog;
    private File destination;
    private JTextField destinationField;
    private JButton destinationBrowseButton;

    public SOFTParserOptionPanel() {
        isOK = false;
        destination = null;
        fileListModel = new DefaultListModel();
        fileList = new JList(fileListModel);
        fileList
                .setSelectionMode(2);
        fileList.addListSelectionListener(this);
        fileList.setCellRenderer(this);
        fileListPane = new JScrollPane(fileList);
        fileListPane.setPreferredSize(new Dimension(200, 100));
        fileAddButton = new JButton("+");
        fileAddButton.setActionCommand(ADD_FILE_COMMAND);
        fileAddButton.addActionListener(this);
        fileRemoveButton = new JButton("-");
        fileRemoveButton.setActionCommand(REMOVE_FILE_COMMAND);
        fileRemoveButton.addActionListener(this);
        fileRemoveButton.setEnabled(false);
        JPanel fileButtonPanel = new JPanel();
        fileButtonPanel.add(fileAddButton);
        fileButtonPanel.add(fileRemoveButton);
        JPanel fileListPanel = new JPanel(new BorderLayout());
        fileListPanel.add(fileListPane, "Center");
        fileListPanel.add(fileButtonPanel, "South");
        Border etched = BorderFactory.createEtchedBorder();
        fileListPanel.setBorder(BorderFactory.createTitledBorder(etched,
                "Files"));
        sortSeriesRadio = new JRadioButton("Sort By Series");
        sortPlatformRadio = new JRadioButton("Sort By Platform");
        ButtonGroup sortGroup = new ButtonGroup();
        sortGroup.add(sortSeriesRadio);
        sortGroup.add(sortPlatformRadio);
        sortSeriesRadio.setSelected(true);
        rowIDTableModel = new NoneditableTableModel(null, new String[]{"ID",
                "Include"});
        rowIDTableModel.setColumnEditable(1, true);
        rowIDTable = new JTable(rowIDTableModel);
        rowIDTablePane = new JScrollPane(rowIDTable);
        rowIDTablePane.setBorder(BorderFactory.createTitledBorder(etched, "Row IDs"));
        JPanel destinationPanel = new JPanel(new BorderLayout());
        destinationField = new JTextField();
        destinationField.setEditable(false);
        destinationBrowseButton = new JButton("Browse");
        destinationBrowseButton.setActionCommand(DESTINATION_BROWSE_COMMAND);
        destinationBrowseButton.addActionListener(this);
        destinationPanel.add(new JLabel("Destination Data File Name:"), "Before");
        destinationPanel.add(destinationField, "Center");
        destinationPanel.add(destinationBrowseButton, "After");
        JPanel mainPanel = new JPanel(new GridBagLayout());
        setLayout(new BorderLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        c.gridwidth = 1;
        c.weightx = 0.0D;
        c.weighty = 1.0D;
        c.fill = 1;
        mainPanel.add(fileListPanel, c);
        c.gridx = 1;
        c.gridheight = 2;
        c.weighty = 1.0D;
        c.weightx = 1.0D;
        c.gridwidth = 2;
        c.fill = 1;
        mainPanel.add(new JScrollPane(rowIDTablePane), c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.weighty = 0.0D;
        c.fill = 2;
        mainPanel.add(destinationPanel, c);
        JLabel warningLabel = new JLabel("WARNING: This feature is not complete.  Ensure all series/samples have the same platform.");
        warningLabel.setForeground(Color.RED);
        warningLabel.setHorizontalAlignment(0);
        add(warningLabel, "First");
        add(mainPanel, "Center");
        new FileDrop(this, null, new FileDrop.Listener() {
            public void filesDropped(File[] files) {
                ProgressMonitor pm = new ProgressMonitor(MetaOmGraph.getMainWindow(), "Adding files", "", 0, files.length);
                pm.setMillisToDecideToPopup(0);
                pm.setMillisToPopup(0);
                pm.setProgress(0);
                for (int x = 0; x < files.length; x++) {
                    pm.setNote(files[x].getName());
                    addFileToList(new SOFTFile(files[x]));
                    pm.setProgress(x);
                }
                pm.close();
            }
        });
    }

    public JDialog makeDialog(Frame parent) {
        isOK = false;
        JPanel buttonPanel = new JPanel();

        JButton okButton = new JButton("OK");
        okButton.setActionCommand(OK_COMMAND);
        okButton.addActionListener(this);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand(CANCEL_COMMAND);
        cancelButton.addActionListener(this);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog = new JDialog(parent, "Open SOFT files");
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.add(this, "Center");
        dialog.add(buttonPanel, "South");
        dialog.pack();
        dialog.setDefaultCloseOperation(2);
        dialog.setLocationRelativeTo(null);
        AbstractAction helpAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                MetaOmGraph.getHelpListener().actionPerformed(new ActionEvent(dialog, 0, "newproject-soft.php"));
            }
        };
        dialog.getRootPane().getActionMap().put("help", helpAction);
        InputMap im = getRootPane().getInputMap(1);
        im.put(KeyStroke.getKeyStroke(112, 0), "help");
        return dialog;
    }

    public JDialog makeDialog() {
        return makeDialog(null);
    }

    public SOFTFile[] getFilesToParse() {
        SOFTFile[] result = new SOFTFile[fileListModel.size()];
        for (int x = 0; x < result.length; x++) {
            result[x] = ((SOFTFile) fileListModel.get(x));
        }
        return result;
    }

    public boolean isSortSeriesSelected() {
        return sortSeriesRadio.isSelected();
    }

    public String[] getRowIDs() {
        Vector<String> idVector = new Vector();
        for (int x = 0; x < rowIDTableModel.getRowCount(); x++) {
            boolean addMe = ((Boolean) rowIDTableModel.getValueAt(x, 1)).booleanValue();
            if (addMe) {
                idVector.add(rowIDTableModel.getValueAt(x, 0) + "");
            }
        }
        if (idVector.size() <= 0)
            return null;
        String[] result = new String[idVector.size()];
        for (int x = 0; x < idVector.size(); x++) {
            result[x] = idVector.get(x);
            System.out.println("Row ID: " + result[x]);
        }
        return result;
    }

    public File getDestination() {
        return destination;
    }

    public void actionPerformed(ActionEvent e) {
        if (ADD_FILE_COMMAND.equals(e.getActionCommand())) {
            FileFilter filter = Utils.createFileFilter(new String[]{"soft", "txt"}, "SOFT files");
            JFileChooser chooser = new JFileChooser(Utils.getLastDir());
            chooser.setFileFilter(filter);
            chooser.setMultiSelectionEnabled(true);
            chooser.showOpenDialog(MetaOmGraph.getMainWindow());
            File[] openUs = chooser.getSelectedFiles();
            if ((openUs == null) || (openUs.length <= 0)) {
                return;
            }
            Utils.setLastDir(openUs[0].getParentFile());

            for (int x = 0; x < openUs.length; x++) {
                addFileToList(new SOFTFile(openUs[x]));
            }


            return;
        }
        if (REMOVE_FILE_COMMAND.equals(e.getActionCommand())) {
            int[] selected = fileList.getSelectedIndices();
            if ((selected == null) || (selected.length <= 0)) {
                return;
            }
            for (int x = selected.length - 1; x >= 0; x--) {
                fileListModel.removeElementAt(selected[x]);
            }
            if (fileListModel.size() <= 0) {
                destinationField.setText("");
            }
            return;
        }
        if (OK_COMMAND.equals(e.getActionCommand())) {
            System.out.println("Files to parse:");
            File[] files = getFilesToParse();
            for (int x = 0; x < files.length; x++) {
                System.out.println(files[x].getAbsolutePath());
            }
            System.out.println("ID columns to include:");


            System.out.println("Sort by: " + (
                    isSortSeriesSelected() ? "series" : "platform"));
            if (fileListModel.size() <= 0) {
                JOptionPane.showMessageDialog(this,
                        "You must choose at least one SOFT file to convert",
                        "No SOFT Files", 0);
                fileList.setBorder(BorderFactory.createLineBorder(Color.RED));
                return;
            }
            if (destination == null) {
                JOptionPane.showMessageDialog(this,
                        "You must choose a destination file!",
                        "No Destination", 0);
                destinationField.setBorder(
                        BorderFactory.createLineBorder(Color.RED));
                return;
            }
            isOK = true;
            if (dialog != null) {
                dialog.dispose();
            }
            return;
        }
        if (CANCEL_COMMAND.equals(e.getActionCommand())) {
            isOK = false;
            if (dialog != null) {
                dialog.dispose();
            }
            return;
        }
        if (DESTINATION_BROWSE_COMMAND.equals(e.getActionCommand())) {
            File dest = Utils.chooseFileToSave(null, "txt", this, false);
            if (dest == null)
                return;
            if ((dest.exists()) && (!dest.canWrite())) {
                JOptionPane.showMessageDialog(this,
                        "Unable to write to the selected file.", "Write error",
                        0);
                return;
            }
            destinationField.setText(dest.getAbsolutePath());
            destination = dest;
            destinationField.setBorder(new JTextField().getBorder());
            destinationField.setEditable(false);
        }
    }

    public void addFileToList(SOFTFile addMe) {
        if (addMe == null)
            return;
        if (destinationField.getText().equals("")) {
            int extIndex = addMe.getName().lastIndexOf(".");
            String newName = addMe.getName().substring(0, extIndex) +
                    ".data.txt";
            newName = addMe.getParent() + File.separator + newName;
            destinationField.setText(newName);
            destination = new File(newName);
        }
        fileList.setBorder(null);
        fileListModel.addElement(addMe);
        String[] rowIDs = addMe.getRowIDs();
        if (rowIDs == null)
            return;
        Vector<String> goodIDs = new Vector();
        for (int x = 0; x < rowIDs.length; x++) {
            boolean found = false;
            int i = 0;
            while ((i < rowIDTableModel.getRowCount()) && (!found)) {
                if (rowIDs[x].toLowerCase().equals(
                        (rowIDTableModel.getValueAt(i, 0) + "").toLowerCase())) {
                    found = true;
                }
                i++;
            }
            if (!found) {
                goodIDs.add(rowIDs[x]);
            }
        }
        if (goodIDs.size() > 0) {
            System.out.println("Adding " + goodIDs.size() + " IDs!");
            Object[][] newData = new Object[goodIDs.size()][2];
            for (int x = 0; x < newData.length; x++) {
                newData[x][0] = goodIDs.get(x);
                newData[x][1] = Boolean.valueOf(false);
            }
            rowIDTableModel.appendRows(newData);
            rowIDTablePane.setViewportView(rowIDTable);
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        fileRemoveButton.setEnabled(fileList.getSelectedIndex() >= 0);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String textValue;

        if ((value instanceof File)) {
            textValue = ((File) value).getName();
        } else {
            textValue = value + "";
        }
        JLabel label = new JLabel(textValue);
        if (isSelected) {
            label.setBackground(list.getSelectionBackground());
            label.setForeground(list.getSelectionForeground());
        } else {
            label.setBackground(list.getBackground());
            label.setForeground(list.getForeground());
        }
        label.setEnabled(list.isEnabled());
        label.setFont(list.getFont());
        label.setOpaque(true);
        return label;
    }

    public boolean isOK() {
        return isOK;
    }

    public static void main(String[] args) {
        JDialog.setDefaultLookAndFeelDecorated(true);
        JFrame.setDefaultLookAndFeelDecorated(true);
        Utils.setLastDir(new File("z:\\soft stuff\\"));
        new SOFTParserOptionPanel().makeDialog();
    }
}
