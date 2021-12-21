package edu.iastate.metnet.metaomgraph;

import com.formdev.flatlaf.FlatLightLaf;
import edu.iastate.metnet.metaomgraph.ui.VersionFrame.VersionController;
import org.intellij.lang.annotations.JdkConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MetaOmGraphLauncher implements ActionListener {

    // launcher variables
    private final String jarPath = "metaomgraph4.jar"; // replace with jar for current version of MOG
    private final String logoPath = "/resource/MetaOmicon.png";

    //JFrame
    private JFrame frame;
    private JLabel mogLogo;
    private JRadioButton defaultMemoryRadio;
    private JRadioButton extraMemoryRadio;
    private ButtonGroup memoryGroup;
    private JTextField minMemoryBox;
    private JTextField maxMemoryBox;
    private JScrollPane messagePane;
    private JTextArea messageLog;
    private JButton runButton;
    private JButton closeButton;
    private JLabel text;
    private JLabel errorMessage;

    //commands
    private final String EXTRA_MEMORY = "extra memory";
    private final String DEFAULT_MEMORY = "default memory";
    private final String RUN = "run";
    private final String SET_MIN_MEMORY = "set min memory";
    private final String SET_MAX_MEMORY = "set max memory";
    private final String EXIT = "exit";

    //launch variables
    private boolean extraMemory;
    private int minMemory;
    private int maxMemory;
    private boolean errorExists;

    //debugger log on/off
    private boolean loggerOn = false;

    private MetaOmGraphLauncher () {
        // build window
        frame = new JFrame("MetaOmGraph");
        frame.setLayout(new GridBagLayout());
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // gridbag formatting
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;

        // logo
        gbc.gridx = 0; // x pos
        gbc.gridy = 0; // y pos
        gbc.weighty = 0;
        gbc.weightx = 1;
        gbc.gridwidth = 3; // number of grids to fill horizontally
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,0,0,10);
        try {
            int width = 120;
            int height = 120;
            BufferedImage image = ImageIO.read(this.getClass().getResourceAsStream(logoPath));
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            mogLogo = new JLabel(new ImageIcon(scaledImage));
            mogLogo.setPreferredSize(new Dimension(width, height));
            frame.add(mogLogo, gbc);
            frame.setIconImage(image);
        } catch (IOException e) {
            e.printStackTrace();
            log(e.getMessage());
        }

        // Logo Text
        text = new JLabel("MetaOmGraph");
        text.setFont(new Font("Serif", Font.BOLD, 24));
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.gridx = 4;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,10,0,0);
        frame.add(text, gbc);

        // default memory radio button
        defaultMemoryRadio = new JRadioButton("Open with Default Memory");
        defaultMemoryRadio.setActionCommand(DEFAULT_MEMORY);
        defaultMemoryRadio.addActionListener(this);
        gbc.weighty = 0.2;
        gbc.weightx = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 0, 0);
        frame.add(defaultMemoryRadio, gbc);

        // extra memory radio button
        extraMemoryRadio = new JRadioButton("Open with Extra Memory");
        extraMemoryRadio.setActionCommand(EXTRA_MEMORY);
        extraMemoryRadio.addActionListener(this);
        gbc.weighty = 0.2;
        gbc.weightx = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 20, 0);
        frame.add(extraMemoryRadio, gbc);

        // group buttons
        memoryGroup = new ButtonGroup();
        memoryGroup.add(defaultMemoryRadio);
        memoryGroup.add(extraMemoryRadio);

        // min label
        text = new JLabel("Min:");
        gbc.weighty = 0.1;
        gbc.weightx = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(0,0,0,0);
        frame.add(text, gbc);
        // min memory
        minMemoryBox = new JTextField(5);
        minMemoryBox.setHorizontalAlignment(JTextField.CENTER);
        minMemoryBox.setActionCommand(SET_MIN_MEMORY);
        minMemoryBox.addActionListener(this);
        gbc.weightx = 0;
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.NORTH;
        frame.add(minMemoryBox, gbc);
        // min label GB
        text = new JLabel("GB");
        gbc.weightx = 1;
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        frame.add(text, gbc);

        // max label
        text = new JLabel("Max:");
        gbc.weighty = 0.1;
        gbc.weightx = 1;
        gbc.gridx = 4;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(0,0,0,0);
        frame.add(text, gbc);
        // max memory
        maxMemoryBox = new JTextField(5);
        maxMemoryBox.setHorizontalAlignment(JTextField.CENTER);
        maxMemoryBox.setActionCommand(SET_MAX_MEMORY);
        maxMemoryBox.addActionListener(this);
        gbc.weightx = 0;
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.NORTH;
        frame.add(maxMemoryBox, gbc);
        // max label GB
        text = new JLabel("GB");
        gbc.gridx++;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        frame.add(text, gbc);

        errorMessage = new JLabel(" ");
        errorMessage.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 7;
        gbc.weighty = 0.1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0,0,0,0);
        frame.add(errorMessage, gbc);

        // close program button
        closeButton = new JButton("Close");
        closeButton.setActionCommand(EXIT);
        closeButton.addActionListener(this);
        gbc.gridy++;
        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.weighty = 0.3;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(30,0,0,0);
        frame.add(closeButton, gbc);

        // run program button
        runButton = new JButton("Launch");
        runButton.setActionCommand(RUN);
        runButton.addActionListener(this);
        runButton.setBackground(Color.green);
        gbc.gridx = 5;
        gbc.gridwidth = 1;
        gbc.weighty = 0.3;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(30,15,0,0);
        frame.add(runButton, gbc);

        // message log
        if (loggerOn) {
            messageLog = new JTextArea("=== Message Log ===");
            messageLog.setRows(5);
            messagePane = new JScrollPane(messageLog);
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 7;
            gbc.weighty = 0;
            gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(0,0,0,0);
            frame.add(messagePane, gbc);
        }

        DocumentListener dlMin = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedMin();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedMin();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changedMin();
            }
        };
        DocumentListener dlMax = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedMax();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedMax();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changedMax();
            }
        };
        minMemoryBox.getDocument().addDocumentListener(dlMin);
        maxMemoryBox.getDocument().addDocumentListener(dlMax);

        // init launch variables
        initVariables();

        // open launcher
        init();
    }

    private void changedMin() {
        if (extraMemory && !minMemoryBox.getText().equals("")) {
            if(!minMemoryBox.getText().equals(Integer.toString(minMemory))) {
                setMinMemory();
            }
            else if (checkInputIsInt(minMemoryBox.getText())) {
                checkValues();
            }
        }
    }

    private void changedMax() {
        if (extraMemory && !maxMemoryBox.getText().equals("")) {
            if(!maxMemoryBox.getText().equals(Integer.toString(maxMemory))) {
                setMaxMemory();
            }
            else if (checkInputIsInt(maxMemoryBox.getText())) {
                checkValues();
            }
        }
    }

    private void initVariables() {
        extraMemory = false;
        minMemory = 2;
        maxMemory = 6;
        defaultMemoryRadio.setSelected(true);
        defaultMemory();
        voidErrors();
    }

    // start the program
    private void init() {
        frame.setSize(600, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        log(System.getProperty("java.version"));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String com = e.getActionCommand();
        switch (com) {
            case DEFAULT_MEMORY:
            {
                defaultMemory();
                break;
            }
            case EXTRA_MEMORY: {
                extraMemory();
                break;
            }
            case RUN:
            {
                if (VersionController.check()) break;
                runProgram();
                break;
            }
            case SET_MIN_MEMORY: {
                setMinMemory();
                break;
            }
            case SET_MAX_MEMORY: {
                setMaxMemory();
                break;
            }
            case EXIT: {
                exit();
            }
            default:
            {
                break;
            }
        }
    }

    private void defaultMemory() {
        extraMemory = false;
        minMemoryBox.setEnabled(false);
        maxMemoryBox.setEnabled(false);
        minMemory = 2;
        maxMemory = 6;
        minMemoryBox.setText(Integer.toString(minMemory));
        maxMemoryBox.setText(Integer.toString(maxMemory));
        log("extra heap memory: " + Boolean.toString(extraMemory));
        voidErrors();
    }

    private void extraMemory() {
        extraMemory = true;
        minMemoryBox.setEnabled(true);
        maxMemoryBox.setEnabled(true);
        minMemoryBox.setText(Integer.toString(minMemory));
        maxMemoryBox.setText(Integer.toString(maxMemory));
        log("extra heap memory: " + Boolean.toString(extraMemory));
    }

    // sets min memory
    // needs error checking - aorgler
    private void setMinMemory() {
        String input = minMemoryBox.getText();
        // wrong input checks:
        if (checkInputIsInt(input)) {
            int value = Integer.parseInt(input);
            minMemory = value;
            log("Set MIN: " + minMemory + "GB");
            checkValues();
        }
    }

    // sets max value
    private void setMaxMemory() {
        String input = maxMemoryBox.getText();
        if (checkInputIsInt(input)) {
            int value = Integer.parseInt(input);
            maxMemory = value;
            log("Set MAX: " + maxMemory + "GB");
            checkValues();
        }
    }

    private void checkValues() {
        if (maxMemory > 32 || maxMemory < 1) {
            logError("MAX value must be between 1-32GB!");
            return;
        } else if (maxMemory <= minMemory) {
            logError("MAX value cannot be lower than MIN!");
            return;
        } else if (minMemory > 31 || minMemory < 0) {
            logError("MIN value must be between 0-31GB!");
            return;
        } else if (minMemory >= maxMemory) {
            logError("MIN value cannot be higher than MAX!");
            return;
        } else {
            voidErrors();
        }
    }

    private boolean checkInputIsInt(String input) {
        for(char c : input.toCharArray()) {
            if (!Character.isDigit(c)) {
                logError("Input number values only!");
                return false;
            }
        }
        return true;
    }

    private void exit() {
        log("exiting...");
        System.exit(0);
    }

    // method which launches MOG
    private void runProgram() {
        log("Starting MetaOmGraph...");

        File file = null; // jar directory - for running program
        try {
            file = new File(MetaOmGraphLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            file = file.getParentFile(); // get parent directory
        } catch (URISyntaxException e) {
            e.printStackTrace();
            log(e.getMessage());
            return;
        }
//        try {
//            file = new File(".").getCanonicalFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//            log(e.getMessage());
//            return;
//        }
        // File file = new File(System.getProperty("user.dir")); // doesn't work on macOS???? -_-
        // File file = new File("target"); // jar directory - for testing jar in IDE
        String cmd = ""; // cmd for launching MOG

//        String os = System.getProperty("os.name"); // get operating system
//        if (os.toLowerCase().startsWith("windows")) { // check if windows
//            cmd += "cmd /c ";g
//        } else {
//            cmd += "sh -c ";
//        }
        cmd += "java ";

        // check for input errors before running
        if (errorExists) {
            log("Errors exist! Abandoned run!");
            return;
        }

        // add logic for adding flags here
        ArrayList<String> flags = new ArrayList<String>();
        if (extraMemory && !errorExists) {
            setMinMemory();
            String minHeap = "-Xms" + Integer.toString(minMemory) + "g";
            flags.add(minHeap);

            setMaxMemory();
            String maxHeap = "-Xmx" + Integer.toString(maxMemory) + "g";
            flags.add(maxHeap);
        }

        for (String f: flags) {
            cmd += f + " ";
        }
        cmd += "-jar " + jarPath; // add jar path to command
        try {
            // log for debugging
            log(cmd);
            log(file.getAbsolutePath());
            Process pr = Runtime.getRuntime().exec(cmd, null, file); // run command on command line in target dir
            InputStream in = pr.getInputStream();
            InputStream err = pr.getErrorStream();
            // printResults(pr); // print run results for debugging
            // System.exit(-1);

        } catch (IOException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    // log messages in launcher
    private void log(String message) {
        if (loggerOn) {
            this.messageLog.append("\n" + message);

            // scroll to the bottom of the pane
            messageLog.setCaretPosition(messageLog.getDocument().getLength());
        }
    }

    private void logError(String error) {
        log("ERROR! " + error);
        this.errorMessage.setText(error);
        this.errorExists = true;
    }

    private void voidErrors() {
        this.errorMessage.setText(" ");
        this.errorExists = false;
    }

    // main method
    public static void main(String[] args) {
        new MetaOmGraphLauncher();
    }

    // check if process if running correctly
    public void printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
