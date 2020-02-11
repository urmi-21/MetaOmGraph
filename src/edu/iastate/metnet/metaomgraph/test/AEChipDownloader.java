package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.metaomgraph.SwingWorker;
import edu.iastate.metnet.metaomgraph.ui.LongProgressBar;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


public class AEChipDownloader {
    public static final String HGU133A_ARRAY_NAME = "Homo Sapiens (HG-U133A)";
    public static final String MOUSE_4302_ARRAY_NAME = "Mus Musculus (430 2.0)";
    public static final String ATH1_ARRAY_NAME = "Arabidopsis Thaliana (ATH1)";
    public static final String SOYBEAN_ARRAY_NAME = "Soybean Genome Array";
    public static final String RAT_230_ARRAY_NAME = "Rat Genome (230 2.0)";
    public static final String YEAST_ARRAY_NAME = "Yeast Genome (S98)";
    public static final String RICE_ARRAY_NAME = "Rice Genome Array";
    public static final String BARLEY_ARRAY_NAME = "Barley Genome Array";
    public static final String HGU133PLUS2_ARRAY_NAME = "Homo Sapiens (HG-U133 Plus 2.0)";
    private static boolean isCanceled;
    private static ArrayList<Thread> threads;
    private static int threadIndex;
    private static ExperimentFiles[] files;
    private static JProgressBar[] progs;
    private static File destDir;

    public AEChipDownloader() {
    }

