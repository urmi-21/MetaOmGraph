package edu.iastate.metnet.metaomgraph;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;


public class MetaOmHelpListener
        implements ActionListener, TreeSelectionListener, HyperlinkListener {
    private static final String HOST = "http://metnetweb.gdcb.iastate.edu//MetaOmGraph/help/newhelp/";
    private JTree helpTree;
    private JTextPane helpDisplay;

    public MetaOmHelpListener() {
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        String target = "";
        if (e.getSource() == null) {
            target = e.getActionCommand();
        } else if ((e.getSource() instanceof JMenuItem)) {
            if (MetaOmGraph.getDesktop().getSelectedFrame() != null) {
                target = MetaOmGraph.getDesktop().getSelectedFrame().getName();
            } else {
                target = "index.php";
            }
        } else {
            target = e.getActionCommand();
        }
        if ((target == null) || (target.length() < 4)) {
            target = "index.php";
        }
        if (target.substring(target.length() - 4).equals(".php")) {
            String urlString = "http://metnetweb.gdcb.iastate.edu/MetaOmGraph/help/newhelp/" + target;
            URI ns=null;
			try {
				ns = new URI(urlString);
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            try {
            	/*
                BrowserLauncher launcher = new BrowserLauncher(null);
                BrowserLauncherErrorHandler errorHandler = new BrowserLauncherDefaultErrorHandler();
                BrowserLauncherRunner runner = new BrowserLauncherRunner(
                        launcher, urlString, errorHandler);
                Thread launcherThread = new Thread(runner);
                launcherThread.start();
                */
            	//urmi
            	//JOptionPane.showMessageDialog(null, "Opening: "+ns.getPath());
                java.awt.Desktop.getDesktop().browse(ns);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(),
                        "Unable to launch web browser", "Error",
                        0);
                ex.printStackTrace();
            }
        }
    }

    public TreeNode[] findPath(TreeNode thisNode, String target) {
        if (((thisNode instanceof HelpNode)) &&
                (((HelpNode) thisNode).getAlias().equals(target))) {
            return ((HelpNode) thisNode).getPath();
        }
        TreeNode[] thisPath = null;
        int i = 0;
        while ((thisPath == null) && (i < thisNode.getChildCount()))
            thisPath = findPath(thisNode.getChildAt(i++), target);
        return thisPath;
    }

    private class HelpNode
            extends DefaultMutableTreeNode {
        private String treeText;
        private String sourcePage;
        private String alias;

        public HelpNode(String treeText, String alias, String sourcePage) {
            this.treeText = treeText;
            this.alias = alias;
            this.sourcePage = sourcePage;
        }

        @Override
		public String toString() {
            return treeText;
        }

        public String getPage() {
            return sourcePage;
        }

        public String getAlias() {
            return alias;
        }
    }

    @Override
	public void valueChanged(TreeSelectionEvent e) {
        String destPage = "http://metnetweb.gdcb.iastate.edu/MetaOmGraph/help/newhelp/" +
                ((HelpNode) helpTree.getLastSelectedPathComponent())
                        .getPage();
        try {
            helpDisplay.setPage(new URL(destPage));
        } catch (IOException e1) {
            helpDisplay.setText("Unable to connect to " + destPage);
        }
    }


    @Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
    }

    public AbstractAction createHelpAction(final String pagename) {
        AbstractAction result = new AbstractAction() {
            @Override
			public void actionPerformed(ActionEvent e) {
                ActionEvent e2 = new ActionEvent(this, 1001, pagename);
                MetaOmGraph.getHelpListener().actionPerformed(e2);
            }

        };
        return result;
    }
}
