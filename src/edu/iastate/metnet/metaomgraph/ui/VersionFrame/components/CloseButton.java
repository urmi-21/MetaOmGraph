package edu.iastate.metnet.metaomgraph.ui.VersionFrame.components;

import edu.iastate.metnet.simpleui.components.Button;

import javax.swing.*;
import java.awt.*;

public class CloseButton extends Button {
    public CloseButton() {
        super("Close");
    }

    @Override
    public Container create() {
        JButton button = (JButton) super.create();
        button.addActionListener(e -> ((JFrame) SwingUtilities.getRoot(button)).dispose());

        return button;
    }
}
