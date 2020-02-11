package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;


public class MetNetLAF {
    public static final ColorUIResource MNYellow = new ColorUIResource(255,255, 153);

    public static final ColorUIResource MNGreen = new ColorUIResource(0, 184, 2);

    public static final ColorUIResource MNDarkGreen = new ColorUIResource(0, 102, 0);

    public static final ColorUIResource MNBlue = new ColorUIResource(109, 217, 217);

    public static final ColorUIResource greenGradLight = new ColorUIResource(243, 249, 242);

    public static final ColorUIResource greenGradDark = new ColorUIResource(103, 136, 95);

    public static final ColorUIResource greenGradMiddle = new ColorUIResource(153, 203, 142);

    public static final ColorUIResource alternateRowColor = new ColorUIResource(216, 236, 213);


    public MetNetLAF() {
    }


    private static void initMetalTheme(UIDefaults def) {
    }


    private static class HorizontalSliderThumbIcon implements Icon, Serializable {
        public HorizontalSliderThumbIcon() {
        }


        @Override
		public int getIconWidth() {
            return 15;
        }


        @Override
		public int getIconHeight() {
            return 16;
        }


        @Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
            boolean enabled = false;
            boolean focus = false;
            if (c != null) {
                enabled = c.isEnabled();
                focus = c.hasFocus();
            }


            Polygon outline = new Polygon();
            outline.addPoint(x + 1, y);
            outline.addPoint(x + 13, y);
            outline.addPoint(x + 14, y + 1);
            outline.addPoint(x + 14, y + 8);
            outline.addPoint(x + 7, y + 15);
            outline.addPoint(x + 6, y + 14);
            outline.addPoint(x, y + 8);
            outline.addPoint(x, y + 1);


            Graphics2D g2d = (Graphics2D) g.create();
            if (enabled) {
                GradientPaint greenGradPaint = new GradientPaint(1.0F, y + 1, MetNetLAF.greenGradLight, 1.0F, y + 14, MetNetLAF.greenGradDark);
                g2d.setPaint(greenGradPaint);
            } else {
                g2d.setColor(MetNetLAF.MNYellow);
            }
            g2d.fillPolygon(outline);


            if (enabled) {
                g2d.setColor(MetalLookAndFeel.getBlack());
            } else
                g2d.setColor(MetalLookAndFeel.getControlDisabled());
            g2d.drawPolygon(outline);


            if (c.isEnabled()) {
                if (focus) {
                    g.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
                } else
                    g.setColor(MetalLookAndFeel.getBlack());
                g.drawLine(x + 3, y + 3, x + 3, y + 3);
                g.drawLine(x + 7, y + 3, x + 7, y + 3);
                g.drawLine(x + 11, y + 3, x + 11, y + 3);

                g.drawLine(x + 5, y + 5, x + 5, y + 5);
                g.drawLine(x + 9, y + 5, x + 9, y + 5);

                g.drawLine(x + 3, y + 7, x + 3, y + 7);
                g.drawLine(x + 7, y + 7, x + 7, y + 7);
                g.drawLine(x + 11, y + 7, x + 11, y + 7);


                g.setColor(MetalLookAndFeel.getWhite());
                if (focus) {
                    g.drawLine(x + 1, y + 1, x + 13, y + 1);
                    g.drawLine(x + 1, y + 2, x + 1, y + 8);
                }

                g.drawLine(x + 2, y + 2, x + 2, y + 2);
                g.drawLine(x + 6, y + 2, x + 6, y + 2);
                g.drawLine(x + 10, y + 2, x + 10, y + 2);

                g.drawLine(x + 4, y + 4, x + 4, y + 4);
                g.drawLine(x + 8, y + 4, x + 8, y + 4);

                g.drawLine(x + 2, y + 6, x + 2, y + 6);
                g.drawLine(x + 6, y + 6, x + 6, y + 6);
                g.drawLine(x + 10, y + 6, x + 10, y + 6);
            }
        }
    }
}
