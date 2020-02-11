package edu.iastate.metnet.metaomgraph.test;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ColorPicker extends java.awt.event.MouseAdapter implements java.awt.event.KeyListener, java.awt.event.MouseMotionListener {
    private static JFrame f;
    private static JTextField red;
    private static JTextField green;
    private static JTextField blue;
    private static JTextField web;
    private static JLabel g1r;
    private static JLabel g1g;
    private static JLabel g1b;
    private static JLabel g2r;
    private static JLabel g2g;
    private static JLabel g2b;
    private static JLabel g1web;
    private static JLabel g2web;
    private static JLabel drag;
    private static JLabel g1color;
    private static JLabel g2color;
    private static JLabel midcolor;
    private static JLabel gradsample;
    private static JPanel colorPanel;
    private static JPanel g1Panel;
    private static JPanel g2Panel;
    private static JPanel gradPanel;
    private static javax.swing.JSlider g1slider;
    private static javax.swing.JSlider g2slider;
    private java.awt.Robot myRobot;

    public ColorPicker() throws java.awt.AWTException {
        myRobot = new java.awt.Robot();
    }


    public static void main(String[] args)
            throws java.awt.AWTException {
        JFrame.setDefaultLookAndFeelDecorated(true);
        ColorPicker picker = new ColorPicker();
        f = new JFrame("Color Picker");
        red = new JTextField("255");
        green = new JTextField("255");
        blue = new JTextField("255");
        web = new JTextField("#FFFFFF");
        drag = new JLabel("Drag!");


        g1r = new JLabel("0");
        g2r = new JLabel("0");
        g1g = new JLabel("0");
        g2g = new JLabel("0");
        g1b = new JLabel("0");
        g2b = new JLabel("0");
        g1web = new JLabel("#000000");
        g2web = new JLabel("#000000");
        g1slider = new javax.swing.JSlider(0, 100, 100);
        g2slider = new javax.swing.JSlider(0, 100, 0);
        f.getContentPane().setLayout(new javax.swing.BoxLayout(f.getContentPane(), 1));
        colorPanel = new JPanel();
        g1Panel = new JPanel();
        g2Panel = new JPanel();
        gradPanel = new JPanel();
        colorPanel.setLayout(new javax.swing.BoxLayout(colorPanel, 1));
        g1Panel.setLayout(new javax.swing.BoxLayout(g1Panel, 1));
        g2Panel.setLayout(new javax.swing.BoxLayout(g2Panel, 1));
        gradPanel.setLayout(new javax.swing.BoxLayout(gradPanel, 1));
        JPanel triPanel = new JPanel();
        triPanel.setLayout(new javax.swing.BoxLayout(triPanel, 0));
        g1color = new JLabel();
        g2color = new JLabel();
        midcolor = new JLabel();
        gradsample = new JLabel();
        g1color.setOpaque(true);
        g2color.setOpaque(true);
        midcolor.setOpaque(true);
        gradsample.setOpaque(true);
        g1color.setPreferredSize(new java.awt.Dimension(32, 32));
        g2color.setPreferredSize(new java.awt.Dimension(32, 32));
        midcolor.setPreferredSize(new java.awt.Dimension(32, 32));
        colorPanel.add(midcolor);
        colorPanel.add(red);
        colorPanel.add(green);
        colorPanel.add(blue);
        colorPanel.add(web);
        g1Panel.add(g1color);
        g1Panel.add(g1r);
        g1Panel.add(g1g);
        g1Panel.add(g1b);
        g1Panel.add(g1web);
        g2Panel.add(g2color);
        g2Panel.add(g2r);
        g2Panel.add(g2g);
        g2Panel.add(g2b);
        g2Panel.add(g2web);
        triPanel.add(g1Panel);
        triPanel.add(colorPanel);
        triPanel.add(g2Panel);
        gradPanel.add(g1slider);
        gradPanel.add(g2slider);


        f.getContentPane().add(drag);
        f.getContentPane().add(triPanel);
        f.getContentPane().add(gradPanel);
        f.pack();
        f.addMouseListener(picker);
        f.addMouseMotionListener(picker);
        f.setVisible(true);
        f.setResizable(false);
        f.setDefaultCloseOperation(3);
    }

    public java.awt.Color getColor(java.awt.Point pt) {
        return myRobot.getPixelColor(pt.x + f.getX(), pt.y + f.getY());
    }

    @Override
	public void mousePressed(MouseEvent e) {
        f.setCursor(java.awt.Cursor.getPredefinedCursor(1));
    }

    @Override
	public void mouseReleased(MouseEvent e) {
        System.out.println(getColor(e.getPoint()) + " at " + e.getPoint().x + ", " + e.getPoint().y);
        f.setCursor(java.awt.Cursor.getDefaultCursor());
    }


    @Override
	public void keyTyped(KeyEvent e) {
    }


    @Override
	public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 27)
            System.exit(0);
        Object o = e.getSource();
        if (!(o instanceof JTextField)) {
        }
    }


    @Override
	public void keyReleased(KeyEvent e) {
    }


    @Override
	public void mouseDragged(MouseEvent e) {
        f.getContentPane().setBackground(getColor(e.getPoint()));
        Color c = getColor(e.getPoint());
        midcolor.setBackground(c);
        red.setText(c.getRed() + "");
        green.setText(c.getGreen() + "");
        blue.setText(c.getBlue() + "");
        String webString = Integer.toHexString(c.getRGB() & 0x00FFFFFF).toUpperCase();
        while (webString.length() < 6) {
            webString = "0" + webString;
        }
        web.setText("#" + webString);
    }

    @Override
	public void mouseMoved(MouseEvent e) {
    }
}
