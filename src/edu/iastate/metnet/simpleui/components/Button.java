package edu.iastate.metnet.simpleui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Button extends Label {

    ActionListener[] actions;

    public Button(String text) {
        this(text, e -> System.exit(0));
    }

    public Button(String text, ActionListener actionListener) {
        super(text);
        this.actions = new ActionListener[]{actionListener};
    }

    @Override
    public Container create() {
        JButton btn = new JButton(this.m_textContent);
        for (ActionListener handler : actions) {
            btn.addActionListener(handler);
        }

        return btn;
    }
}
