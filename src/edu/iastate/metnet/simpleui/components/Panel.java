package edu.iastate.metnet.simpleui.components;

import edu.iastate.metnet.simpleui.AbstractComponent;

import javax.swing.*;
import java.awt.*;

public class Panel extends AbstractComponent {
    @Override
    public Container create() {
        return new JPanel();
    }
}
