package edu.iastate.metnet.metaomgraph;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import javax.imageio.ImageIO;

public class ComponentToImage {
    public ComponentToImage() {
    }

    public static void saveAsPNG(Component c, File f, int width, int height) throws IOException {
        String ext = getExtension(f);


        if ((ext == null) || (!ImageIO.getImageWritersBySuffix(ext).hasNext())) throw new java.security.InvalidParameterException("No ImageWriter for the given extension!");
        BufferedImage image = new BufferedImage(width, height, 2);

        int oldHeight = c.getHeight();
        int oldWidth = c.getWidth();
        c.setSize(width, height);
        c.paint(image.getGraphics());
        ImageIO.write(image, ext, f);
        c.setSize(oldWidth, oldHeight);
    }

    public static void saveAsPNG(Component c, File f) throws IOException {
        if (c.isVisible())
            saveAsPNG(c, f, c.getWidth(), c.getHeight());
        else
            saveAsPNG(c, f, c.getPreferredSize().width, c.getPreferredSize().height);
    }


    public static void main(String[] args) {
        String[] writers = ImageIO.getWriterFormatNames();
        for (int x = 0; x < writers.length; System.out.println(writers[(x++)])) {
        }

        System.out.println("--------");
        for (Iterator iter = ImageIO.getImageWritersBySuffix("png"); iter
                .hasNext(); ) {
            System.out.println(iter.next());
        }
        System.out.println(ImageIO.getImageWritersBySuffix("wtf").hasNext());
        File dest = new File("z:\\output.png");
        javax.swing.JLabel label = new javax.swing.JLabel(new javax.swing.ImageIcon("z:\\logo_sm.gif"));

        label.setSize(400, 400);
        try {
            saveAsPNG(label, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getExtension(File f) {
        if (f != null) {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if ((i > 0) && (i < filename.length() - 1)) {
                return filename.substring(i + 1).toLowerCase();
            }
        }
        return null;
    }
}
