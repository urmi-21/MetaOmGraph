package edu.iastate.metnet.simpleui.components;

import edu.iastate.metnet.simpleui.AbstractComponent;

import javax.swing.*;
import java.awt.*;

public class Header extends Label {

    public Header(String title) {
        super(title);
    }

    public Header(String title, Font font) {
        super(title, font);
    }


    @Override
    public Container create() {
        JLabel label =  new edu.iastate.metnet.metaomgraph.ui.Header("<html><p>" +this.m_textContent + "</p></html>");
        this.applyStyles(label);

        return label;
    }
}
