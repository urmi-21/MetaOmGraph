package edu.iastate.metnet.simpleui.components;

import edu.iastate.metnet.simpleui.AbstractComponent;

import javax.swing.*;
import java.awt.*;

public class Label extends AbstractComponent {
    protected String m_textContent;
    protected Font   m_font;

    public Label(String title) {
        this.m_textContent = title;
    }

    public Label(String title, Font font) {
        this.m_textContent = title;
        this.m_font        = font;
    }

    public void applyStyles(JLabel label) {
        Font font = label.getFont();
        label.setFont(this.m_font == null
                      ? font.deriveFont(font.getStyle() & ~Font.BOLD)
                      : this.m_font);
    }

    @Override
    public Container create() {
        JLabel label = new JLabel("<html><p>" + this.m_textContent + "</p></html>");

        this.applyStyles(label);

        return label;
    }
}
