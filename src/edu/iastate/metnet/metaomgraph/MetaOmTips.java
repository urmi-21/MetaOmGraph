package edu.iastate.metnet.metaomgraph;

import com.l2fprod.common.swing.JTipOfTheDay.ShowOnStartupChoice;
import com.l2fprod.common.swing.JTipOfTheDay;
import com.l2fprod.common.swing.TipModel;
import com.l2fprod.common.swing.TipModel.Tip;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.BrowserLauncherRunner;
import edu.stanford.ejalbert.exceptionhandler.BrowserLauncherDefaultErrorHandler;
import edu.stanford.ejalbert.exceptionhandler.BrowserLauncherErrorHandler;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

public class MetaOmTips implements TipModel, HyperlinkListener {
    private ArrayList<TipModel.Tip> tips;

    public MetaOmTips() {
        tips = new ArrayList();
        addTip(
                "Welcome to MetaOmGraph!",
                "You can press F1 at any time to get help with whatever you're working on.\n\nOn some laptops (especially Mac laptops), you may need to press Fn-F1.");

        JTextPane tipPane = new JTextPane();
        tipPane.setEditable(false);
        tipPane.setContentType("text/html");
        tipPane.setText("<html><font face=\"dialog\" size=\"3\">Bug report?  Feature request?  Submit it with <a href=\"https://subversion.vrac.iastate.edu/Subversion/MetNet/Bugzilla/\">Bugzilla</a>!<br>Nobody knows how to improve a program more than its users!</font></html>");
        addTip("Feedback", tipPane);
        addTip(
                "Table Sorting",
                "You can sort the project data table by clicking on any of the headers.  Click once to sort in ascending order, twice to sort in decending order, and a third time to return to the default order.");
        addTip(
                "Series Recoloring",
                "You can recolor any series on a plot by double-clicking that series' entry in the legend.");
        addTip(
                "Resizing Saved Plots",
                "When using the Save Plot as Image feature, you can specify the size of the saved image.  The plot will then grow or shrink to fit the given size.  This often results in a higher-quality picture than leaving the default size, then growing or shrinking the image with an image-manipulation tool.");
        addTip("Properties", "Plot defaults (axis titles, background gradient), as well as row and column names, can be edited by selecting Project->Properties.");
        addTip("Saving Metadata sorts", "To quickly re-order any plot to the result of a metadata sort, save the result as a custom sort.  Run the metadata sort, then select the custom sort option, then click the Save button.");
        addTip("Sample tip", "Suggestion");
    }

    public TipModel.Tip getTipAt(int index) {
        if ((index < 0) || (index >= tips.size())) {
            return null;
        }
        return tips.get(index);
    }

    public int getTipCount() {
        return tips.size();
    }


    public static class MetaOmShowTipsChoice implements JTipOfTheDay.ShowOnStartupChoice, Serializable {
        public MetaOmShowTipsChoice() {
        }

        boolean show = true;

        public boolean isShowingOnStartup() {
            return show;
        }

        public void setShowingOnStartup(boolean showOnStartup) {
            show = showOnStartup;
        }
    }

    public void addTip(final String name, final String tip) {
        TipModel.Tip myTip = new TipModel.Tip() {
            public Object getTip() {
                return tip;
            }

            public String getTipName() {
                return name;
            }

        };
        tips.add(myTip);
    }

    public void addTip(final String name, final JTextPane tip) {
        tip.addHyperlinkListener(this);
        TipModel.Tip myTip = new TipModel.Tip() {
            public Object getTip() {
                return tip;
            }

            public String getTipName() {
                return name;
            }

        };
        tips.add(myTip);
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
            String urlString = e.getURL() + "";
            try {
                BrowserLauncher launcher = new BrowserLauncher(null);
                BrowserLauncherErrorHandler errorHandler = new BrowserLauncherDefaultErrorHandler();
                BrowserLauncherRunner runner = new BrowserLauncherRunner(launcher, urlString, errorHandler);
                Thread launcherThread = new Thread(runner);
                launcherThread.start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
