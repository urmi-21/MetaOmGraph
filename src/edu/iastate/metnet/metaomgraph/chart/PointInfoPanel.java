package edu.iastate.metnet.metaomgraph.chart;

import edu.iastate.metnet.metaomgraph.IconTheme;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.ui.MenuButton;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.BrowserLauncherRunner;
import edu.stanford.ejalbert.exceptionhandler.BrowserLauncherDefaultErrorHandler;
import edu.stanford.ejalbert.exceptionhandler.BrowserLauncherErrorHandler;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public class PointInfoPanel extends JPanel {
	private JTextField xValue;
	private JTextField yValue;
	private JComboBox seriesName;
	private JButton tairButton;
	private JButton jbrowseButton;
	private JButton thalemineButton;
	private MenuButton infoButton;
	private JMenuItem atgsItem;
	private JMenuItem tairItem;
	private JMenuItem jbrowseItem;
	private JMenuItem thalemineItem;
	private JMenuItem ensemblItem;
	private JMenuItem ensemblPlantsItem;
	private JMenuItem refSeqItem;
	private JMenuItem geneCardsItem;

	private JButton markButton;
	private MetaOmChartPanel myChartPanel;
	private String selectedLocus;

	public PointInfoPanel(MetaOmChartPanel aChartPanel) {
		myChartPanel = aChartPanel;
		setLayout(new BoxLayout(this, 0));
		xValue = new JTextField();
		xValue.setEditable(false);
		yValue = new JTextField();
		yValue.setEditable(false);
		seriesName = new JComboBox();
		seriesName.setEnabled(false);
		jbrowseButton = new JButton("Araport-JBrowse");
		jbrowseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedLocus == null) {
					JOptionPane.showMessageDialog(null, "No Locus ID found.", "Error", 0);
					return;
				}
				String target;

				if (selectedLocus.indexOf(";") >= 0) {
					String[] ids = selectedLocus.split(";");
					target = (String) JOptionPane.showInputDialog(null, "Show Araport-JBrowse results for which ID?",
							"Araport-JBrowse", 3, null, ids, ids[0]);
				} else {
					target = selectedLocus;
				}
				String urlString = "http://www.araport.org/locus/" + target + "/browse";
				try {
					BrowserLauncher launcher = new BrowserLauncher(null);
					BrowserLauncherErrorHandler errorHandler = new BrowserLauncherDefaultErrorHandler();
					BrowserLauncherRunner runner = new BrowserLauncherRunner(launcher, urlString, errorHandler);
					Thread launcherThread = new Thread(runner);
					launcherThread.start();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Unable to launch web browser", "Error", 0);
				}

			}
		});
		jbrowseButton.setEnabled(false);
		thalemineButton = new JButton("Araport-ThaleMine");
		thalemineButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedLocus == null) {
					JOptionPane.showMessageDialog(null, "No Locus ID found.", "Error", 0);
					return;
				}
				String target;

				if (selectedLocus.indexOf(";") >= 0) {
					String[] ids = selectedLocus.split(";");
					target = (String) JOptionPane.showInputDialog(null, "Show Araport-ThaleMine results for which ID?",
							"Araport-ThaleMine", 3, null, ids, ids[0]);
				} else {
					target = selectedLocus;
				}
				String urlString = "http://www.araport.org/locus/" + target + "";
				try {
					BrowserLauncher launcher = new BrowserLauncher(null);
					BrowserLauncherErrorHandler errorHandler = new BrowserLauncherDefaultErrorHandler();
					BrowserLauncherRunner runner = new BrowserLauncherRunner(launcher, urlString, errorHandler);
					Thread launcherThread = new Thread(runner);
					launcherThread.start();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Unable to launch web browser", "Error", 0);
				}

			}
		});
		thalemineButton.setEnabled(false);
		tairButton = new JButton("TAIR");
		tairButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedLocus == null) {
					JOptionPane.showMessageDialog(null, "No Locus ID found.", "Error", 0);
					return;
				}
				String target;

				if (selectedLocus.indexOf(";") >= 0) {
					String[] ids = selectedLocus.split(";");
					target = (String) JOptionPane.showInputDialog(null, "Show TAIR results for which ID?", "TAIR", 3,
							null, ids, ids[0]);
				} else {
					target = selectedLocus;
				}
				String urlString = "http://www.arabidopsis.org/servlets/Search?type=general&name=" + target
						+ "&action=detail&method=4&sub_type=gene";
				try {
					BrowserLauncher launcher = new BrowserLauncher(null);
					BrowserLauncherErrorHandler errorHandler = new BrowserLauncherDefaultErrorHandler();
					BrowserLauncherRunner runner = new BrowserLauncherRunner(launcher, urlString, errorHandler);
					Thread launcherThread = new Thread(runner);
					launcherThread.start();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Unable to launch web browser", "Error", 0);
				}

			}
		});
		tairButton.setEnabled(false);
		JPopupMenu infoPopupMenu = new JPopupMenu();

		// urmi
		ensemblItem = new JMenuItem("Ensembl");
		ensemblItem.setEnabled(false);
		ensemblItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//if no point is selected, return;
				if(myChartPanel.getSelectedPoint()==null) {
					return;
				}
				MetaOmProject myProject = myChartPanel.getProject();
				URI ns = null;
				int[] rows = myChartPanel.getSelectedRows();
				int selectedSeries = myChartPanel.getSelectedSeries();
				String thisName = myProject.getRowName(rows[selectedSeries])[myProject.getDefaultColumn()].toString();
				try {
					ns = new URI("https://www.ensembl.org/Multi/Search/Results?q=" + thisName + ";site=ensembl_all");
					java.awt.Desktop.getDesktop().browse(ns);
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		ensemblPlantsItem = new JMenuItem("EnsemblPlants");
		ensemblPlantsItem.setEnabled(false);
		ensemblPlantsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//if no point is selected, return;
				if(myChartPanel.getSelectedPoint()==null) {
					return;
				}
				MetaOmProject myProject = myChartPanel.getProject();
				URI ns = null;
				int[] rows = myChartPanel.getSelectedRows();
				int selectedSeries = myChartPanel.getSelectedSeries();
				String thisName = myProject.getRowName(rows[selectedSeries])[myProject.getDefaultColumn()].toString();
				try {
					ns = new URI("https://plants.ensembl.org/Multi/Search/Results?species=all;idx=;q=" + thisName
							+ ";site=ensemblunit");
					java.awt.Desktop.getDesktop().browse(ns);
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		refSeqItem = new JMenuItem("RefSeq");
		refSeqItem.setEnabled(false);
		refSeqItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//if no point is selected, return;
				if(myChartPanel.getSelectedPoint()==null) {
					return;
				}
				MetaOmProject myProject = myChartPanel.getProject();
				URI ns = null;
				int[] rows = myChartPanel.getSelectedRows();
				int selectedSeries = myChartPanel.getSelectedSeries();
				String thisName = myProject.getRowName(rows[selectedSeries])[myProject.getDefaultColumn()].toString();
				try {
					ns = new URI("https://www.ncbi.nlm.nih.gov/nuccore/?term=" + thisName
							+ "[Text+Word]+AND+srcdb_refseq[PROP]");
					java.awt.Desktop.getDesktop().browse(ns);
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		
		geneCardsItem = new JMenuItem("GeneCards");
		geneCardsItem.setEnabled(false);
		geneCardsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//if no point is selected, return;
				if(myChartPanel.getSelectedPoint()==null) {
					return;
				}
				MetaOmProject myProject = myChartPanel.getProject();
				URI ns = null;
				int[] rows = myChartPanel.getSelectedRows();
				int selectedSeries = myChartPanel.getSelectedSeries();
				String thisName = myProject.getRowName(rows[selectedSeries])[myProject.getDefaultColumn()].toString();
				try {
					ns = new URI("https://www.genecards.org/Search/Keyword?queryString=" + thisName);
					java.awt.Desktop.getDesktop().browse(ns);
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		atgsItem = new JMenuItem("AtGeneSearch");
		atgsItem.setEnabled(false);
		atgsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//if no point is selected, return;
				if(myChartPanel.getSelectedPoint()==null) {
					return;
				}
				StringBuilder geneList;
				if (selectedLocus != null) {

					geneList = new StringBuilder(selectedLocus);
				} else {
					geneList = new StringBuilder("");
					int[] rows = myChartPanel.getSelectedRows();
					MetaOmProject myProject = myChartPanel.getProject();
					for (int row : rows) {
						Object[] names = myProject.getRowName(row);
						boolean done = false;
						for (int i = 0; (i < names.length) && (!done); i++) {
							Object name = names[i];
							if (Utils.isGeneID(name + "")) {
								geneList.append(";" + name);
								done = true;
							}
						}
					}
				}
				if (geneList.toString().equals("")) {
					JOptionPane.showMessageDialog(null, "Unable to find any gene IDs in the selected rows", "Error", 0);
					return;
				}
				String urlString = "http://metnetweb.gdcb.iastate.edu/AtGeneSearch/index.php?genelist=" + geneList;
				try {
					Class.forName("java.awt.Desktop");
					Desktop.getDesktop().browse(new URI(urlString));
					System.out.println("Launched a browser using Desktop");
				} catch (Exception ex) {
					try {
						BrowserLauncher launcher = new BrowserLauncher(null);
						Object errorHandler = new BrowserLauncherDefaultErrorHandler();
						BrowserLauncherRunner runner = new BrowserLauncherRunner(launcher, urlString,
								(BrowserLauncherErrorHandler) errorHandler);
						Thread launcherThread = new Thread(runner);
						launcherThread.start();
					} catch (Exception ex2) {
						JOptionPane.showMessageDialog(myChartPanel, "Unable to launch web browser", "Error", 0);
						ex2.printStackTrace();
					}
				}
			}
		});
		atgsItem.setToolTipText("Connect to AtGeneSearch for information on all selected genes");
		tairItem = new JMenuItem("TAIR");
		tairItem.setEnabled(false);
		tairItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//if no point is selected, return;
				if(myChartPanel.getSelectedPoint()==null) {
					return;
				}
				if (selectedLocus == null) {
					JOptionPane.showMessageDialog(null, "No Locus ID selected.", "Error", 0);
					return;
				}
				String target = "";

				if (selectedLocus.indexOf(";") >= 0) {
					String[] ids = selectedLocus.split(";");
					target = (String) JOptionPane.showInputDialog(null, "Show TAIR results for which ID?", "TAIR", 3,
							null, ids, ids[0]);
				} else {
					target = selectedLocus;
				}
				String urlString = "http://www.arabidopsis.org/servlets/Search?type=general&name=" + target
						+ "&action=detail&method=4&sub_type=gene";
				try {
					Class.forName("java.awt.Desktop");
					Desktop.getDesktop().browse(new URI(urlString));
					System.out.println("Launched a browser using Desktop");
				} catch (Exception ex) {
					try {
						BrowserLauncher launcher = new BrowserLauncher(null);
						BrowserLauncherErrorHandler errorHandler = new BrowserLauncherDefaultErrorHandler();
						BrowserLauncherRunner runner = new BrowserLauncherRunner(launcher, urlString, errorHandler);
						Thread launcherThread = new Thread(runner);
						launcherThread.start();
					} catch (Exception ex2) {
						JOptionPane.showMessageDialog(myChartPanel, "Unable to launch web browser", "Error", 0);
						ex2.printStackTrace();
					}
				}

			}
		});
		tairItem.setToolTipText("Connect to TAIR for information on the first selected gene");
		jbrowseItem = new JMenuItem("Araport-JBrowse");
		jbrowseItem.setEnabled(false);
		jbrowseItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//if no point is selected, return;
				if(myChartPanel.getSelectedPoint()==null) {
					return;
				}
				if (selectedLocus == null) {
					JOptionPane.showMessageDialog(null, "No Locus ID selected.", "Error", 0);
					return;
				}
				String target = "";

				if (selectedLocus.indexOf(";") >= 0) {
					String[] ids = selectedLocus.split(";");
					target = (String) JOptionPane.showInputDialog(null, "Show Araport-JBrowse results for which ID?",
							"Araport-JBrowse", 3, null, ids, ids[0]);
				} else {
					target = selectedLocus;
				}
				String urlString = "http://www.araport.org/locus/" + target + "/browse";
				try {
					Class.forName("java.awt.Desktop");
					Desktop.getDesktop().browse(new URI(urlString));
					System.out.println("Launched a browser using Desktop");
				} catch (Exception ex) {
					try {
						BrowserLauncher launcher = new BrowserLauncher(null);
						BrowserLauncherErrorHandler errorHandler = new BrowserLauncherDefaultErrorHandler();
						BrowserLauncherRunner runner = new BrowserLauncherRunner(launcher, urlString, errorHandler);
						Thread launcherThread = new Thread(runner);
						launcherThread.start();
					} catch (Exception ex2) {
						JOptionPane.showMessageDialog(myChartPanel, "Unable to launch web browser", "Error", 0);
						ex2.printStackTrace();
					}
				}

			}
		});
		jbrowseItem.setToolTipText("Connect to Araport-JBrowse for information on the first selected gene");
		thalemineItem = new JMenuItem("Araport-ThaleMine");
		thalemineItem.setEnabled(false);
		thalemineItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//if no point is selected, return;
				if(myChartPanel.getSelectedPoint()==null) {
					return;
				}
				if (selectedLocus == null) {
					JOptionPane.showMessageDialog(null, "No Locus ID selected.", "Error", 0);
					return;
				}
				String target = "";

				if (selectedLocus.indexOf(";") >= 0) {
					String[] ids = selectedLocus.split(";");
					target = (String) JOptionPane.showInputDialog(null, "Show Araport-ThaleMine results for which ID?",
							"Araport-ThaleMine", 3, null, ids, ids[0]);
				} else {
					target = selectedLocus;
				}
				String urlString = "http://www.araport.org/locus/" + target;
				try {
					Class.forName("java.awt.Desktop");
					Desktop.getDesktop().browse(new URI(urlString));
					System.out.println("Launched a browser using Desktop");
				} catch (Exception ex) {
					try {
						BrowserLauncher launcher = new BrowserLauncher(null);
						BrowserLauncherErrorHandler errorHandler = new BrowserLauncherDefaultErrorHandler();
						BrowserLauncherRunner runner = new BrowserLauncherRunner(launcher, urlString, errorHandler);
						Thread launcherThread = new Thread(runner);
						launcherThread.start();
					} catch (Exception ex2) {
						JOptionPane.showMessageDialog(myChartPanel, "Unable to launch web browser", "Error", 0);
						ex2.printStackTrace();
					}
				}

			}
		});
		thalemineItem.setToolTipText("Connect to Araport-ThaleMine for information on the first selected gene");
		infoPopupMenu.add(geneCardsItem);
		infoPopupMenu.add(ensemblItem);
		infoPopupMenu.add(ensemblPlantsItem);
		infoPopupMenu.add(refSeqItem);
		infoPopupMenu.add(atgsItem);
		infoPopupMenu.add(tairItem);
		infoPopupMenu.add(jbrowseItem);
		infoPopupMenu.add(thalemineItem);

		infoButton = new MenuButton("External web applications", MetaOmGraph.getIconTheme().getExternalSource(),
				infoPopupMenu);
		infoButton.setToolTipText("Connect to an external website for more info on the selected genes");
		

		markButton = new JButton("Mark");
		markButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				myChartPanel.getAnnotator().markSelected();
			}

		});
		markButton.setEnabled(false);
		add(new JLabel("Series: "));
		add(seriesName);

		add(infoButton);
		add(new JLabel("Column: "));
		add(xValue);
		add(new JLabel("Y-Value: "));
		add(yValue);
		add(markButton);
		yValue.setColumns(6);
	}

	public String cleanHTML(String source) {
		String result;
		for (result = new String(source); result.indexOf("<script") >= 0; result = result.substring(0,
				result.indexOf("<script")) + result.substring(result.indexOf("</script>") + 9))
			;
		for (; result.indexOf("<SCRIPT") >= 0; result = result.substring(0, result.indexOf("<SCRIPT"))
				+ result.substring(result.indexOf("</SCRIPT>") + 9))
			;

		int hitPoint = 0;
		for (hitPoint = 0; result.indexOf("href=\"/", hitPoint) >= 0; result = result.substring(0, hitPoint)
				+ "http://www.arabidopsis.org" + result.substring(hitPoint))
			hitPoint = result.indexOf("href=\"", hitPoint) + "href=\"".length();
		for (hitPoint = 0; result.indexOf("href='/", hitPoint) >= 0; result = result.substring(0, hitPoint)
				+ "http://www.arabidopsis.org" + result.substring(hitPoint))
			hitPoint = result.indexOf("href='", hitPoint) + "href='".length();
		for (hitPoint = 0; result.indexOf("src=\"/", hitPoint) >= 0; result = result.substring(0, hitPoint)
				+ "http://www.arabidopsis.org" + result.substring(hitPoint))
			hitPoint = result.indexOf("src=\"", hitPoint) + "src=\"".length();
		for (hitPoint = 0; result.indexOf("src='/", hitPoint) >= 0; result = result.substring(0, hitPoint)
				+ "http://www.arabidopsis.org" + result.substring(hitPoint))
			hitPoint = result.indexOf("src='", hitPoint) + "src='".length();

		hitPoint = 0;
		for (; result.indexOf("<input type=\"button\"") >= 0; result = result.substring(0, hitPoint)
				+ result.substring(result.indexOf(">", hitPoint) + 1))
			hitPoint = result.indexOf("<input type=\"button\"");
		for (hitPoint = 0; result.indexOf("width=\"100%\"", hitPoint) >= 0; result = result.substring(0, hitPoint)
				+ "width=\"602\"" + result.substring(hitPoint + "width=\"100%\"".length()))
			hitPoint = result.indexOf("width=\"100%\"", hitPoint);

		return result;
	}

	public void refresh() {
		if (myChartPanel.getSelectedPoint() == null) {
			xValue.setText("");
			yValue.setText("");
			geneCardsItem.setEnabled(false);
			ensemblItem.setEnabled(false);
			ensemblPlantsItem.setEnabled(false);
			refSeqItem.setEnabled(false);
			atgsItem.setEnabled(false);
			tairButton.setEnabled(false);
			jbrowseButton.setEnabled(false);
			thalemineButton.setEnabled(false);
			markButton.setEnabled(false);
		} else {
			xValue.setText(myChartPanel.getFormatter().format(myChartPanel.getSelectedPoint().getX()));
			// String
			// v=myChartPanel.getFormatter().format(myChartPanel.getSelectedPoint().getX());
			// JOptionPane.showMessageDialog(null, "@EW:"+v+"");
			yValue.setText(myChartPanel.getSelectedPoint().getY() + "");
			markButton.setEnabled(true);
		}

		if (myChartPanel.getSelectedSeries() < 0) {
			seriesName.removeAllItems();
			seriesName.setEnabled(false);
			selectedLocus = null;
		} else {
			seriesName.removeAllItems();
			selectedLocus = null;
			if (myChartPanel.getProject().getInfoColumnCount() <= 0) {
				seriesName.addItem(myChartPanel.getSelectedSeries() + 1);
			} else {
				for (int x = 0; x < myChartPanel.getProject().getInfoColumnCount(); x++) {
					String thisItem = myChartPanel.getProject()
							.getRowName(myChartPanel.getSelectedRows()[myChartPanel.getSelectedSeries()])[x] + "";

					if ((thisItem != null) && (!thisItem.trim().equals(""))) {
						if (thisItem.indexOf(";") >= 0) {
							String[] items = thisItem.split(";");
							for (int i = 0; i < items.length; i++) {
								if (Utils.getIDType(items[i]) == 1)
									if (selectedLocus == null)
										selectedLocus = items[i];
									else
										selectedLocus = (selectedLocus + ";" + items[i]);
							}
						} else if (Utils.getIDType(thisItem) == 1) {
							if (selectedLocus == null)
								selectedLocus = thisItem;
							else
								selectedLocus = (selectedLocus + ";" + thisItem);
						}
						seriesName.addItem(thisItem);
						// break;//urmi
					}
				}
			}
			seriesName.setEnabled(true);
			//if a point is selected enable these buttons
			geneCardsItem.setEnabled(selectedLocus != null);
			ensemblItem.setEnabled(selectedLocus != null);
			ensemblPlantsItem.setEnabled(selectedLocus != null);
			refSeqItem.setEnabled(selectedLocus != null);
			atgsItem.setEnabled(selectedLocus != null);
			tairButton.setEnabled(selectedLocus != null);
			jbrowseButton.setEnabled(selectedLocus != null);
			thalemineButton.setEnabled(selectedLocus != null);
			
			seriesName.setSelectedIndex(myChartPanel.getProject().getDefaultColumn());
			if (seriesName.getPreferredSize().width > getWidth() / 3)
				seriesName.setPreferredSize(new Dimension(getWidth() / 3, seriesName.getPreferredSize().height));
		}
		
		
		tairItem.setEnabled(selectedLocus != null);
		jbrowseItem.setEnabled(selectedLocus != null);
		thalemineItem.setEnabled(selectedLocus != null);
		if (tairItem.isEnabled())
			tairItem.setToolTipText("Connect to the TAIR website for the selected gene");
		else
			tairItem.setToolTipText("No Locus ID found, can't connect to TAIR");

		if (jbrowseItem.isEnabled())
			jbrowseItem.setToolTipText("Connect to the Araport-JBrowse website for the selected gene");
		else
			jbrowseItem.setToolTipText("No Locus ID found, can't connect to Araport-JBrowse");

		if (thalemineItem.isEnabled())
			thalemineItem.setToolTipText("Connect to the Araport-ThaleMine website for the selected gene");
		else
			thalemineItem.setToolTipText("No Locus ID found, can't connect to Araport-ThaleMine");
	}

	public void reselect() {
		if (myChartPanel.getSelectedPoint() == null)
			return;
		String xname = xValue.getText();
		int x = 0;

		String thisName = myChartPanel.getFormatter().format(x);
		while ((!thisName.equals(xname)) && (thisName != null) && (!thisName.equals(""))) {
			thisName = myChartPanel.getFormatter().format(++x);
		}
		if ((thisName == null) || (thisName.equals(""))) {
			myChartPanel.setSelectedPoint(null);
			myChartPanel.setSelectedSeries(-1);
		} else {
			final double finalX = x;
			final double finalY = Double.parseDouble(yValue.getText());
			myChartPanel.setSelectedPoint(new Point2D() {
				@Override
				public double getX() {
					return finalX;
				}

				@Override
				public double getY() {
					return finalY;
				}

				@Override
				public void setLocation(double d, double d1) {
				}
			});
			refresh();
		}
	}
}
