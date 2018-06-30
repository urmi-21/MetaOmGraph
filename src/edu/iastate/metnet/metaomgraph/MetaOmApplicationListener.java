package edu.iastate.metnet.metaomgraph;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;

import java.awt.event.ActionEvent;
import javax.swing.JButton;

public class MetaOmApplicationListener implements ApplicationListener {
    public MetaOmApplicationListener() {
        Application.getApplication().addApplicationListener(this);
    }

    public void handleAbout(ApplicationEvent arg0) {
        new JButton();
        MetaOmGraph.getInstance()
                .actionPerformed(
                        new ActionEvent(arg0.getSource(), 1,
                                "show about window"));
        arg0.setHandled(true);
    }

    public void handleOpenApplication(ApplicationEvent arg0) {
    }

    public void handleOpenFile(ApplicationEvent arg0) {
    }

    public void handlePreferences(ApplicationEvent arg0) {
    }

    public void handlePrintFile(ApplicationEvent arg0) {
    }

    public void handleQuit(ApplicationEvent arg0) {
    }

    public void handleReOpenApplication(ApplicationEvent arg0) {
    }
}
