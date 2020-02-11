package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import com.l2fprod.common.swing.JDirectoryChooser;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmGraph.OpenProjectWorker;
import edu.iastate.metnet.metaomgraph.utils.MetNetUtils;
import edu.iastate.metnet.metaomgraph.utils.Utils;

public class WelcomePanel extends JPanel {

    /*
     * Creates the welcome panel with a set of buttons such as
     * 1. creating new projects
     * 2. metnet projects
     * 3. get information
     * 4. open existing projects
     * 5. exit metaomgraph
     *
     * The above mentioned buttons along with its sub elements are all docked in the screen using grid constraints
     *
     * @throws IOExeption
     *
     */
    public WelcomePanel() throws IOException {
//links and button creation for welcome panel
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JPanel newPanel = new JPanel();
        // newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));
        newPanel.setLayout(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 0;
        c2.gridy = 0;
        c2.weightx = .5;
        c2.weighty = .5;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.anchor = GridBagConstraints.NORTHWEST;
        ClickableLabel delimitedLabel, softLabel, aeLabel;
        delimitedLabel = new ClickableLabel("From a delimited text file");
        delimitedLabel
                .setActionCommand(MetaOmGraph.NEW_PROJECT_DELIMITED_COMMAND);
        delimitedLabel.addActionListener(MetaOmGraph.getInstance());
        softLabel = new ClickableLabel("From a SOFT file");
        softLabel.setActionCommand(MetaOmGraph.NEW_PROJECT_SOFT_COMMAND);
        softLabel.addActionListener(MetaOmGraph.getInstance());
        aeLabel = new ClickableLabel("From ArrayExpress");
        aeLabel.setActionCommand(MetaOmGraph.NEW_PROJECT_ARRAYEXPRESS_COMMAND);
        aeLabel.addActionListener(MetaOmGraph.getInstance());
        newPanel
                .add(
                        new WelcomeHeader(
                                "Create a New Project",
                                ImageIO
                                        .read(this
                                                .getClass()
                                                .getResourceAsStream(
                                                        "/resource/tango/32x32/actions/document-new.png"))),
                        c2);
        System.out.println(this.getClass().getResource("/resource/tango/32x32/actions/document-new.png"));
        c2.gridy++;
        c2.insets = new Insets(0, 10, 0, 0);
        newPanel.add(delimitedLabel, c2);
        c2.gridy++;
        //urmi remove unused
        //newPanel.add(softLabel, c2);
        c2.gridy++;
        c2.insets = new Insets(0, 10, 10, 0);
       //newPanel.add(aeLabel, c2);
        newPanel.setBorder(BorderFactory.createEtchedBorder());
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = .5;
        this.add(newPanel, c);
        JPanel openPanel = new JPanel();
        // openPanel.setLayout(new BoxLayout(openPanel, BoxLayout.Y_AXIS));
        openPanel.setLayout(new GridBagLayout());
        c2.gridy = 0;
        c2.insets = new Insets(0, 0, 0, 0);
        c2.anchor = GridBagConstraints.NORTHWEST;
        c2.weighty = .75;
        openPanel.add(new WelcomeHeader("Open an Existing Project", ImageIO
                        .read(this.getClass().getResourceAsStream(
                                "/resource/tango/32x32/actions/document-open.png"))),
                c2);
        Collection<File> recentProjects = MetaOmGraph.getRecentProjects();
        c2.insets = new Insets(0, 10, 0, 0);
        c2.weighty = .25;
        c2.anchor = GridBagConstraints.WEST;
        for (File thisProject : recentProjects) {
            ClickableLabel label = new ClickableLabel(Utils.compressPath(
                    thisProject.getAbsolutePath(), 50));
            label.setName(thisProject.getAbsolutePath());
            label.setActionCommand(MetaOmGraph.RECENT_PROJECT_COMMAND);
            label.addActionListener(MetaOmGraph.getInstance());
            c2.gridy++;
            openPanel.add(label, c2);
            JLabel statusLabel;
            if (!thisProject.exists()) {
                statusLabel = new JLabel(
                        "<html><font size=-2>Not found</font></html>");
            } else {
                Date date = new Date(thisProject.lastModified());
                statusLabel = new JLabel(
                        "<html><font size=-2>Last modified on "
                                + DateFormat.getDateTimeInstance(
                                DateFormat.LONG, DateFormat.SHORT)
                                .format(date) + "</font></html>");
            }
            statusLabel.setForeground(Color.DARK_GRAY);
            c2.gridy++;
            c2.insets = new Insets(0, 20, 10, 0);
            openPanel.add(statusLabel, c2);
            c2.insets = new Insets(0, 10, 0, 0);
        }
        if (recentProjects.size() <= 0) {
            JLabel label = new JLabel("No recent projects found");
            label.setForeground(Color.DARK_GRAY);
            c2.gridy++;
            openPanel.add(label, c2);
        }
        ImageIcon openIcon = new ImageIcon(ImageIO.read(this.getClass()
                .getResourceAsStream(
                        "/resource/tango/16x16/actions/document-open.png")));
        ClickableLabel openOtherLabel = new ClickableLabel(
                "Open another project", openIcon);
        openOtherLabel.setActionCommand(MetaOmGraph.OPEN_COMMAND);
        openOtherLabel.addActionListener(MetaOmGraph.getInstance());
        c2.gridy++;
        openPanel.add(new JLabel(" "), c2);
        c2.gridy++;
        c2.insets = new Insets(0, 10, 10, 0);
        openPanel.add(openOtherLabel, c2);
        openPanel.setBorder(BorderFactory.createEtchedBorder());
        c.gridy = 0;
        c.gridx = 1;
        c.gridheight = 4;
        this.add(openPanel, c);
        JPanel downloadPanel = new JPanel();
        downloadPanel.setLayout(new GridBagLayout());
        c2.gridy = 0;
        c2.insets = new Insets(0, 0, 0, 0);
        //urmi remove metnet panel
        //downloadPanel.add(new WelcomeHeader("MetNet Projects", ImageIO.read(this.getClass().getResourceAsStream("/resource/tango/32x32/actions/document-save.png"))),c2);
        /*try {
            long startTime = System.currentTimeMillis();
            Collection<DownloadableProject> projects = getDownloadableProjects();
//			System.out.println("Projects took "
//					+ (System.currentTimeMillis() - startTime) + "ms");
            ProjectDownloader downloader = new ProjectDownloader();
            for (DownloadableProject thisProject : projects) {
                ClickableLabel label = new ClickableLabel(thisProject.name);
                label.setActionCommand(thisProject.url);
                label.addActionListener(downloader);
                label.setEnabled(thisProject.downloadable);
                c2.gridy++;
                c2.insets = new Insets(0, 10, 0, 0);
                downloadPanel.add(label, c2);
                JLabel descrip = new JLabel(Utils.wrapText("<html>"
                        + thisProject.descrip + "</html>", 80, "<br>"));
                descrip.setFont(descrip.getFont().deriveFont((float) 10));
                c2.gridy++;
                c2.insets = new Insets(0, 15, 10, 0);
                downloadPanel.add(descrip, c2);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            c2.insets = new Insets(0, 10, 10, 0);
            downloadPanel
                    .add(new JLabel("Unable to retrieve project list"), c2);
        }
        downloadPanel.setBorder(BorderFactory.createEtchedBorder());
        c.gridy = 2;
        c.gridx = 0;
        c.gridheight = 1;
        this.add(downloadPanel, c);
        */
        JPanel helpPanel = new JPanel();
        helpPanel.setLayout(new GridBagLayout());
        c2.gridy = 0;
        c2.insets = new Insets(0, 0, 0, 0);
        helpPanel.add(new WelcomeHeader("Get Information", ImageIO.read(this
                .getClass().getResourceAsStream(
                        "/resource/tango/32x32/apps/help-browser.png"))), c2);
        c2.insets = new Insets(0, 10, 0, 0);
        c2.gridy++;
        ClickableLabel quickstartLabel = new ClickableLabel("Quick Start Guide");
        quickstartLabel.setActionCommand("quickstart.php");
        quickstartLabel.addActionListener(MetaOmGraph.getHelpListener());
        helpPanel.add(quickstartLabel, c2);
        c2.gridy++;
        c2.insets = new Insets(0, 10, 10, 0);
        ClickableLabel overviewLabel = new ClickableLabel("Overview");
        overviewLabel.setActionCommand("index.php");
        overviewLabel.addActionListener(MetaOmGraph.getHelpListener());
        helpPanel.add(overviewLabel, c2);
        helpPanel.setBorder(BorderFactory.createEtchedBorder());
        c.gridy = 3;
        c.gridx = 0;
        c.gridheight = 1;
        this.add(helpPanel, c);
        JButton exitButton = new JButton("Exit MetaOmGraph", new ImageIcon(this
                .getClass().getResource(
                        "/resource/tango/32x32/actions/system-log-out.png")));
        exitButton.setActionCommand(MetaOmGraph.QUIT_COMMAND);
        exitButton.addActionListener(MetaOmGraph.getInstance());
        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 2;
        this.add(exitButton, c);
        //setSize(500,500);
    }

    public void oldWelcomePanel() throws IOException {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JPanel newPanel = new JPanel();
        // newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));
        newPanel.setLayout(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 0;
        c2.gridy = 0;
        c2.weightx = 1;
        c2.weighty = .5;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.anchor = GridBagConstraints.WEST;
        ClickableLabel delimitedLabel, softLabel, aeLabel;
        delimitedLabel = new ClickableLabel("From a delimited text file");
        delimitedLabel
                .setActionCommand(MetaOmGraph.NEW_PROJECT_DELIMITED_COMMAND);
        delimitedLabel.addActionListener(MetaOmGraph.getInstance());
        softLabel = new ClickableLabel("From a SOFT file");
        softLabel.setActionCommand(MetaOmGraph.NEW_PROJECT_SOFT_COMMAND);
        softLabel.addActionListener(MetaOmGraph.getInstance());
        aeLabel = new ClickableLabel("From ArrayExpress");
        aeLabel.setActionCommand(MetaOmGraph.NEW_PROJECT_ARRAYEXPRESS_COMMAND);
        aeLabel.addActionListener(MetaOmGraph.getInstance());
        newPanel
                .add(
                        new WelcomeHeader(
                                "Create a New Project",
                                ImageIO
                                        .read(this
                                                .getClass()
                                                .getResourceAsStream(
                                                        "/resource/tango/32x32/actions/document-new.png"))),
                        c2);
        c2.gridy++;
        c2.insets = new Insets(0, 10, 0, 0);
        newPanel.add(delimitedLabel, c2);
        c2.gridy++;
        //newPanel.add(softLabel, c2);
        c2.gridy++;
        c2.insets = new Insets(0, 10, 10, 0);
        //newPanel.add(aeLabel, c2);
        // IconBorder newIconBorder = new IconBorder(new
        // ImageIcon(this.getClass().getResource(
        // "/resource/tango/32x32/actions/document-new.png")),
        // SwingConstants.NORTH_WEST);
        // // newPanel.setBorder(BorderFactory.createTitledBorder("New"));
        // Border titledBorder = BorderFactory.createTitledBorder(newIconBorder,
        // "Create New Project");
        // newPanel.setBorder(BorderFactory.createCompoundBorder(titledBorder,
        // BorderFactory
        // .createEtchedBorder()));
        newPanel.setBorder(BorderFactory.createEtchedBorder());
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = .5;
        this.add(newPanel, c);
        JPanel openPanel = new JPanel();
        // openPanel.setLayout(new BoxLayout(openPanel, BoxLayout.Y_AXIS));
        openPanel.setLayout(new GridBagLayout());
        c2.gridy = 0;
        c2.insets = new Insets(0, 0, 0, 0);
        openPanel.add(new WelcomeHeader("Open an Existing Project", ImageIO
                        .read(this.getClass().getResourceAsStream(
                                "/resource/tango/32x32/actions/document-open.png"))),
                c2);
        Collection<File> recentProjects = MetaOmGraph.getRecentProjects();
        c2.insets = new Insets(0, 10, 0, 0);
        for (File thisProject : recentProjects) {
            ClickableLabel label = new ClickableLabel(Utils.compressPath(
                    thisProject.getAbsolutePath(), 50));
            label.setName(thisProject.getAbsolutePath());
            label.setActionCommand(MetaOmGraph.RECENT_PROJECT_COMMAND);
            label.addActionListener(MetaOmGraph.getInstance());
            c2.gridy++;
            openPanel.add(label, c2);
        }
        if (recentProjects.size() <= 0) {
            JLabel label = new JLabel("No recent projects found");
            label.setForeground(Color.DARK_GRAY);
            c2.gridy++;
            openPanel.add(label, c2);
        }
        ClickableLabel openOtherLabel = new ClickableLabel(
                "Open another project");
        openOtherLabel.setActionCommand(MetaOmGraph.OPEN_COMMAND);
        openOtherLabel.addActionListener(MetaOmGraph.getInstance());
        c2.gridy++;
        openPanel.add(new JLabel(" "), c2);
        c2.gridy++;
        c2.insets = new Insets(0, 10, 10, 0);
        openPanel.add(openOtherLabel, c2);
        openPanel.setBorder(BorderFactory.createEtchedBorder());
        c.gridy++;
        this.add(openPanel, c);
        JPanel downloadPanel = new JPanel();
        downloadPanel.setLayout(new GridBagLayout());
        c2.gridy = 0;
        c2.insets = new Insets(0, 0, 0, 0);
        downloadPanel.add(new WelcomeHeader("MetNet Projects", ImageIO
                        .read(this.getClass().getResourceAsStream(
                                "/resource/tango/32x32/actions/document-save.png"))),
                c2);
        try {
            long startTime = System.currentTimeMillis();
            Collection<DownloadableProject> projects = getDownloadableProjects();
//			System.out.println("Projects took "
//					+ (System.currentTimeMillis() - startTime) + "ms");
            ProjectDownloader downloader = new ProjectDownloader();
            for (DownloadableProject thisProject : projects) {
                ClickableLabel label = new ClickableLabel(thisProject.name);
                label.setActionCommand(thisProject.url);
                label.addActionListener(downloader);
                label.setEnabled(thisProject.downloadable);
                c2.gridy++;
                c2.insets = new Insets(0, 10, 0, 0);
                downloadPanel.add(label, c2);
                JLabel descrip = new JLabel(Utils.wrapText("<html>"
                        + thisProject.descrip + "</html>", 80, "<br>"));
                descrip.setFont(descrip.getFont().deriveFont((float) 10));
                c2.gridy++;
                c2.insets = new Insets(0, 15, 10, 0);
                downloadPanel.add(descrip, c2);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            c2.insets = new Insets(0, 10, 10, 0);
            downloadPanel
                    .add(new JLabel("Unable to retrieve project list"), c2);
        }
        c.gridy++;
        this.add(downloadPanel, c);
        JPanel helpPanel = new JPanel();
        helpPanel.setLayout(new GridBagLayout());
        c2.gridy = 0;
        c2.insets = new Insets(0, 0, 0, 0);
        helpPanel.add(new WelcomeHeader("Get Information", ImageIO.read(this
                .getClass().getResourceAsStream(
                        "/resource/tango/32x32/apps/help-browser.png"))), c2);
        c2.insets = new Insets(0, 10, 0, 0);
        c2.gridy++;
        ClickableLabel quickstartLabel = new ClickableLabel("Quick Start Guide");
        quickstartLabel.setActionCommand("quickstart.php");
        quickstartLabel.addActionListener(MetaOmGraph.getHelpListener());
        helpPanel.add(quickstartLabel, c2);
        c2.gridy++;
        c2.insets = new Insets(0, 10, 10, 0);
        ClickableLabel overviewLabel = new ClickableLabel("Overview");
        overviewLabel.setActionCommand("index.php");
        overviewLabel.addActionListener(MetaOmGraph.getHelpListener());
        helpPanel.add(overviewLabel, c2);
        helpPanel.setBorder(BorderFactory.createEtchedBorder());
        c.gridy++;
        this.add(helpPanel, c);
        JButton exitButton = new JButton("Exit MetaOmGraph", new ImageIcon(this
                .getClass().getResource(
                        "/resource/tango/32x32/actions/system-log-out.png")));
        exitButton.setActionCommand(MetaOmGraph.QUIT_COMMAND);
        exitButton.addActionListener(MetaOmGraph.getInstance());
        c.gridy++;
        this.add(exitButton, c);
    }

    private Collection<DownloadableProject> getDownloadableProjects()
            throws IOException {
        ArrayList<DownloadableProject> result = new ArrayList<DownloadableProject>();
        URL website;
        BufferedReader in;
        try {
            website = new URL(
                    "http://metnetweb.gdcb.iastate.edu/MetaOmGraph/projectlist.txt");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        try {
            in = new BufferedReader(new InputStreamReader(website.openStream()));
        } catch (Exception e) {
            DownloadableProject errorProject = new DownloadableProject();
            errorProject.name = "Unable to connect to MetNetDB";
            errorProject.descrip = "";
            errorProject.downloadable = false;
            result.add(errorProject);
            return result;
        }
        String thisLine;
        while ((thisLine = in.readLine()) != null) {
            DownloadableProject thisProject = new DownloadableProject();
            thisProject.name = thisLine;
            thisProject.url = in.readLine();
            thisProject.descrip = in.readLine();
            thisProject.downloadable = true;
            result.add(thisProject);
        }
        return result;
    }

    private static class DownloadableProject {
        String name;
        String descrip;
        String url;
        boolean downloadable;
    }

    private static class ProjectDownloader implements ActionListener {

        @Override
		public void actionPerformed(ActionEvent e) {
            URL site;
            try {
                site = new URL(e.getActionCommand());
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
                return;
            }
            // JFileChooser chooser = new JFileChooser(Utils.getLastDir());
            JDirectoryChooser chooser = new JDirectoryChooser(Utils
                    .getLastDir());
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            final String fileName = e.getActionCommand().substring(
                    e.getActionCommand().lastIndexOf("/") + 1).replaceAll(
                    "%20", " ");
            FileFilter filter = new FileFilter() {

                @Override
                public boolean accept(File f) {
                    return f.getName().equals(fileName) || f.isDirectory();
                }

                @Override
                public String getDescription() {
                    return fileName;
                }

            };
            chooser.setFileFilter(filter);
            int result = chooser.showDialog(MetaOmGraph.getMainWindow(),
                    "Download");
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File dest = chooser.getSelectedFile();
            Utils.setLastDir(dest);
            try {
                File zipFile = MetNetUtils.downloadFile(MetaOmGraph
                        .getMainWindow(), site, dest);
                File unzipDest = new File(dest, Utils.removeExtension(fileName));
                unzipDest.mkdir();
                if (!unzipDest.exists()) {
                    System.err.println("Doesn't exist");
                    return;
                }
                if (!unzipDest.isDirectory()) {
                    System.err.println("Not a dir");
                }
                File[] unzippedFiles = Utils.unzip(zipFile, unzipDest);
                zipFile.delete();
                boolean found = false;
                int i = 0;
                while (i < unzippedFiles.length && !found) {
                    if (Utils.getExtension(unzippedFiles[i]).equals("mog")) {
                        found = true;
                        new OpenProjectWorker(unzippedFiles[i]).start();
                    }
                    i++;
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        MetaOmGraph.init();
        JDialog dialog = new JDialog(MetaOmGraph.getMainWindow(),
                "Welcome to MetaOmGraph", true);
        dialog.getContentPane().add(new WelcomePanel());
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(MetaOmGraph.getMainWindow());
        dialog.setVisible(true);
    }

}