    public static void main(String[] args)
            throws JDOMException, IOException {
        isCanceled = false;

        Vector<OptionObject> arrayList = new Vector();
        arrayList.add(new OptionObject("Arabidopsis Thaliana (ATH1)", "13851999"));
        arrayList.add(new OptionObject("Barley Genome Array", "287590865"));
        arrayList.add(new OptionObject("Homo Sapiens (HG-U133A)", "302382080"));
        arrayList.add(new OptionObject("Homo Sapiens (HG-U133 Plus 2.0)", "405156763"));
        arrayList.add(new OptionObject("Mus Musculus (430 2.0)", "417942717&species=mus musculus"));
        arrayList.add(new OptionObject("Rat Genome (230 2.0)", "383811809"));
        arrayList.add(new OptionObject("Rice Genome Array", "1656801082"));
        arrayList.add(new OptionObject("Soybean Genome Array", "1134087197"));
        arrayList.add(new OptionObject("Yeast Genome (S98)", "119528504"));
        DefaultComboBoxModel arrayModel = new DefaultComboBoxModel(arrayList);
        JComboBox arrayBox = new JComboBox(arrayModel);
        JPanel arrayPanel = new JPanel();
        arrayPanel.add(new JLabel("Array: "));
        arrayPanel.add(arrayBox);

        SpinnerNumberModel spinModel = new SpinnerNumberModel(5, 1, 100, 1);
        JSpinner spinner = new JSpinner(spinModel);
        JPanel spinPanel = new JPanel();
        spinPanel.add(new JLabel("Concurrent downloads: "));
        spinPanel.add(spinner);

        final JDialog dialog = new JDialog((Frame) null, "Download All Experiments", true);
        JButton okButton = new JButton(new AbstractAction("OK") {
            @Override
			public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }

        });
        JButton cancelButton = new JButton(new AbstractAction("Cancel") {
            @Override
			public void actionPerformed(ActionEvent e) {
                AEChipDownloader.isCanceled = true;
                dialog.dispose();
            }

        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), 1));
        dialog.getContentPane().add(arrayPanel);
        dialog.getContentPane().add(spinPanel);
        dialog.getContentPane().add(buttonPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        if (isCanceled) {
            System.out.println("Canceled");
            System.exit(0);
        }
        System.out.println("Array: " + arrayModel.getSelectedItem());
        System.out.println("Downloads: " + spinner.getValue());
        String arrayID = ((OptionObject) arrayModel.getSelectedItem()).value;
        URL fileURL = null;
        try {
            fileURL = new URL("http://www.ebi.ac.uk/microarray-as/ae/xml/files?array=" + arrayID);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            System.exit(-1);
        }
        System.out.println("Retrieving experiments");
        Document expDoc = new SAXBuilder().build(fileURL.openStream());
        List exps = expDoc.getRootElement().getChildren("experiment");
        System.out.println("Building files");
        files = new ExperimentFiles[exps.size()];
        progs = new LongProgressBar[files.length];
        final JDialog dialog2 = new JDialog((Frame) null, "Downloading", false);
        JPanel allProgs = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.weighty = 0.5D;
        c.insets = new Insets(0, 10, 0, 10);

        int index = 0;
        for (Object o : exps) {
            Element e = (Element) o;
            files[index] = new ExperimentFiles(e);
            progs[index] = new LongProgressBar(0L, 1000L);


            c.gridx = 0;
            c.fill = 0;
            c.anchor = 13;
            c.weightx = 0.0D;
            allProgs.add(new JLabel(files[index].expID), c);
            c.gridx = 1;
            c.fill = 2;
            c.weightx = 1.0D;
            allProgs.add(progs[index], c);
            c.gridy += 1;
            index++;
        }
        dialog2.getContentPane().add(new JScrollPane(allProgs));
        dialog2.setSize(800, 600);
        dialog2.setLocationRelativeTo(null);
        destDir = new File("/Users/mhhur/Desktop/multi download test/");

        threadIndex = 0;
        for (int i = 0; i < ((Integer) spinModel.getValue()).intValue(); i++) {
            ExperimentFiles f = files[threadIndex];
            new DownloadThread(f, new File(destDir, f.expID + ".zip"),
                    progs[threadIndex]).start();
            threadIndex += 1;
            if (threadIndex < files.length) {
                files[threadIndex].prep();
            }
        }

        dialog2.setVisible(true);
    }

    private static synchronized void threadFinished() {
        if (threadIndex >= files.length) {
            System.out.println("No files left to download");
            return;
        }
        ExperimentFiles f = files[threadIndex];
        System.out.println("Launching new thread: " + f.expID);
        new DownloadThread(f, new File(destDir, f.expID + ".zip"),
                progs[threadIndex]).start();
        threadIndex += 1;
        if (threadIndex < files.length) {
            files[threadIndex].prep();
        }
    }

    private static class DownloadThread extends SwingWorker {
        AEChipDownloader.ExperimentFiles myFile;
        File dest;
        JProgressBar prog;

        public DownloadThread(AEChipDownloader.ExperimentFiles f, File destination, JProgressBar prog) {
            myFile = f;
            dest = destination;
            this.prog = prog;
        }

        @Override
		public Object construct() {
            try {
                myFile.getProcessedData(dest, prog);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
		public void finished() {
            threadFinished();
            super.finished();
        }
    }

    private static class ExperimentFiles {
        boolean done;
        URL expFile;
        URL sdrfFile;
        String expID;
        URLConnection conn;

        public ExperimentFiles(Element exp) {
            done = false;
            expID = exp.getChildText("accession");
            List children = exp.getChildren("file");
            for (Object o : children) {
                Element file = (Element) o;
                String kind = file.getChildText("kind");
                URL loc = null;
                try {
                    loc = new URL(file.getChildText("url"));
                } catch (MalformedURLException localMalformedURLException) {
                }
                if ("sdrf".equals(kind)) {
                    if (sdrfFile != null) {
                        System.out.println("Multiple SDRF files for " + expID);
                    }
                    sdrfFile = loc;
                }
                if ("fgem".equals(kind)) {
                    if (expFile != null) {
                        System.out.println("Multiple FGEM files for " + expID);
                    }
                    expFile = loc;
                }
            }
        }

        public synchronized void prep() {
            if (expFile == null) {
                System.err.println("No processed data for " + expID);
                return;
            }
            System.out.println("Prepping " + expID);
            try {
                conn = expFile.openConnection();
            } catch (IOException e) {
                conn = null;
                e.printStackTrace();
            }
        }

        public synchronized File getProcessedData(File dest, JProgressBar progress) throws IOException {
            if (conn == null) {
                prep();
            }
            if (conn == null) {
                System.err.println("Unable to open connection for " + expID);
                return null;
            }
            System.out.println("Beginning download: " + expID);
            try {
                int size = conn.getContentLength();
                if (size <= 0) {
                    System.out.println(expID + ": 0-length content for " +
                            expFile.toExternalForm());
                    return null;
                }
                System.out.println(expID + ": " + size);
                progress.setMinimum(0);
                progress.setMaximum(size);
                BufferedInputStream in = new BufferedInputStream(conn
                        .getInputStream());
                FileOutputStream out = new FileOutputStream(dest);

                long count = 0L;

                int i = 0;
                byte[] bytesIn = new byte[1024];
                int update = 0;
                long startTime = System.currentTimeMillis();
                while ((i = in.read(bytesIn)) >= 0) {
                    out.write(bytesIn, 0, i);
                    count += i;
                    update = (update + 1) % 100;
                    if (update == 0) {
                        double time = System.currentTimeMillis() - startTime;
                        time /= 1000.0D;
                        int speed = (int) (count / 1024L / time);
                        progress.setValue((int) (count / bytesIn.length));
                    }
                }
                long endTime = Calendar.getInstance().getTimeInMillis();
                long timeTaken = endTime - startTime;
                double speed = size / 1024.0D / (
                        timeTaken / 1000.0D);
                progress.setValue(size);
                System.out.println("Downloaded " + count + " bytes in " +
                        timeTaken + "ms (" + speed + " KB/sec)");
                in.close();
                out.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return dest;
        }
    }

    private static class OptionObject {
        String name;
        String value;

        public OptionObject(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
		public String toString() {
            return name;
        }

        @Override
		public boolean equals(Object obj) {
            return name.equals(obj);
        }
    }
}
