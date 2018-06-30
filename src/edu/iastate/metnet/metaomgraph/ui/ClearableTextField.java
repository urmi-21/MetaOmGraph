package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.border.Border;


public class ClearableTextField
        extends JTextField
        implements MouseListener, KeyListener, MouseMotionListener, FocusListener {
    private Rectangle clearIconRect;
    private Icon clearIcon;
    private Icon mouseoverIcon;
    private Icon pressedIcon;
    private Icon normalIcon;
    private Border defaultBorder;
    private Border iconBorder;
    private boolean activated;
    private String defaultText;
    private Color defaultTextColor;

    public ClearableTextField() {
        this(null);
    }

    public ClearableTextField(String text) {
        super(text);
        try {
            initIcon();
        } catch (IOException e) {
            e.printStackTrace();
        }
        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);
        addFocusListener(this);
        activated = false;
        clearIconRect = new Rectangle(-2, -2, 1, 1);
    }


    public ClearableTextField(Icon normalIcon, Icon mouseoverIcon, Icon pressedIcon) {
        this.normalIcon = normalIcon;
        this.mouseoverIcon = mouseoverIcon;
        this.pressedIcon = pressedIcon;
        clearIcon = this.normalIcon;
        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);
        addFocusListener(this);
        activated = false;
        clearIconRect = new Rectangle(-2, -2, 1, 1);
    }

    public void setIcons(Icon normalIcon, Icon mouseoverIcon, Icon pressedIcon) {
        this.normalIcon = normalIcon;
        this.mouseoverIcon = mouseoverIcon;
        this.pressedIcon = pressedIcon;
        repaint();
    }

    public String getToolTipText(MouseEvent event) {
        if (clearIconRect.contains(event.getPoint())) {
            return "Clear";
        }
        return super.getToolTipText(event);
    }


    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.width += clearIcon.getIconWidth();
        if (d.height < clearIcon.getIconHeight()) {
            d.height = clearIcon.getIconHeight();
        }
        return d;
    }

    private void initIcon() throws IOException {
        normalIcon = new ImageIcon(getClass().getResource(
                "/resource/customicon/light_grey_close_icon.gif"));
        mouseoverIcon = new ImageIcon(getClass().getResource(
                "/resource/customicon/yellow_close_icon.gif"));
        pressedIcon = new ImageIcon(getClass().getResource(
                "/resource/customicon/dark_yellow_close_icon.gif"));
        clearIcon = normalIcon;


        ClearableTextField myself = this;
        Border border = new Border() {
            ClearableTextField parent;

            public Insets getBorderInsets(Component c) {
                return new Insets(0, 0, 0, clearIcon.getIconWidth() + 5);
            }

            public boolean isBorderOpaque() {
                return false;
            }

            public void paintBorder(Component c, Graphics g, int dx, int dy, int dwidth, int dheight) {
                int width = clearIcon.getIconWidth();
                int height = clearIcon.getIconHeight();
                int x = getWidth() - width - 5;
                int y = (getHeight() - height) / 2;
                clearIcon.paintIcon(parent, g, x, y);
            }


        };
        defaultBorder = getBorder();
        iconBorder = BorderFactory.createCompoundBorder(defaultBorder, border);
    }


    public static void main(String[] args) {
        JFrame f = new JFrame("Clearable Text Field Test");
        ClearableTextField empty = new ClearableTextField();
        ClearableTextField words = new ClearableTextField("Words!");
        empty.setDefaultText("initially empty");
        words.setDefaultText("initially had text");
        f.getContentPane().setLayout(
                new BoxLayout(f.getContentPane(), 1));
        f.getContentPane().add(new JButton("whoa"));
        f.getContentPane().add(empty);
        empty.setBackground(Color.RED);
        f.getContentPane().add(words);
        f.pack();
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        super.paintComponent(g2d);
        if (!getText().equals("")) {
            setBorder(iconBorder);
            int width = clearIcon.getIconWidth();
            int height = clearIcon.getIconHeight();
            int x = getWidth() - width;
            int y = (getHeight() - height) / 2;
            clearIconRect = new Rectangle(x, y, width, height);
        } else {
            clearIconRect = new Rectangle(-2, -2, 0, 0);
            setBorder(defaultBorder);
            if ((!isFocusOwner()) && (defaultText != null) && (!"".equals(defaultText))) {
                g2d.setFont(g2d.getFont().deriveFont(2));
                if (defaultTextColor != null) {
                    g2d.setColor(defaultTextColor);
                } else {
                    g2d.setColor(Color.LIGHT_GRAY);
                }

                g2d.drawString(defaultText, getInsets().left, getHeight() / 2 + getFontMetrics(g2d.getFont()).getHeight() / 2 - 2);
            }
        }
        g2d.dispose();
    }

    public void mouseClicked(MouseEvent e) {
        if (clearIconRect.contains(e.getPoint())) {
            setText("");
            fireActionPerformed();
        }
    }


    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
        if (clearIcon != normalIcon) {
            clearIcon = normalIcon;
            repaint();
        }
    }

    public void mousePressed(MouseEvent e) {
        if ((clearIconRect.contains(e.getPoint())) &&
                (clearIcon != pressedIcon)) {
            clearIcon = pressedIcon;
            activated = true;
            repaint();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (clearIcon != normalIcon) {
            clearIcon = normalIcon;
            repaint();
        }
        activated = false;
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 27) {
            setText("");
            fireActionPerformed();
        }
    }


    public void keyReleased(KeyEvent e) {
    }


    public void keyTyped(KeyEvent e) {
    }


    public void mouseDragged(MouseEvent e) {
        if (clearIconRect.contains(e.getPoint())) {
            if ((activated) &&
                    (clearIcon != pressedIcon)) {
                clearIcon = pressedIcon;
                repaint();
            }

        } else if (clearIcon != normalIcon) {
            clearIcon = normalIcon;
            repaint();
        }
    }

    public void mouseMoved(MouseEvent e) {
        if (clearIconRect.contains(e.getPoint())) {
            setCursor(Cursor.getDefaultCursor());
            if (clearIcon == normalIcon) {
                clearIcon = mouseoverIcon;
                repaint();
            }
        } else {
            setCursor(Cursor.getPredefinedCursor(2));
            if (clearIcon != normalIcon) {
                clearIcon = normalIcon;
                repaint();
            }
        }
    }

    public Icon getMouseoverIcon() {
        return mouseoverIcon;
    }

    public void setMouseoverIcon(Icon mouseoverIcon) {
        this.mouseoverIcon = mouseoverIcon;
        repaint();
    }

    public Icon getNormalIcon() {
        return normalIcon;
    }

    public void setNormalIcon(Icon normalIcon) {
        this.normalIcon = normalIcon;
        repaint();
    }

    public Icon getPressedIcon() {
        return pressedIcon;
    }

    public void setPressedIcon(Icon pressedIcon) {
        this.pressedIcon = pressedIcon;
        repaint();
    }

    public void focusGained(FocusEvent e) {
        repaint();
    }

    public void focusLost(FocusEvent e) {
        repaint();
    }

    public void setDefaultText(String text) {
        defaultText = text;
        repaint();
    }

    public String getDefaultText() {
        return defaultText;
    }

    public void setDefaultTextColor(Color defaultColor) {
        defaultTextColor = defaultColor;
    }

    public Color getDefaultTextColor() {
        return defaultTextColor;
    }
}
