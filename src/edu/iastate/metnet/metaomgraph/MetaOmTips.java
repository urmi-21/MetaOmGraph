package edu.iastate.metnet.metaomgraph;

import com.l2fprod.common.swing.JTipOfTheDay;
import com.l2fprod.common.swing.TipModel;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.BrowserLauncherRunner;
import edu.stanford.ejalbert.exceptionhandler.BrowserLauncherDefaultErrorHandler;
import edu.stanford.ejalbert.exceptionhandler.BrowserLauncherErrorHandler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import edu.stanford.ejalbert.launching.IBrowserLaunching;
import net.sf.wraplog.AbstractLogger;
import java.util.List;
import java.util.Random;

public class MetaOmTips implements TipModel, HyperlinkListener {
    private ArrayList<TipModel.Tip> tips;
    private JTextPane tipPane;
    private JPanel tipPanel;
    private static int tipIndex = 0;
    public MetaOmTips() {
        tips = new ArrayList();
        addTip(
                "Welcome to MetaOmGraph!",
                "<html><p style=\"color:3335FF\">You can press F1 at any time to get help with whatever you're working on.\n\nOn some laptops (especially Mac laptops), you may need to press Fn-F1.</p></html>");

        tipPane = new JTextPane();
        tipPane.setEditable(false);
        tipPane.setContentType("text/html");
        addTip("Feedback", 
        		"<html><p style=\"color:red\"> Bug report?  Feature request?  Submit it to MOG's GitHub page <a href=\"https://github.com/urmi-21/MetaOmGraph\">MOG GitHub</a>!<br>Nobody knows how to improve a program more than its users!</p></html>");
        addTip(
                "Table Sorting",
                "<html><p style=\"color:DC143C\">You can sort the project data table by clicking on any of the headers.  Click once to sort in ascending order, twice to sort in decending order, and a third time to return to the default order.</p></html>");
        addTip(
                "Series Recoloring",
                "<html><p style=\"color:blue\">You can recolor any series on a plot by double-clicking that series' entry in the legend.</p></html>");
        addTip(
                "Resizing Saved Plots",
                "<html><p style=\"color:green\">When using the Save Plot as Image feature, you can specify the size of the saved image.  The plot will then grow or shrink to fit the given size.  "
                + "This often results in a higher-quality picture than leaving the default size, then growing or shrinking the image with an image-manipulation tool.</p></html>");
        addTip("Properties", 
        		"<html><p style=\"color:FF00FF\">Plot defaults (axis titles, background gradient), as well as row and column names, can be edited by selecting Project->Properties from the menu bar.</p></html>");
        addTip("Saving Metadata sorts", 
        		"<html><p style=\"color:FF4C33\">To quickly re-order any plot to the result of a metadata sort, save the result as a custom sort.  Run the metadata sort, then select the custom sort option, then click the Save button.</p></html>");
        
        
        createTipPanel();
    }

    @Override
	public TipModel.Tip getTipAt(int index) {
        if ((index < 0) || (index >= tips.size())) {
            return null;
        }
        return tips.get(index);
    }

    @Override
	public int getTipCount() {
        return tips.size();
    }


    public static class MetaOmShowTipsChoice implements JTipOfTheDay.ShowOnStartupChoice, Serializable {
        public MetaOmShowTipsChoice() {
        }

        boolean show = true;

        @Override
		public boolean isShowingOnStartup() {
            return show;
        }

        @Override
		public void setShowingOnStartup(boolean showOnStartup) {
            show = showOnStartup;
        }
    }

    public void addTip(final String name, final String tip) {
        TipModel.Tip myTip = new TipModel.Tip() {
            @Override
			public Object getTip() {
                return tip;
            }

            @Override
			public String getTipName() {
                return name;
            }

        };
        tips.add(myTip);
    }

    private void createTipPanel() {

    	tipPanel = new JPanel();
    	tipPanel.setLayout(new BoxLayout(tipPanel, BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel(new BorderLayout(0, 0));
        topPanel.setMaximumSize(new Dimension(500, 0));
        JLabel hint = new JLabel("<html><b>Mog tips:<b></html>");
        hint.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        topPanel.add(hint);

        JSeparator separator = new JSeparator();
        separator.setForeground(Color.gray);

        topPanel.add(separator, BorderLayout.SOUTH);

        tipPanel.add(topPanel);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        textPanel.setPreferredSize(new Dimension(500, 200));
        
        Random random = new Random();
    	tipIndex = random.nextInt(7);
        tipPane.setText(tips.get(tipIndex).getTip().toString());
        tipPane.setBackground(Color.WHITE);
        textPanel.add(new JScrollPane(tipPane));

        tipPanel.add(textPanel);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton nextTip = new JButton("Next Tip");
        nextTip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tipIndex = tipIndex % tips.size();
				String tipText = tips.get(tipIndex).getTip().toString();
				tipPane.setText(tipText);
				tipIndex++;
			}
		});
        bottom.add(nextTip);
        tipPanel.add(bottom);

        bottom.setMaximumSize(new Dimension(450, 0));
    }
    
    public JPanel getTipPane() {
    	return tipPanel;
    }
    
    public void addTip(final String name, final JTextPane tip) {
        tip.addHyperlinkListener(this);
        TipModel.Tip myTip = new TipModel.Tip() {
            @Override
			public Object getTip() {
                return tip;
            }

            @Override
			public String getTipName() {
                return name;
            }

        };
        tips.add(myTip);
    }

    @Override
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


