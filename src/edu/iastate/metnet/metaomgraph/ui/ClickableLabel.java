package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ClickableLabel extends JLabel implements java.awt.event.MouseListener, FocusListener, KeyListener {
    private ArrayList<ActionListener> actionListeners;
    private String actionCommand;
    private String text;
    private String htmlText;
    private String pressedText;
    private String textColor = "blue";
    private javax.swing.border.Border pressedBorder;

    public static void main(String[] args) {
        ClickableLabel label = new ClickableLabel("Click Me!");
        label.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked!");
                if ((e.getModifiers() & 0x2) == 2) {
                    System.out.println("CTRL!");
                }
                if ((e.getModifiers() & 0x8) == 8) {
                    System.out.println("ALT!");
                }
                if ((e.getModifiers() & 0x1) == 1) {
                    System.out.println("SHIFT!");
                }
                if ((e.getModifiers() & 0x4) == 4) {
                    System.out.println("META!");
                }

            }
        });
        JFrame f = new JFrame("Clickable Label Test");
        f.getContentPane().add(label, "Center");
        f.getContentPane().add(new JLabel("North"), "North");
        f.getContentPane().add(new JLabel("South"), "South");
        f.getContentPane().add(new JLabel("West"), "West");
        f.getContentPane().add(new JLabel("East"), "East");
        f.setSize(200, 200);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }


    public ClickableLabel(String text) {
        super(text);
        init();
    }

    public ClickableLabel(String text, Icon icon) {
        super(text, icon, 2);
        init();
    }
    
    public ClickableLabel(String text, String textColor, Icon icon) {
    	super(text, icon, 2);
        this.textColor = textColor;
        init();
        this.text = htmlText;
        setText(this.text);
    }

    private void init() {
        text = getText();
        htmlText =
                ("<html><u><font color="+ textColor + ">" + text + "</font></u></html>");

        pressedText =
                ("<html><u><font color=#ff0000>" + text + "</font></u></html>");
        pressedBorder = new ThinDottedBorder(Color.RED);
        setCursor(java.awt.Cursor.getPredefinedCursor(12));
        addMouseListener(this);
        setFocusable(true);
        addFocusListener(this);
        addKeyListener(this);
        actionListeners = new ArrayList();
    }

    public void setActionCommand(String command) {
        actionCommand = command;
    }

    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    public void removeActionListener(ActionListener listener) {
        actionListeners.remove(listener);
    }

    protected void fireActionPerformed(ActionEvent e) {
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(e);
        }
    }

    @Override
	public void mouseClicked(MouseEvent e) {
        if (isEnabled()) {
            ActionEvent event = new ActionEvent(this,
                    1001, actionCommand, e
                    .getModifiers());
            fireActionPerformed(event);
        }
    }

    @Override
	public void mouseEntered(MouseEvent e) {
        if (isEnabled()) {
            setText(htmlText);
        }
    }

    @Override
	public void mouseExited(MouseEvent e) {
        if (isEnabled()) {
            setText(text);
            setBorder(null);
        }
    }

    @Override
	public void mousePressed(MouseEvent e) {
        if (isEnabled()) {
            setText(pressedText);
            setBorder(pressedBorder);
        }
    }

    @Override
	public void mouseReleased(MouseEvent e) {
        if (isEnabled()) {
            if (contains(e.getPoint())) {
                setText(htmlText);
            } else {
                setText(text);
            }
            setBorder(null);
        }
    }

    @Override
	public void focusGained(FocusEvent e) {
        if (isEnabled()) {
            setText(htmlText);
        }
    }

    @Override
	public void focusLost(FocusEvent e) {
        if (isEnabled()) {
            setText(text);
            setBorder(null);
        }
    }

    @Override
	public void keyPressed(KeyEvent e) {
        if ((isEnabled()) && (
                (e.getKeyCode() == 10) ||
                        (e.getKeyCode() == 32))) {
            setText(pressedText);
            setBorder(pressedBorder);
        }
    }

    @Override
	public void keyReleased(KeyEvent e) {
        if (isEnabled()) {
            if ((hasFocus()) && (getBorder() != null) && (
                    (e.getKeyCode() == 10) ||
                            (e.getKeyCode() == 32))) {
                ActionEvent event = new ActionEvent(this,
                        1001, actionCommand, e
                        .getModifiers());
                fireActionPerformed(event);
            }

            if (hasFocus()) {
                setText(htmlText);
            } else {
                setText(text);
            }
            setBorder(null);
        }
    }

    @Override
	public void keyTyped(KeyEvent e) {
        if ((isEnabled()) && (
                (e.getKeyCode() == 10) ||
                        (e.getKeyCode() == 32))) {
            ActionEvent event = new ActionEvent(this,
                    1001, actionCommand, e
                    .getModifiers());
            fireActionPerformed(event);
        }
    }
}
