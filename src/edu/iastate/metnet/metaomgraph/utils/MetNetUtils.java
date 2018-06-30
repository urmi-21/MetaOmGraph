package edu.iastate.metnet.metaomgraph.utils;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.SwingWorker;
import edu.iastate.metnet.metaomgraph.ui.BlockingProgressDialog;
import edu.iastate.metnet.metaomgraph.ui.SimpleChipAnimationPanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.gjt.mm.mysql.Driver;

public class MetNetUtils {
    public MetNetUtils() {
    }

    public static String[][] getMetNetInfo(Object[] ids) {
        int i = 0;
        while (Utils.getIDType(ids[i] + "") == -1) {
            i++;
        }
        if (Utils.getIDType(ids[i] + "") == 1) {
            System.out.println("Getting metnet info from locus IDs: " + ids[i]);
            return getMetNetInfoFromLocus(ids);
        }
        System.out.println("Getting metnet info from probe IDs: " + ids[i]);
        return getMetNetInfoFromProbes(ids);
    }

    public static String[][] getMetNetInfoFromLocus(Object[] ids) {
        int NAME = 0;
        int REGULON = 1;
        int PATH = 2;
        String[][] result = new String[ids.length][3];
        Hashtable<String, String> pathhash = getPathwayInfoForLocus();
        Hashtable<String, String> namehash = getNameInfoForLocus();
        FlatFileConverter locusRegulonConvert = new FlatFileConverter(
                Utils.class.getResourceAsStream("/resource/misc/locusregulon.txt"),
                "\t");
        for (int i = 0; i < ids.length; i++) {
            result[i][0] = namehash.get(ids[i] + "".toUpperCase());
            result[i][2] = pathhash.get(ids[i] + "".toUpperCase());
            result[i][1] = locusRegulonConvert.convert(ids[i] + "");
        }
        return result;
    }

