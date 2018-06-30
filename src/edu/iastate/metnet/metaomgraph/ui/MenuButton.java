package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;


public class MenuButton
        extends JButton
        implements MouseListener, KeyListener {
    JPopupMenu myMenu;

    public MenuButton() {
        this(null, null, null);
    }


    public MenuButton(JPopupMenu myMenu) {
        this(null, null, myMenu);
    }


    public MenuButton(String title, JPopupMenu myMenu) {
        this(title, null, myMenu);
    }


    public MenuButton(ImageIcon icon, JPopupMenu myMenu) {
        this(null, icon, myMenu);
    }


    public MenuButton(String title, ImageIcon icon, JPopupMenu myMenu) {
        super(title, icon);
        this.myMenu = myMenu;
        addMouseListener(this);
        addKeyListener(this);
        JComboBox box = new JComboBox();
        Object preventHide = box.getClientProperty("doNotCancelPopup");
        putClientProperty("doNotCancelPopup", preventHide);
    }


    public void setMenu(JPopupMenu myMenu) {
        this.myMenu = myMenu;
    }

    public void mouseClicked(MouseEvent arg0) {
    }

    public void mousePressed(MouseEvent arg0) {
        if (myMenu == null)
            return;
        if (!myMenu.isVisible()) {
            myMenu.show(this, 0, getHeight());
        } else {
            myMenu.setVisible(false);
        }
    }


    public void mouseReleased(MouseEvent arg0) {
    }


    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        if (myMenu == null)
            return;
        if (e.getKeyChar() == ' ') {
            myMenu.show(this, 0, getHeight());
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        MenuButton buttony = new MenuButton();
        buttony.setText("menu");
        JPopupMenu poppy = new JPopupMenu();
        poppy.add(new JCheckBox("huh, what"));
        JMenu op1 = new JMenu("Options");

        JMenuItem op2 = new JMenuItem("op2");
        JMenuItem op3 = new JMenuItem("op3");
        JMenuItem op4 = new JMenuItem("op4");
        op1.add(op3);
        op1.add(op4);
        poppy.add(op1);
        poppy.add(op2);
        buttony.setMenu(poppy);
        JFrame f = new JFrame("test frame");
        JToolBar tb = new JToolBar();
        tb.add(new JButton("dummy"));
        tb.add(buttony);
        tb.add(new JButton("also dummy"));
        f.getContentPane().add(tb, "First");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }
}
