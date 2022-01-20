package edu.iastate.metnet.metaomgraph.ui.VersionFrame.components;

import edu.iastate.metnet.simpleui.components.Header;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BigHeader extends Header {
    public BigHeader(String title) {
        super(title);
    }

    public BigHeader(String title, Font font) {
        super(title, font);
    }

    @Override
    public Container create() {
        JLabel header = (JLabel) super.create();

        Border border = header.getBorder();
        Border margin = new EmptyBorder(0,0,15,0);
        header.setBorder(new CompoundBorder(border, margin));

        return header;
    }
}
