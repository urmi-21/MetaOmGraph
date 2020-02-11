package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MetaOmIconPanel extends JPanel {
    private int circlewidth = 10;

    private int circlegap = 5;

    private int linewidth = 15;
    private String name;
    private String fontName;
    private int fontSize;

    public MetaOmIconPanel() {
        this(null, null, 0);
    }

    public MetaOmIconPanel(String name, String fontName, int fontSize) {
        this.name = name;
        this.fontName = fontName;
        this.fontSize = fontSize;
    }

    private void drawDots(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        for (int x = 0; x < getWidth(); x = x + circlegap + circlewidth) {
            for (int y = 0; y < getHeight(); y = y + circlegap + circlewidth) {
                double colorValue = Math.random();
                int green;
                int red;
                if (colorValue <= 0.5D) {
                    red = 255;
                    green = (int) (colorValue / 0.5D * 255.0D);
                } else {
                    red = (int) ((1.0D - colorValue) / 0.5D * 255.0D);
                    green = 255;
                }
                g2d.setColor(new Color(red, green, 0));
                g2d.fillOval(x + circlegap, y + circlegap, circlewidth,
                        circlewidth);
            }
        }
    }

    @Override
	protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        drawDots(g2d);
        GradientPaint gradpaint = new GradientPaint(0.0F, 0.0F, new Color(255, 255,
                255, 0), (int) (1.1D * getWidth()), 0.0F, Color.WHITE);
        g2d.setPaint(gradpaint);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        int x1 = 0;
        int y1 = getHeight() - linewidth / 2;
        int x2 = getWidth() / 3;
        int y2 = getHeight() / 3;
        int x3 = 2 * getWidth() / 3;
        int y3 = 2 * getHeight() / 3;
        int x4 = getWidth();
        int y4 = 10;
        gradpaint = new GradientPaint(0.0F, 0.0F, new Color(255, 0, 0, 0),
                getWidth(), 0.0F, Color.RED);
        g2d.setPaint(gradpaint);
        g2d.setStroke(new BasicStroke(linewidth));
        GeneralPath line = new GeneralPath(0, 3);
        line.moveTo(x1, y1);
        line.lineTo(x2, y2);
        line.lineTo(x3, y3);
        line.lineTo(x4, y4);
        g2d.draw(line);
        y1 += y4;
        y4 = y1 - y4;
        y1 -= y4;
        y2 += y3;
        y3 = y2 - y3;
        y2 -= y3;
        line = new GeneralPath(0, 3);
        line.moveTo(x1, y1);
        line.lineTo(x2, y2);
        line.lineTo(x3, y3);
        line.lineTo(x4, y4);
        gradpaint = new GradientPaint(0.0F, 0.0F, new Color(0, 0, 255, 0),
                getWidth(), 0.0F, Color.BLUE);
        g2d.setPaint(gradpaint);
        g2d.draw(line);


        g2d.setColor(Color.WHITE);
        Font fillFont = Font.decode(fontName + "-" + fontSize);
        Font outlineFont = fillFont.deriveFont((float) (1.125D * fontSize));
        FontRenderContext frc = g2d.getFontRenderContext();
        if (name != null) {
            TextLayout layout = new TextLayout(name, fillFont, frc);
            int width = SwingUtilities.computeStringWidth(
                    g2d.getFontMetrics(fillFont), name);

            g2d.setFont(fillFont);
            g2d.setColor(Color.BLACK);
            AffineTransform transform = new AffineTransform();
            Shape outline = layout.getOutline(null);
            Rectangle outlineBounds = outline.getBounds();
            transform = g2d.getTransform();
            transform.translate(getWidth() / 2 - (outlineBounds.width / 2),
                    getHeight() / 1.5 + (outlineBounds.height / 2));
            g2d.transform(transform);
            g2d.setStroke(new BasicStroke(6.0F));
            g2d.draw(outline);
            g2d.setFont(fillFont);
            g2d.transform(new AffineTransform());
            g2d.setColor(Color.WHITE);

            layout.draw(g2d, 0.0F, 0.0F);
        }
    }


    protected void paintComponent2(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        for (int x = 0; x < getWidth(); x = x + circlegap + circlewidth) {
            for (int y = 0; y < getHeight(); y = y + circlegap + circlewidth) {
                double colorValue = Math.random();
                int green;
                int red;
                if (colorValue <= 0.5D) {
                    red = 255;
                    green = (int) (colorValue / 0.5D * 255.0D);
                } else {
                    red = (int) ((1.0D - colorValue) / 0.5D * 255.0D);
                    green = 255;
                }
                g2d.setColor(new Color(red, green, 0));
                g2d.fillOval(x + circlegap, y + circlegap, circlewidth,
                        circlewidth);
            }
        }
        GradientPaint gradpaint = new GradientPaint(0.0F, 0.0F, new Color(255, 255,
                255, 0), 4 * getWidth() / 5, 0.0F, Color.WHITE);
        g2d.setPaint(gradpaint);
        g2d.fillRect(0, 0, getWidth(), getHeight());


        int x1 = 0;
        int y1 = getHeight() - linewidth / 2;
        int x2 = getWidth() / 3;
        int y2 = getHeight() / 3;
        int x3 = 2 * getWidth() / 3;
        int y3 = 2 * getHeight() / 3;
        int x4 = getWidth();
        int y4 = 10;
        gradpaint = new GradientPaint(0.0F, 0.0F, new Color(255, 0, 0, 0),
                getWidth(), 0.0F, Color.RED);
        g2d.setPaint(gradpaint);
        Polygon poly = new Polygon();
        poly.addPoint(x1, y1 - linewidth / 2);
        poly.addPoint(x2, y2 - linewidth / 2);
        poly.addPoint(x2, y2 + linewidth / 2);
        poly.addPoint(x1, y1 + linewidth / 2);
        g2d.fillPolygon(poly);
        poly = new Polygon();
        poly.addPoint(x2, y2 - linewidth / 2);
        poly.addPoint(x3, y3 - linewidth / 2);
        poly.addPoint(x3, y3 + linewidth / 2);
        poly.addPoint(x2, y2 + linewidth / 2);
        g2d.fillPolygon(poly);
        poly.addPoint(x3, y3 - linewidth / 2);
        poly.addPoint(x4, y4 - linewidth / 2);
        poly.addPoint(x4, y4 + linewidth / 2);
        poly.addPoint(x3, y3 + linewidth / 2);
        g2d.fillPolygon(poly);


        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(x4 - 1, y4);
        arrowHead.addPoint(x4 - 1, y4 + 10);
        arrowHead.addPoint(x4 - 10, y4 + 8);


        g2d.fillPolygon(arrowHead);
    }

    public static void main(String[] args) {
        GraphicsEnvironment e =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (String font : e.getAvailableFontFamilyNames()) {
            System.out.println(font);
        }
        JFrame f = new JFrame("MetaOmIcon test");
        final MetaOmIconPanel panel = new MetaOmIconPanel("MOG", "Futura", 150);
        f.getContentPane().add(panel);
        f.setSize(600, 622);
        f.setLocationRelativeTo(null);

        panel.setFocusable(true);
        panel.addKeyListener(new KeyAdapter() {
            @Override
			public void keyTyped(KeyEvent e) {
                System.out.println("Keyed!");
                File dest = Utils.chooseFileToSave();
                if (dest == null) {
                    return;
                }
                BufferedImage image = new BufferedImage(panel.getWidth(), panel
                        .getHeight(), BufferedImage.TYPE_INT_ARGB);
                panel.paintComponent(image.getGraphics());
                try {
                    ImageIO.write(image, "png", dest);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        f.setVisible(true);
        f.setDefaultCloseOperation(3);
    }
}
