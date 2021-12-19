package edu.iastate.metnet.metaomgraph.ui.VersionFrame.layouts.MainLayout;

import edu.iastate.metnet.metaomgraph.ui.VersionFrame.components.CloseButton;
import edu.iastate.metnet.simpleui.AbstractComponent;
import edu.iastate.metnet.simpleui.AbstractLayout;
import edu.iastate.metnet.simpleui.ISimpleConstraint;
import edu.iastate.metnet.simpleui.StackConstraint;
import edu.iastate.metnet.simpleui.components.Button;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class B_Buttons extends AbstractLayout {

    public B_Buttons() {
    }

    public ISimpleConstraint constraint() {
        StackConstraint constraint = new StackConstraint(10, 0);
        constraint.anchor = StackConstraint.EAST;
        constraint.weightx = 0;
        constraint.asColumns();

        return constraint;
    }

    protected static void browserOpen(URL url) throws URISyntaxException, IOException {
        if (! Desktop.isDesktopSupported()) return;

        Desktop desktop = Desktop.getDesktop();

        desktop.isSupported(Desktop.Action.OPEN);
        if (! desktop.isSupported(Desktop.Action.BROWSE)) return;

        desktop.browse(url.toURI());

    }

    protected static URL getJDKUrl() {
        String sysName = System.getProperty("os.name", "Linux").split(" ")[0];

        try {
            switch (sysName.toLowerCase()) {
                case "windows":
                    return new URL("https://developers.redhat.com/content-gateway/file/java-17-openjdk-17.0.1.0.12-1.win.x86_64.msi");

                default:
                    return new URL("https://www.oracle.com/java/technologies/downloads/");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public AbstractComponent[] components() {
        return new AbstractComponent[]{
                new CloseButton(),
                new Button("Visit Downloads", e -> {
                    try {
                        B_Buttons.browserOpen(getJDKUrl());
                    } catch (Exception ex) {
                        JFrame frame = new JFrame();
                        JOptionPane.showInputDialog(
                                frame,
                                "Please copy-and-paste the URL below to instead.",
                                "MetaOmGraph - Default Browser Unavailable",
                                JOptionPane.ERROR_MESSAGE,
                                null, null,
                                getJDKUrl().toString()
                        );
                    }
                })
        };
    }
}
