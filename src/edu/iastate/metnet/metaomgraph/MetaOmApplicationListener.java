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

    @Override
	public void handleAbout(ApplicationEvent arg0) {
        new JButton();
        MetaOmGraph.getInstance()
                .actionPerformed(
                        new ActionEvent(arg0.getSource(), 1,
                                "show about window"));
        arg0.setHandled(true);
    }

    @Override
	public void handleOpenApplication(ApplicationEvent arg0) {
    }

    @Override
	public void handleOpenFile(ApplicationEvent arg0) {
    }

    @Override
	public void handlePreferences(ApplicationEvent arg0) {
    }

    @Override
	public void handlePrintFile(ApplicationEvent arg0) {
    }

    @Override
	public void handleQuit(ApplicationEvent arg0) {
    }

    @Override
	public void handleReOpenApplication(ApplicationEvent arg0) {
    }
}