    public static Hashtable<String, String> getPathwayInfoForLocus() {
        Hashtable<String, String> result = new Hashtable();
        String sql = "select distinct block.name as locus, path.name as pathway from blockunit block, interactionparts ip, pathwayparts pp, blockunit path where path.type='pathway' and pp.blockid=path.blockid and ip.blockid=pp.part and block.blockid=ip.part and block.name like 'at_g_____'";


        final JDialog dialog = new JDialog(MetaOmGraph.getMainWindow(),
                "Querying MetNet database", true);
        try {
            Statement statement = connectToMetNetDB();
            SimpleChipAnimationPanel bg = new SimpleChipAnimationPanel();
            JLabel myLabel = new JLabel(
                    "<html><p align=\"center\">Querying MetNet database<br>Please be patient</p></html>");
            myLabel.setForeground(Color.BLACK);
            myLabel.setFont(myLabel.getFont().deriveFont(1));
            JPanel labelPanel = new JPanel() {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setComposite(AlphaComposite.getInstance(
                            10, 0.75F));
                    super.paintComponent(g2d);
                }

            };
            labelPanel.setBackground(Color.WHITE);
            labelPanel.add(myLabel);
            bg.add(labelPanel);
            dialog.getContentPane().add(bg, "Center");
            dialog.pack();
            dialog.setDefaultCloseOperation(0);
            dialog.setLocationRelativeTo(MetaOmGraph.getMainWindow());
            new Thread() {
                public void run() {
                    dialog.setVisible(true);
                }

            }.start();
            ResultSet rs = statement.executeQuery(sql);
            if (rs.first()) {
                while (!rs.isAfterLast()) {
                    String thisLocus = rs.getString("locus").toUpperCase();
                    String thisPath = rs.getString("pathway");
                    Utils.appendMapEntry(result, thisLocus, thisPath, "; ");
                    rs.next();
                }
            }
            dialog.dispose();
        } catch (SQLException e) {
            dialog.dispose();
            e.printStackTrace();
        }
        return result;
    }

    public static Hashtable<String, String> getNameInfoForLocus() {
        Hashtable<String, String> result = new Hashtable();
        String sql = "select distinct tg.locus_id as locus, tg.gene_name as name from tair_sequenced_genes tg where tg.locus_id like 'at_g_____' and tg.gene_name not like 'at_g_____%' union select distinct e.name,  es.synonym from entitysynonym es, entity e where es.entityid = e.entityid and e.name like 'at_g_____' and es.synonym not like 'at_g_____%'";


        try {
            Statement statement = connectToMetNetDB();
            JDialog dialog = new JDialog(MetaOmGraph.getMainWindow(),
                    "Querying MetNet database", false);
            SimpleChipAnimationPanel bg = new SimpleChipAnimationPanel();
            JLabel myLabel = new JLabel(
                    "<html><p align=\"center\">Querying MetNet database<br>Please be patient</p></html>");
            myLabel.setForeground(Color.BLACK);
            myLabel.setFont(myLabel.getFont().deriveFont(1));
            JPanel labelPanel = new JPanel() {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setComposite(AlphaComposite.getInstance(
                            10, 0.75F));
                    super.paintComponent(g2d);
                }

            };
            labelPanel.setBackground(Color.WHITE);
            labelPanel.add(myLabel);
            bg.add(labelPanel);
            dialog.getContentPane().add(bg, "Center");
            dialog.pack();
            dialog.setDefaultCloseOperation(0);
            dialog.setLocationRelativeTo(MetaOmGraph.getMainWindow());
            dialog.setVisible(true);
            dialog.paint(dialog.getGraphics());
            ResultSet rs = statement.executeQuery(sql);
            if (rs.first()) {
                while (!rs.isAfterLast()) {
                    String thisLocus = rs.getString("locus").toUpperCase();
                    String thisName = rs.getString("name");
                    Utils.appendMapEntry(result, thisLocus, thisName, "; ");
                    rs.next();
                }
            }
            dialog.dispose();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String[][] getMetNetInfoFromProbes(Object[] ids) {
        long startTime = java.util.Calendar.getInstance().getTimeInMillis();
        int LOCUSID = 0;
        int NAME = 1;
        int REGULON = 2;
        int PATH = 3;
        String[][] result = new String[ids.length][4];
        Class cl = Utils.class;
        FlatFileConverter affyLocusConvert = new FlatFileConverter(cl
                .getResourceAsStream("/resource/misc/affy25klocusconvert.txt"), " ");
        FlatFileConverter locusNameConvert = new FlatFileConverter(cl
                .getResourceAsStream("/resource/misc/locusnameconvert.txt"), " ");
        FlatFileConverter locusEntityConvert = new FlatFileConverter(cl
                .getResourceAsStream("/resource/misc/locusentityconvert.txt"), " ");
        FlatFileConverter locusRegulonConvert = new FlatFileConverter(cl
                .getResourceAsStream("/resource/misc/locusregulon.txt"), "\t");
        Hashtable<String, String> pathwayHash = getPathwayInfo();
        for (int x = 0; x < result.length; x++) {
            result[x][0] = affyLocusConvert.convert(ids[x].toString());
            result[x][1] = locusNameConvert.convert(result[x][0]);
            if (result[x][1] != null) {
                result[x][1] = result[x][1].replace("&beta;", "beta")
                        .replace("<i>", "").replace("</i>", "");
            }
            result[x][2] = locusRegulonConvert
                    .convert(result[x][0]);
            String entityID = locusEntityConvert.convert(result[x][0]);
            if ((entityID != null) && (!entityID.equals(""))) {
                if (entityID.indexOf(';') < 0) {
                    result[x][3] = pathwayHash.get(entityID);
                } else {
                    result[x][3] = "";
                    String[] entities = entityID.split("; ");
                    for (int i = 0; i < entities.length; i++) {
                        String thisPath = pathwayHash.get(entities[i]);
                        if (thisPath != null) {
                            if (result[x][3].equals("")) {
                                result[x][3] = thisPath;
                            } else {
                                String[] currentPaths = result[x][3]
                                        .split("; ");
                                String[] newPaths = thisPath.split("; ");
                                for (int j = 0; j < newPaths.length; j++) {
                                    if (Utils.linearSearch(currentPaths,
                                            newPaths[j]) < 0) {
                                        result[x][PATH] += "; " + newPaths[j];
                                    }
                                }
                            }
                        }
                    }
                }
            }


            if (result[x][0] == null)
                result[x][0] = "";
            if (result[x][1] == null)
                result[x][1] = "";
            if (result[x][3] == null)
                result[x][3] = "";
            if (result[x][2] == null) {
                result[x][2] = "";
            }
        }


        return result;
    }

    public static String[] getMetNetInfo(String searchMe) {
        String[][] result =
                getMetNetInfo(new String[]{searchMe});
        if ((result != null) && (result.length >= 1)) {
            return result[0];
        }
        return null;
    }


    public static Hashtable<String, String> getPathwayInfo() {
        String sql = "select entitywithcontext.entityid, pathwayblocks.name from blockunit as entityblocks left join entitywithcontext on entitywithcontext.blockid=entityblocks.blockid left join interactionparts on interactionparts.part=entitywithcontext.blockid left join pathwayparts on pathwayparts.part=interactionparts.blockid left join blockunit as pathwayblocks on pathwayblocks.blockid=pathwayparts.blockid where entityblocks.name like 'at_g_____' and pathwayblocks.name!='null' and pathwayblocks.name not like 'obsolete%' group by entitywithcontext.entityid, name union select entityid, pathway_name as name from tair_aracyc_dump left join blockunit on name=locus_id left join entitywithcontext on entitywithcontext.blockid=blockunit.blockid where locus_id!='unknown' and entityid!='null' group by entityid, pathway_name order by entityid";


        Hashtable<String, String> pathwayHash = new Hashtable();
        try {
            Statement statement = connectToMetNetDB();
            JDialog dialog = new JDialog(MetaOmGraph.getMainWindow(),
                    "Querying MetNet database", false);
            SimpleChipAnimationPanel bg = new SimpleChipAnimationPanel();
            JLabel myLabel = new JLabel(
                    "<html><p align=\"center\">Querying MetNet database<br>Please be patient</p></html>");
            myLabel.setForeground(Color.BLACK);
            myLabel.setFont(myLabel.getFont().deriveFont(1));
            JPanel labelPanel = new JPanel() {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setComposite(AlphaComposite.getInstance(
                            10, 0.75F));
                    super.paintComponent(g2d);
                }

            };
            labelPanel.setBackground(Color.WHITE);
            labelPanel.add(myLabel);
            bg.add(labelPanel);
            dialog.getContentPane().add(bg, "Center");
            dialog.pack();
            dialog.setDefaultCloseOperation(0);
            dialog.setLocationRelativeTo(MetaOmGraph.getMainWindow());
            dialog.setVisible(true);
            dialog.paint(dialog.getGraphics());
            ResultSet rs = statement.executeQuery(sql);
            if (rs.first()) {
                while (!rs.isAfterLast()) {
                    String thisEntity = rs.getString("entityid");
                    String thisPath = "<html>" + rs.getString("name") +
                            "</html>";
                    thisPath = thisPath.replace("&beta;", "beta").replace(
                            "<i>", "").replace("</i>", "");


                    if (pathwayHash.get(thisEntity) == null) {
                        pathwayHash.put(thisEntity, thisPath);
                    } else {
                        String currentPaths =
                                pathwayHash.get(thisEntity);
                        if ((!currentPaths.equals(thisPath)) &&
                                (!currentPaths.startsWith(thisPath + ";")) &&
                                (!currentPaths.endsWith("; " + thisPath)) &&
                                (!currentPaths.contains("; " + thisPath +
                                        ";"))) {
                            pathwayHash.put(thisEntity, currentPaths + "; " +
                                    thisPath);
                        }
                    }
                    rs.next();
                }
            }
            dialog.dispose();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return pathwayHash;
    }

    public static String getPathwayInfo(String entityID) {
        if ((entityID == null) || (entityID.equals("")))
            return null;
        String result = "(entityID=" + entityID + ")";

        try {
            Statement statement = connectToMetNetDB();
            String sql = "select blockid from entitywithcontext where entityid='" +
                    entityID + "'";
            ResultSet rs = statement.executeQuery(sql);
            if (rs.first()) {
                String blockIDQueryString = Utils.makeQueryString(rs,
                        "blockid", "blockid");

                sql = "select blockid from interactionparts " +
                        blockIDQueryString.replaceAll("blockid", "part");

                rs = statement.executeQuery(sql);
                if (rs.first()) {
                    String interactionQueryString = Utils.makeQueryString(rs,
                            "blockid", "part");
                    sql = "select blockid from pathwayparts " +
                            interactionQueryString;
                    rs = statement.executeQuery(sql);
                    if (rs.first()) {
                        String pathwayQueryString = Utils.makeQueryString(rs,
                                "blockid", "blockid");
                        sql = "select name from blockunit " +
                                pathwayQueryString;
                        rs = statement.executeQuery(sql);
                        if (rs.first()) {
                            String metnetPathways = "";
                            while (!rs.isAfterLast()) {
                                if (result.indexOf(rs.getString("name")) < 0) {
                                    if ((!result.equals("")) ||
                                            (!metnetPathways.equals("")))
                                        metnetPathways = metnetPathways + ";";
                                    metnetPathways = metnetPathways + rs.getString("name");
                                }
                                rs.next();
                            }
                            result = result + metnetPathways;
                        } else {
                            return "entityid=" + entityID;
                        }
                    } else {
                        return "entityid=" + entityID;
                    }
                } else {
                    return "entityid=" + entityID;
                }
            } else {
                return "entityid=" + entityID;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Statement connectToMetNetDB() throws SQLException {
        String connString = "jdbc:mysql://localhost/metnet2";
        try {
            Driver.class.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Connection connection = DriverManager.getConnection(connString,
                "guest", "");
        return connection.createStatement();
    }

    public static Statement connectToTestDB() throws SQLException {
        String connString = "jdbc:mysql://localhost/metnet2";
        try {
            Driver.class.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Connection connection = DriverManager.getConnection(connString,
                "guest", "");
        return connection.createStatement();
    }

    public static File downloadFile(final JFrame parent, URL downloadMe, File destDir) throws java.io.IOException {
        final URLConnection conn = downloadMe.openConnection();
        final int filesize = conn.getContentLength();
        final String fileName = downloadMe.getPath().substring(
                downloadMe.getPath().lastIndexOf('/') + 1).replaceAll("%20",
                " ");
        final BlockingProgressDialog progress = new BlockingProgressDialog(
                parent, "Downloading",
                "<html>" + fileName + "<br>0 KB of " + filesize / 1024 +
                        " KB complete<br>0 KB/sec</html>", 0L, filesize, true);
        progress.setSize(300, progress.getPreferredSize().height);
        progress.setLocationRelativeTo(parent);
        final File destFile = new File(destDir, fileName);
        System.out.println("Creating file: " + destFile.getAbsolutePath());
        destFile.createNewFile();
        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                    FileOutputStream out = new FileOutputStream(destFile);

                    long count = 0L;
                    System.out.println("Beginning download");
                    int i = 0;
                    byte[] bytesIn = new byte[1024];
                    int update = 0;
                    long startTime = System.currentTimeMillis();
                    while (((i = in.read(bytesIn)) >= 0) && (!
                            progress.isCanceled())) {
                        out.write(bytesIn, 0, i);
                        count += i;
                        update = (update + 1) % 100;
                        if (update == 0) {
                            double time = System.currentTimeMillis() -
                                    startTime;
                            time /= 1000.0D;
                            int speed = (int) (count / 1024L / time);
                            progress.setProgress(count);
                            progress.setMessage("<html>" + fileName + "<br>" +
                                    count / 1024L + " KB of " +
                                    filesize / 1024 + " KB complete<br>" +
                                    speed + " KB/sec</html>");
                        }
                    }

                    in.close();
                    out.close();
                } catch (Exception ioe) {
                    ioe.printStackTrace();
                    progress.dispose();

                    JOptionPane.showMessageDialog(
                            parent,
                            "Unable to download the file.  See console for details.",
                            "Error", 0);
                    return null;
                }
                progress.dispose();
                if (progress.isCanceled()) {
                    return null;
                }
                return destFile;
            }


        };
        worker.start();
        progress.setVisible(true);
        if (worker.get() == null) {
            return null;
        }
        return destFile;
    }

    public static Statement superConnect() throws SQLException {
        throw new SQLException("Whuh oh!");
    }


    public static Statement loginToDB()
            throws SQLException {
        return loginToDB("jdbc:mysql://localhost/metnet2");
    }

    public static Statement loginToTestDB() throws SQLException {
        return loginToDB("jdbc:mysql://localhost/metnet2");
    }

    public static Statement loginToDB(String connString) throws SQLException {
        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, nameField.getPreferredSize().height));
        JPasswordField passField = new JPasswordField();
        passField.setPreferredSize(new Dimension(200, passField.getPreferredSize().height));
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new java.awt.GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0D;
        c.weighty = 0.5D;
        c.anchor = 13;
        c.fill = 3;
        loginPanel.add(new JLabel("Username:"), c);
        c.gridy = 1;
        loginPanel.add(new JLabel("Password:"), c);
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = 17;
        c.fill = 2;
        loginPanel.add(nameField, c);
        c.gridy = 1;
        loginPanel.add(passField, c);
        int result = JOptionPane.showConfirmDialog(null, loginPanel, "Log in to MetNetDB", 2);
        if (result != 0) {
            return null;
        }
        try {
            Driver.class.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Connection connection = null;
        while ((connection == null) && (result == 0)) {
            try {
                connection = DriverManager.getConnection(connString, nameField.getText(), new String(passField.getPassword()));
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(null, "Invalid username/password", "Login error", 0);
                passField.setText("");
                result = JOptionPane.showConfirmDialog(null, loginPanel, "Log in to MetNetDB", 2);
            }
        }
        if (connection == null) {
            return null;
        }
        return connection.createStatement();
    }
}
