package edu.iastate.metnet.metaomgraph.ui.VersionFrame.components;

import edu.iastate.metnet.metaomgraph.ui.VersionFrame.VersionController;
import edu.iastate.metnet.simpleui.components.Label;

import java.awt.*;

public class FmtVersionLabel extends Label {
    public FmtVersionLabel(String title) {
        super(title);
    }

    public FmtVersionLabel(String title, Font font) {
        super(title, font);
    }


    @Override
    public Container create() {
        this.m_textContent = String.format(this.m_textContent, VersionController.getVersion());
        return super.create();
    }
}
