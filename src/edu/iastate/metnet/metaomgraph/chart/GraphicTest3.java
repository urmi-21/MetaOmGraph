package edu.iastate.metnet.metaomgraph.chart;


//import javax.swing.*;
import org.jfree.chart.axis.ValueAxis;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class GraphicTest3 {
    public static void main(String[] args) {
        long time = System.currentTimeMillis();

        int width = 1024;
        int height = 800;
        double anchorx = 1111;
        double anchory =  500;

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        System.out.println(System.currentTimeMillis() - time); //consistently ~2 seconds

        Graphics2D graphics = img.createGraphics();
        graphics.setColor(Color.white);
        graphics.setFont(new Font("TimesRoman", Font.BOLD, 12));
        System.out.println("1 "+(System.currentTimeMillis() - time)); //consistently ~2 seconds


        Paint oldPaint = graphics.getPaint();
        AffineTransform at = graphics.getTransform();
        FontMetrics fontMetrics = graphics.getFontMetrics();
        FontRenderContext context = graphics.getFontRenderContext();
        Font font = graphics.getFont();

        time = System.currentTimeMillis();

        //graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, (float) .5));
        //AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f); // mhhur
        //graphics.setComposite(alpha);

        String strSampleText = "MetaOmGraph (http://metnetdb.org/MetaOmGraph/ ) is free, publicly-available software designed for analysis and visualization of high-throughput transcriptomic, proteomic, and metabolomic data.  The software allows evaluation and manipulation of data based on attributes such as expression values, metadata terms, and ontology annotations.  Co-expressed modules can be identified from among tens of thousands of biomolecules across thousands of samples. By reducing memory use, MetaOmGraph enables rapid analysis in";
        for(int i=0; i<1000; i++) {
            graphics.drawLine((int) 0, (int) 222, (int) 100, (int) 33);
            graphics.drawLine((int) 0, (int) 222, (int) 100, (int) 33);

            TextLayout layout = new TextLayout(strSampleText, font, context);

            double strWidth = fontMetrics.stringWidth(strSampleText);
            graphics.translate(anchorx + i, strWidth);

            graphics.rotate(-Math.PI / 2);

            //layout.draw(graphics, 0, 0); // removed by mhhur
            graphics.drawString(strSampleText, 0, 0);

            graphics.setTransform(at); //mhhur
        }
        graphics.setPaint(oldPaint);

        System.out.println("Final time: "+(System.currentTimeMillis() - time) + "ms"); //consistently ~2 seconds
    }
}