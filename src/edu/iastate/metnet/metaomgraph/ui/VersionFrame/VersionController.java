package edu.iastate.metnet.metaomgraph.ui.VersionFrame;

import edu.iastate.metnet.metaomgraph.ui.VersionFrame.layouts.MainLayout.Manifest;
import edu.iastate.metnet.simpleui.LayoutFactory;

import javax.swing.*;

public class VersionController {
    final protected LayoutFactory factory;

    VersionController() {
        factory = new LayoutFactory(new Manifest());
    }

    public void prompt() {
        JFrame frame = factory.make("Local Java Outdated");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static boolean check() {
        if (getVersion() < 11)
        {
            VersionController ctrl = new VersionController();
            ctrl.prompt();
            return true;
        }

        return false;
    }

    public static int getVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }
        return Integer.parseInt(version);
    }

    public static void main(String[] args) {
        VersionController controller = new VersionController();
        controller.prompt();
    }

}
