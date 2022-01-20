package edu.iastate.metnet.metaomgraph.ui;

import javax.swing.*;
import java.awt.*;

public class Header extends JLabel {
    public Header(String text) {
        this(text, 18.0f);
    }

    public Header(String text, float size) {
        super(text);
        this.setFont(this.getFont().deriveFont(size));
    }

    public Header(String text, Font font) {
        super(text);
        this.setFont(font);
    }

}
