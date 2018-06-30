package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.*;
import edu.iastate.metnet.metaomgraph.SwingWorker;
import edu.iastate.metnet.metaomgraph.MetaOmProject.RepAveragedData;
import edu.iastate.metnet.metaomgraph.chart.MetaOmChartPanel;
import edu.iastate.metnet.metaomgraph.test.BoxPlotter;
import edu.iastate.metnet.metaomgraph.test.GeneHistogram;
import edu.iastate.metnet.metaomgraph.throbber.MetaOmThrobber;
import edu.iastate.metnet.metaomgraph.throbber.MultiFrameImageThrobber;
import edu.iastate.metnet.metaomgraph.throbber.Throbber;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.BrowserLauncherRunner;
import edu.stanford.ejalbert.exceptionhandler.BrowserLauncherDefaultErrorHandler;
import edu.stanford.ejalbert.exceptionhandler.BrowserLauncherErrorHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JToolBar.Separator;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.jdom.Element;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MetaOmTablePanel extends JPanel implements ActionListener, ListSelectionListener, ChangeListener {
	public static final String GRAPH_LIST_COMMAND = "graph list";
	public static final String GRAPH_SELECTED_COMMAND = "graph selected";
	public static final String GRAPH_FILTERED_COMMAND = "graph filter";
	public static final String REPORT_COMMAND = "report";
	public static final String ATGENESEARCH_COMMAND = "atgenesearch";
	public static final String TAIR_COMMAND = "tair";
	public static final String ARAPORT_THALEMINE_COMMAND = "ThaleMine";
	public static final String ARAPORT_JBROWSE_COMMAND = "JBrowse";
	public static final String LIST_FROM_FILTER_COMMAND = "list from filter";
	public static final String DELETE_LIST_COMMAND = "delete list";
	public static final String NEW_LIST_COMMAND = "new list";
	public static final String EDIT_LIST_COMMAND = "edit list";
	public static final String RENAME_LIST_COMMAND = "rename list";
	public static final String PEARSON_COMMAND = "pearson correlation";
	public static final String SPEARMAN_COMMAND = "spearman correlation";
	public static final String SAVE_CORRELATION_COMMAND = "save correlation";
	public static final String REMOVE_CORRELATION_COMMAND = "remove correlation";
	public static final String REMOVE_ALL_CORRELATIONS_COMMAND = "remove all correlations";
	public static final String EXCLUDE_SAMPLES_COMMAND = "Exclude samples";
	public static final String EUCLIDEAN_COMMAND = "euclidean distance";
	public static final String CANBERRA_COMMAND = "canberra distance";
	public static final String MANHATTAN_COMMAND = "manhattan distance";
	public static final String WEIGHTED_EUCLIDEAN_COMMAND = "weighted euclidean distance";
	public static final String WEIGHTED_MANHATTAN_COMMAND = "weighted manhattan distance";
	public static final String PAIRWISE_PEARSON_COMMAND = "pairwise pearson";
	public static final String PAIRWISE_SPEARMAN_COMMAND = "pairwise spearman";
	public static final String GRAPH_REPS_COMMAND = "plot reps";
	public static final String GRAPH_BOXPLOT_COMMAND = "make boxplot";
	public static final String GRAPH_BOXPLOT_COLS_COMMAND = "col boxplot";
	public static final String MAKE_HISTOGRAM_COMMAND = "create histogram";
	private JButton reportButton;
	private JButton listFromFilterButton;
	private MenuButton plotButton;
	private JMenuItem plotListItem;
	private JMenuItem plotRowsItem;
	private JMenuItem plotFilterItem;
	private JMenuItem plotRepsItem;
	private JMenuItem plotBoxRowItem;
	private JMenuItem plotBoxColItem;
	private JMenuItem plotHistogramItem;
	private MenuButton analyzeMenuButton;
	private JMenuItem pearsonItem;
	// urmi
	private JMenuItem pearsonItem2;
	private JMenuItem pearsonItemPool;
	private JMenuItem spearmanItem;
	private JMenuItem euclideanItem;
	private JMenuItem canberraItem;
	private JMenuItem manhattanItem;
	private JMenuItem weightedEuclideanItem;
	private JMenuItem weightedManhattanItem;
	private JMenuItem saveCorrelationItem;
	private JMenuItem pairwisePearsonItem;
	private JMenuItem pairwiseSpearmanItem;
	private JMenu removeCorrelationMenu;
	private JMenu selectedRowsMenu;
	private MenuButton infoButton;
	private JMenuItem atgsItem;
	private JMenuItem tairItem;
	private JMenuItem thaleMineItem;
	private JMenuItem jBrowseItem;
	private JPanel listPanel;
	private JScrollPane geneListScrollPane;
	private JScrollPane geneListDisplayPane;
	private JButton listDeleteButton;
	private JButton listEditButton;
	private JButton listCreateButton;
	private JButton listRenameButton;
	private MetaOmProject myProject;
	private JTabbedPane tabby;
	private JList geneLists;
	private StripedTable listDisplay;
	// private MetadataPanel extInfoPanel;
	// urmi
	private MetadataTreeDisplayPanel extInfoPanel2;
	private MetadataTableDisplayPanel mdtablepanel;
	private JToolBar dataToolbar;
	private JToolBar listToolbar;
	private JSplitPane listSplitPane;
	private FilterableTableModel filterModel;
	private NoneditableTableModel mainModel;
	private TableSorter sorter;
	private ClearableTextField filterField;
	private Throbber throbber;
	private CorrelationValue[] lastCorrelation;

	public MetaOmTablePanel(MetaOmProject project) {
		myProject = project;
		setLayout(new BorderLayout());
		listPanel = new JPanel(new BorderLayout());
		dataToolbar = new JToolBar();
		dataToolbar.setFloatable(false);
		listToolbar = new JToolBar();
		listToolbar.setFloatable(false);
		IconTheme theme = MetaOmGraph.getIconTheme();
		listDeleteButton = new JButton(theme.getListDelete());
		listDeleteButton.setActionCommand("delete list");
		listDeleteButton.addActionListener(this);
		listDeleteButton.setToolTipText("Delete the selected list");

		listEditButton = new JButton(theme.getListEdit());
		listEditButton.setActionCommand("edit list");
		listEditButton.addActionListener(this);
		listEditButton.setToolTipText("Edit the selected list");
		listRenameButton = new JButton(theme.getListRename());
		listRenameButton.setActionCommand("rename list");
		listRenameButton.addActionListener(this);
		listRenameButton.setToolTipText("Rename the selected list");

		listCreateButton = new JButton(theme.getListAdd());
		listCreateButton.addActionListener(this);
		listCreateButton.setActionCommand("new list");
		listCreateButton.setToolTipText("Create a new list");
		listToolbar.add(listCreateButton);
		listToolbar.add(listEditButton);
		listToolbar.add(listRenameButton);
		listToolbar.add(listDeleteButton);

		plotButton = new MenuButton("Plot", theme.getPlot(), null);
		plotButton.setToolTipText("Plot");
		selectedRowsMenu = new JMenu("Selected Rows");
		plotListItem = new JMenuItem("Entire List");

		plotRowsItem = new JMenuItem("Line Chart");
		plotFilterItem = new JMenuItem("Filtered List");

		plotRepsItem = new JMenuItem("Line Chart with Averaged Replicates");

		plotBoxRowItem = new JMenuItem("Box Plot");
		plotBoxColItem = new JMenuItem("Box Plot Samples");

		plotHistogramItem = new JMenuItem("Histogram");
		plotListItem.setActionCommand(GRAPH_LIST_COMMAND);
		plotListItem.addActionListener(this);
		plotRowsItem.setActionCommand(GRAPH_SELECTED_COMMAND);
		plotRowsItem.addActionListener(this);
		plotFilterItem.setActionCommand(GRAPH_FILTERED_COMMAND);
		plotFilterItem.addActionListener(this);
		plotFilterItem.setEnabled(false);
		plotRepsItem.setActionCommand("plot reps");
		plotRepsItem.addActionListener(this);
		plotBoxRowItem.setActionCommand("make boxplot");
		plotBoxRowItem.addActionListener(this);
		plotBoxColItem.setActionCommand("col boxplot");
		plotBoxColItem.addActionListener(this);
		plotHistogramItem.setActionCommand("create histogram");
		plotHistogramItem.addActionListener(this);
		JPopupMenu plotPopupMenu = new JPopupMenu();
		selectedRowsMenu.add(plotRowsItem);
		selectedRowsMenu.add(plotRepsItem);
		selectedRowsMenu.add(plotBoxRowItem);
		selectedRowsMenu.add(plotHistogramItem);
		plotPopupMenu.add(selectedRowsMenu);

		plotPopupMenu.add(plotFilterItem);
		plotPopupMenu.add(plotListItem);

		plotButton.setMenu(plotPopupMenu);
		plotButton.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				selectedRowsMenu.setEnabled(listDisplay.getSelectedRowCount() > 0);
			}

		});
		dataToolbar.add(plotButton);

		JPopupMenu analyzePopupMenu = new JPopupMenu();
		pearsonItem = new JMenuItem("Pearson Correlation");
		pearsonItem.setActionCommand("pearson correlation");
		pearsonItem.addActionListener(this);
		// urmi
		pearsonItem2 = new JMenuItem("Pearson Correlation2");
		pearsonItem2.setActionCommand("pearson correlation2");
		pearsonItem2.addActionListener(this);

		pearsonItemPool = new JMenuItem("Pearson Correlation Pooled");
		pearsonItemPool.setActionCommand("pearson correlationP");
		pearsonItemPool.addActionListener(this);

		spearmanItem = new JMenuItem("Spearman Correlation");
		spearmanItem.setActionCommand("spearman correlation");
		spearmanItem.addActionListener(this);
		euclideanItem = new JMenuItem("Euclidean distance");
		euclideanItem.setActionCommand("euclidean distance");
		euclideanItem.addActionListener(this);
		canberraItem = new JMenuItem("Canberra distance");
		canberraItem.setActionCommand("canberra distance");
		canberraItem.addActionListener(this);
		manhattanItem = new JMenuItem("Manhattan distance");
		manhattanItem.setActionCommand("manhattan distance");
		manhattanItem.addActionListener(this);
		weightedEuclideanItem = new JMenuItem("Weighted Euclidean distance");
		weightedEuclideanItem.setActionCommand("weighted euclidean distance");
		weightedEuclideanItem.addActionListener(this);
		weightedManhattanItem = new JMenuItem("Weighted Manhattan distance");
		weightedManhattanItem.setActionCommand("weighted manhattan distance");
		weightedManhattanItem.addActionListener(this);
		saveCorrelationItem = new JMenuItem("Keep Previous Correlation");
		saveCorrelationItem.setActionCommand("save correlation");
		saveCorrelationItem.addActionListener(this);
		pairwisePearsonItem = new JMenuItem("Pairwise Pearson Correlation");
		pairwisePearsonItem.setActionCommand("pairwise pearson");
		pairwisePearsonItem.addActionListener(this);
		pairwiseSpearmanItem = new JMenuItem("Pairwise Spearman Correlation");
		pairwiseSpearmanItem.setActionCommand("pairwise spearman");
		pairwiseSpearmanItem.addActionListener(this);

		removeCorrelationMenu = new JMenu("Remove Correlation");

		analyzePopupMenu.add(pearsonItem);
		analyzePopupMenu.add(pearsonItem2);
		analyzePopupMenu.add(pearsonItemPool);
		analyzePopupMenu.add(spearmanItem);
		analyzePopupMenu.add(euclideanItem);
		analyzePopupMenu.add(weightedEuclideanItem);
		analyzePopupMenu.add(manhattanItem);
		analyzePopupMenu.add(weightedManhattanItem);

		analyzePopupMenu.add(saveCorrelationItem);
		analyzePopupMenu.addSeparator();
		analyzePopupMenu.add(removeCorrelationMenu);

		analyzePopupMenu.add(pairwisePearsonItem);
		analyzePopupMenu.add(pairwiseSpearmanItem);
		analyzeMenuButton = new MenuButton("Statistical analysis", theme.getMath(), analyzePopupMenu);
		analyzeMenuButton.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				saveCorrelationItem.setEnabled(myProject.hasLastCorrelation());
				MetaOmTablePanel.this.populateRemoveCorrelationMenu();
			}

		});

		analyzeMenuButton.setToolTipText(
				"Statistically analyze the selected data set against the other sets in the selected list");
		dataToolbar.add(analyzeMenuButton);
		reportButton = new JButton("Finding samples", theme.getReport());
		reportButton.setToolTipText("Generate report");
		reportButton.setActionCommand(REPORT_COMMAND);
		reportButton.addActionListener(this);
		dataToolbar.add(reportButton);
		JPopupMenu infoPopupMenu = new JPopupMenu();
		atgsItem = new JMenuItem("AtGeneSearch");
		atgsItem.setActionCommand(ATGENESEARCH_COMMAND);
		atgsItem.addActionListener(this);
		atgsItem.setToolTipText("Connect to AtGeneSearch for information on all selected genes");
		tairItem = new JMenuItem("TAIR");
		tairItem.setActionCommand(TAIR_COMMAND);
		tairItem.addActionListener(this);
		tairItem.setToolTipText("Connect to TAIR for information on the first selected gene");

		thaleMineItem = new JMenuItem("Araport-ThaleMine");
		thaleMineItem.setActionCommand(ARAPORT_THALEMINE_COMMAND);
		thaleMineItem.addActionListener(this);
		thaleMineItem.setToolTipText("Connect to Araport-ThaleMine for information on the first selected gene");

		jBrowseItem = new JMenuItem("Araport-JBrowse");
		jBrowseItem.setActionCommand(ARAPORT_JBROWSE_COMMAND);
		jBrowseItem.addActionListener(this);
		jBrowseItem.setToolTipText("Connect to Araport-JBrowse for information on the first selected gene");

		infoPopupMenu.add(atgsItem);
		infoPopupMenu.add(tairItem);
		infoPopupMenu.add(thaleMineItem);
		infoPopupMenu.add(jBrowseItem);
		infoButton = new MenuButton("External web applications", theme.getExternalSource(), infoPopupMenu);
		infoButton.setToolTipText("Connect to an external website for more info on the selected genes");
		dataToolbar.add(infoButton);
		String[] listNames = myProject.getGeneListNames();
		Arrays.sort(listNames, new ListNameComparator());
		geneLists = new JList(listNames);
		geneLists.setSelectionMode(0);
		geneLists.setSelectedIndex(0);
		geneLists.addListSelectionListener(this);
		listDeleteButton.setEnabled(false);
		listEditButton.setEnabled(false);
		listRenameButton.setEnabled(false);
		JPanel geneListPanel = new JPanel(new BorderLayout());
		geneListScrollPane = new JScrollPane(geneLists);
		geneListPanel.add(listToolbar, "First");
		geneListPanel.add(geneListScrollPane, "Center");
		Border loweredetched = BorderFactory.createEtchedBorder();
		geneListPanel.setBorder(BorderFactory.createTitledBorder(loweredetched, "Lists"));
		mainModel = new NoneditableTableModel(myProject.getGeneListRowNames(geneLists.getSelectedValue().toString()),
				myProject.getInfoColumnNames());
		filterModel = new FilterableTableModel(mainModel);
		sorter = new TableSorter(filterModel);
		MyComparator comparator = new MyComparator();
		sorter.setColumnComparator(String.class, comparator);
		sorter.setColumnComparator(CorrelationValue.class, comparator);
		sorter.setColumnComparator(null, comparator);
		listDisplay = new StripedTable(sorter);
		sorter.setTableHeader(listDisplay.getTableHeader());
		geneListDisplayPane = new JScrollPane(listDisplay);
		JPanel searchPanel = new JPanel(new BorderLayout());
		searchPanel.add(new JLabel("Filter:"), "Before");
		filterField = new ClearableTextField();
		filterField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 27) {
					filterModel.clearFilter();
					filterField.setText("");
				}
			}
		});
		filterField.getDocument().addDocumentListener(new FilterFieldListener());
		filterField.setDefaultText("Use semicolon (;) for multiple filters");
		searchPanel.add(filterField, "Center");

		try {
			BufferedImage source = ImageIO
					.read(getClass().getResourceAsStream("/resource/tango/22x22/animations/process-working.png"));
			throbber = new MultiFrameImageThrobber(source, 4, 8);
		} catch (IOException e1) {
			throbber = new MetaOmThrobber();
		}
		searchPanel.add(throbber, "After");
		listFromFilterButton = new JButton(">> Add to List");
		listFromFilterButton.setActionCommand("list from filter");
		listFromFilterButton.addActionListener(this);
		listFromFilterButton.setEnabled(false);
		listFromFilterButton.setToolTipText("Export the results of the current filter to a new list");
		dataToolbar.add(new Separator());
		dataToolbar.add(searchPanel);
		dataToolbar.add(listFromFilterButton);
		geneListPanel.setMinimumSize(listToolbar.getPreferredSize());
		listSplitPane = new JSplitPane(1, true, geneListPanel, geneListDisplayPane);
		listSplitPane.setDividerSize(1);
		listPanel.add(dataToolbar, "First");
		listPanel.add(listSplitPane, "Center");
		tabby = new JTabbedPane();
		tabby.addTab("Data", listPanel);
		if (myProject.getMetadataHybrid() != null)
			addExtInfoTab();
		add(tabby, "Center");
		tabby.addChangeListener(this);
		listDisplay.setAutoResizeMode(0);
		sizeColumnsToFit();
		sorter.setSortingStatus(myProject.getDefaultColumn(), 1);
	}

	public void stateChanged(ChangeEvent event) {
		int selectedList = geneLists.getSelectedIndex();
		String[] listNames = myProject.getGeneListNames();
		Arrays.sort(listNames, new ListNameComparator());
		geneLists = new JList(listNames);
		geneLists.addListSelectionListener(this);
		geneLists.setSelectionMode(0);
		if ("delete list".equals(event.getSource())) {
			geneLists.setSelectedIndex(0);
		} else {
			geneLists.setSelectedIndex(selectedList);
		}
		valueChanged(null);
		if (("new correlation".equals(event.getSource())) || ("info column deleted".equals(event.getSource()))) {
			sizeColumnsToFit();
		}
		if ("new correlation".equals(event.getSource())) {
			for (int i = 0; i < listDisplay.getColumnCount(); i++) {
				sorter.setSortingStatus(i, 0);
			}
			sorter.setSortingStatus(0, -1);
		}
		geneListScrollPane.setViewportView(geneLists);
	}

	public void selectList(String listName) {
		geneLists.setSelectedValue(listName, true);
	}

	public void sizeColumnsToFit() {
		for (int col = 0; col < listDisplay.getColumnCount(); col++) {
			TableColumn myCol = listDisplay.getColumnModel().getColumn(col);

			int longest = (myCol.getHeaderValue() + "").length();
			int spacePoint = -1;
			if (listDisplay.getColumnClass(col).equals(CorrelationValue.class)) {
				spacePoint = (myCol.getHeaderValue() + "").indexOf(" ");
				if (spacePoint >= 0)
					longest = spacePoint;
			}
			int longIndex = -1;
			for (int row = 0; row < listDisplay.getRowCount(); row++) {
				if (listDisplay.getValueAt(row, col) != null) {
					String thisData = listDisplay.getValueAt(row, col).toString();
					if (thisData.indexOf(";") >= 0)
						thisData = thisData.substring(0, thisData.indexOf(";"));
					if (thisData.length() > longest) {
						longest = thisData.length();
						longIndex = row;
					}
				}
			}
			String longString;

			if (longIndex >= 0)
				longString = listDisplay.getValueAt(longIndex, col) + "";
			else {
				longString = myCol.getHeaderValue() + "";
				if (spacePoint >= 0)
					longString = longString.substring(0, spacePoint);
			}
			int width = listDisplay.getFontMetrics(listDisplay.getFont()).stringWidth(longString)
					+ 2 * listDisplay.getColumnModel().getColumnMargin() + 10;
			if (width > 300)
				width = 300;
			myCol.setPreferredWidth(width);
		}
	}

	public void valueChanged(ListSelectionEvent event) {
		int[] oldWidths = new int[listDisplay.getColumnCount()];
		for (int x = 0; x < oldWidths.length; x++)
			oldWidths[x] = listDisplay.getColumnModel().getColumn(x).getPreferredWidth();
		mainModel = new NoneditableTableModel(myProject.getGeneListRowNames(geneLists.getSelectedValue().toString()),
				myProject.getInfoColumnNames());
		filterModel = new FilterableTableModel(mainModel);
		sorter = new TableSorter(filterModel);
		listDisplay = new StripedTable(sorter);
		listDisplay.setAutoResizeMode(0);
		sorter.setTableHeader(listDisplay.getTableHeader());
		sorter.setColumnComparator(String.class, new MyComparator());
		sorter.setSortingStatus(myProject.getDefaultColumn(), 1);
		geneListDisplayPane.setViewportView(listDisplay);
		filterField.setText("");
		for (int x = 0; (x < listDisplay.getColumnCount()) && (x < oldWidths.length); x++) {
			listDisplay.getColumnModel().getColumn(x).setPreferredWidth(oldWidths[x]);
		}
		if (geneLists.getSelectedIndex() != 0) {
			listDeleteButton.setEnabled(true);
			listEditButton.setEnabled(true);
			listRenameButton.setEnabled(true);
		} else {
			listDeleteButton.setEnabled(false);
			listEditButton.setEnabled(false);
			listRenameButton.setEnabled(false);
		}
		getTable().requestFocus();
	}

	public void launchAtGeneSearch() {

		callExternalSite("http://metnetweb.gdcb.iastate.edu/AtGeneSearch/index.php?genelist=", "");
	}

	public void launchAraportThaleMine() {

		callExternalSite("http://www.araport.org/locus/", "");
	}

	public void launchAraportJbrowse() {

		callExternalSite("http://www.araport.org/locus/", "browse");
	}

	private void callExternalSite(String url, String option) {
		int[] selected = listDisplay.getSelectedRows();
		String geneList = "";
		boolean found = false;
		for (int x = 0; x < selected.length; x++) {
			for (int y = 0; (y < listDisplay.getColumnCount()) && (!found); y++) {
				if (Utils.isGeneID(listDisplay.getValueAt(selected[x], y) + "")) {
					found = true;
					geneList = listDisplay.getValueAt(selected[x], y) + "";
				}
			}
		}
		if (geneList.equals("")) {
			JOptionPane.showMessageDialog(getParent(), "Unable to find any locus IDs in the selected rows", "Error", 0);
			return;
		}
		String urlString = url + geneList;
		if (option != "")
			urlString = url + geneList + "/" + option;
		try {
			Class.forName("java.awt.Desktop");
			Desktop.getDesktop().browse(new URI(urlString));
			System.out.println("Launched a browser using Desktop");
		} catch (Exception e) {
			try {
				BrowserLauncher launcher = new BrowserLauncher(null);
				BrowserLauncherErrorHandler errorHandler = new BrowserLauncherDefaultErrorHandler();
				BrowserLauncherRunner runner = new BrowserLauncherRunner(launcher, urlString, errorHandler);
				Thread launcherThread = new Thread(runner);
				launcherThread.start();
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(getParent(), "Unable to launch web browser", "Error", 0);
				e2.printStackTrace();
			}
		}
	}

	public void launchTAIR() {
		int[] selected = listDisplay.getSelectedRows();
		String geneList = "";
		boolean found = false;
		for (int x = 0; x < selected.length; x++) {
			for (int y = 0; (y < listDisplay.getColumnCount()) && (!found); y++) {
				if (Utils.isGeneID(listDisplay.getValueAt(selected[x], y) + "")) {
					found = true;
					geneList = listDisplay.getValueAt(selected[x], y) + "";
				}
			}
		}
		if (geneList.equals("")) {
			JOptionPane.showMessageDialog(getParent(), "Unable to find any locus IDs in the selected rows", "Error", 0);
			return;
		}
		String urlString = "http://www.arabidopsis.org/servlets/Search?type=general&name=" + geneList
				+ "&action=detail&method=4&sub_type=gene";
		try {
			Class.forName("java.awt.Desktop");
			Desktop.getDesktop().browse(new URI(urlString));
			System.out.println("Launched a browser using Desktop");
		} catch (Exception e) {
			try {
				BrowserLauncher launcher = new BrowserLauncher(null);
				BrowserLauncherErrorHandler errorHandler = new BrowserLauncherDefaultErrorHandler();
				BrowserLauncherRunner runner = new BrowserLauncherRunner(launcher, urlString, errorHandler);
				Thread launcherThread = new Thread(runner);
				launcherThread.start();
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(getParent(), "Unable to launch web browser", "Error", 0);
				e2.printStackTrace();
			}
		}
	}

	public void makeReport() {
		String thisGene = getSelectedGeneName();
		if (thisGene == null) {
			JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "You must select a row!",
					"Error: no row selected", 0);
			return;
		}
		JPanel optionPanel = new JPanel();
		JSpinner spinner = new JSpinner();
		spinner.setValue(Integer.valueOf(100));
		JFormattedTextField valueField = new JFormattedTextField(new Double(100.0D));
		valueField.setColumns(7);
		JLabel label = new JLabel("Find all chips with expression level over:");
		optionPanel.add(label);
		optionPanel.add(valueField);
		int result = JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(), optionPanel, thisGene + " Report", 2,
				-1);
		if (result != 0)
			return;

		double target = Double.parseDouble(valueField.getValue() + "");
		String chipList = thisGene + "'s expression level is above " + target + " in the following samples:";

		try {
			double[] values = myProject.getAllData(getSelectedGeneIndex());
			for (int i = 0; i < values.length; i++)
				try {
					if (values[i] > target)
						chipList = chipList + "\n" + myProject.getDataColumnHeader(i);
				} catch (NumberFormatException nfe) {
					System.err.println("value at " + i + " is not a number");
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
		JInternalFrame resultWindow = new JInternalFrame(thisGene + " Report", true, true, true, true);
		resultWindow.putClientProperty("JInternalFrame.frameType", "normal");
		JTextArea text = new JTextArea(chipList);
		resultWindow.getContentPane().add(new JScrollPane(text));
		resultWindow.setSize(400, 400);
		MetaOmGraph.getDesktop().add(resultWindow);
		resultWindow.setVisible(true);
	}

	public void graphSelectedRows() {
		new MetaOmChartPanel(getSelectedRowsInList(), myProject.getDefaultXAxis(), myProject.getDefaultYAxis(),
				myProject.getDefaultTitle(), myProject.getColor1(), myProject.getColor2(), myProject)
						.createInternalFrame();
	}

	public void graphSelectedList() {
		int[] selected = myProject.getGeneListRowNumbers((String) geneLists.getSelectedValue());
		new MetaOmChartPanel(selected, myProject.getDefaultXAxis(), myProject.getDefaultYAxis(),
				myProject.getDefaultTitle(), myProject.getColor1(), myProject.getColor2(), myProject)
						.createInternalFrame();
	}

	public void graphFilteredList() {
		int[] trueRows = getAllTrueRows();
		if (geneLists.getSelectedValue().equals("Complete List")) {
			new MetaOmChartPanel(trueRows, myProject.getDefaultXAxis(), myProject.getDefaultYAxis(),
					myProject.getDefaultTitle(), myProject.getColor1(), myProject.getColor2(), myProject)
							.createInternalFrame();
		} else {
			int[] entries = myProject.getGeneListRowNumbers((String) geneLists.getSelectedValue());
			int[] selected = new int[listDisplay.getSelectedRowCount()];
			for (int x = 0; x < selected.length; x++) {
				selected[x] = entries[trueRows[x]];
			}

			new MetaOmChartPanel(selected, myProject.getDefaultXAxis(), myProject.getDefaultYAxis(),
					myProject.getDefaultTitle(), myProject.getColor1(), myProject.getColor2(), myProject)
							.createInternalFrame();
		}
	}

	public void deleteSelectedList() {
		myProject.deleteGeneList(geneLists.getSelectedValue().toString());
	}

	public String getSelectedGeneName() {
		if (listDisplay.getSelectedRow() != -1) {
			return myProject.getRowNames()[myProject
					.getGeneListRowNumbers(geneLists.getSelectedValue().toString())[getTrueSelectedRow()]][myProject
							.getDefaultColumn()].toString();
		}
		return null;
	}

	public int getSelectedGeneIndex() {
		String activeList = geneLists.getSelectedValue().toString();
		int selectedRow = sorter.modelIndex(listDisplay.getSelectedRow());
		return myProject.getGeneListRowNumbers(activeList)[selectedRow];
	}

	public void addExtInfoTab() {

		/*
		 * if (myProject.getMetadata() == null) return; // remove existing new metadata
		 * panel urmi for (int x = 0; x < tabby.getTabCount(); x++) { if
		 * (tabby.getTitleAt(x).equals("Metadata")) { tabby.remove(x); break; } }
		 */
		// JOptionPane.showMessageDialog(null, "adding tabs");
		if (myProject.getMetadataHybrid() == null) {
			// JOptionPane.showMessageDialog(null, "mh is null");
			return;
		}
		for (int x = 0; x < tabby.getTabCount(); x++) {
			if (tabby.getTitleAt(x).equals("MetadataTree")) {
				tabby.remove(x);
				break;
			}
		}

		for (int x = 0; x < tabby.getTabCount(); x++) {
			if (tabby.getTitleAt(x).equals("MetadataTable")) {
				tabby.remove(x);
				break;
			}
		}

		// extInfoPanel = new MetadataPanel(myProject.getMetadata());
		// tabby.addTab("Metadata", extInfoPanel);
		/**
		 * @author urmi Added new tabs to display metadata from new MetadataHybrid class
		 */

		MetadataHybrid mdhObj = MetaOmGraph.getActiveProject().getMetadataHybrid();
		if (mdhObj != null) {
			extInfoPanel2 = new MetadataTreeDisplayPanel(mdhObj.getXMLRoot());
			tabby.addTab("MetadataTree", extInfoPanel2);
			// build the treemap to map data col index to nodes in Jtree displaying data
			// extInfoPanel2.buildTreemaptoNode();
			mdtablepanel = new MetadataTableDisplayPanel(mdhObj.getMetadataCollection());
			tabby.addTab("MetadataTable", mdtablepanel);
		}

		// extInfoPanel.getSplitPane().setDividerLocation(0.5D);
	}

	/**
	 * Added boolean param to select parent node urmi
	 * 
	 * @param col
	 * @param parent
	 */
	public void selectNode(int col, boolean parent) {
		// TreeNode[] pathNodes = myProject.getMetadata().getNodeForCol(col).getPath();
		/**
		 * @author urmi MetadataTreeDisplayPanel has a treemap which maps col index in
		 *         data file to a tree node in the display tree this tree map is based
		 *         on the knownCols tree map from MetadataHybrid class
		 */

		// if no metadata then return
		if (MetaOmGraph.getActiveProject().getMetadataHybrid() == null) {
			return;
		}
		TreeNode[] pathNodes = null;
		if (parent) {
			if (extInfoPanel2.getColstoTreeMap().containsKey(col)) {
				DefaultMutableTreeNode p = (DefaultMutableTreeNode) extInfoPanel2.getColstoTreeMap().get(col)
						.getParent();
				pathNodes = p.getPath();
			} else {
				return;
			}

		} else {
			if (extInfoPanel2.getColstoTreeMap().containsKey(col)) {
				pathNodes = extInfoPanel2.getColstoTreeMap().get(col).getPath();
			} else {
				return;
			}

		}
		if (pathNodes != null) {
			// JTree tree = extInfoPanel.getTree();
			JTree tree = extInfoPanel2.getTree();
			TreePath path = new TreePath(pathNodes);
			tree.setSelectionPath(path);
			Rectangle rect = tree.getPathBounds(path);

			if (rect == null) {
				// JOptionPane.showMessageDialog(null, "Please remove a text on the search text
				// field.", "MetaOmGraph", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			/*
			 * long currtime=System.currentTimeMillis(); JOptionPane.showMessageDialog(null,
			 * "X:"+rect.x+" Y:"+rect.y+" time"+currtime); MouseEvent me = new
			 * MouseEvent(tree, 500, currtime, 0, rect.x, rect.y, 1, true);
			 * for(MouseListener ml: tree.getMouseListeners()){ ml.mousePressed(me); }
			 */

			rect.width += rect.x;
			rect.x = 0;
			tree.scrollRectToVisible(rect);
			tabby.setSelectedComponent(extInfoPanel2);
		}
	}

	public void selecTabRow(String value) {
		// select first val
		java.util.List<String> hits = MetaOmGraph.getActiveProject().getMetadataHybrid().searchByValue(value,
				MetaOmGraph.getActiveProject().getMetadataHybrid().getDataColName(), true, false, true);
		// JOptionPane.showMessageDialog(null, "hits:"+hits.toString());

		///// set selected rows /////////
		JTable tab = mdtablepanel.getTable();
		DefaultTableModel model = (DefaultTableModel) tab.getModel();
		// tab.setRowSelectionInterval(0, 0);
		tabby.setSelectedComponent(mdtablepanel);
	}

	public void setExtInfoDividerPos(double pos) {
		if (extInfoPanel2 != null) {
			extInfoPanel2.getSplitPane().setDividerLocation(pos);
		}
	}

	public class ListNameComparator implements Comparator<String> {
		public ListNameComparator() {
		}

		public int compare(String o1, String o2) {
			if ((!(o1 instanceof String)) || (!(o2 instanceof String)))
				return 0;

			if (o1.equals("Complete List"))
				return -1;
			if (o2.equals("Complete List"))
				return 1;
			return o1.toLowerCase().compareTo(o2.toLowerCase());
		}
	}

	public void actionPerformed(ActionEvent e) {
		if ("new list".equals(e.getActionCommand())) {
			CreateListFrame clf = new CreateListFrame(myProject);

			clf.setSize(MetaOmGraph.getMainWindow().getWidth() / 2, MetaOmGraph.getMainWindow().getHeight() / 2);
			clf.setResizable(true);
			clf.setMaximizable(true);
			clf.setIconifiable(true);
			clf.setClosable(true);
			clf.setTitle("Create New List");
			MetaOmGraph.getDesktop().add(clf);
			clf.setVisible(true);
			return;
		}
		if ("edit list".equals(e.getActionCommand())) {
			CreateListFrame clf = new CreateListFrame(myProject, (String) geneLists.getSelectedValue());
			clf.setSize(MetaOmGraph.getMainWindow().getWidth() / 2, MetaOmGraph.getMainWindow().getHeight() / 2);
			clf.setResizable(true);
			clf.setMaximizable(true);
			clf.setIconifiable(true);
			clf.setClosable(true);
			clf.setTitle("Edit List");
			MetaOmGraph.getDesktop().add(clf);
			clf.setVisible(true);
			return;
		}
		if ("rename list".equals(e.getActionCommand())) {
			myProject.renameGeneList(geneLists.getSelectedValue() + "", null);
			return;
		}
		if ("delete list".equals(e.getActionCommand())) {
			int result = JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(),
					"Are you sure you want to delete the list '" + geneLists.getSelectedValue().toString() + "'?",
					"Confirm", 0, 3);
			if (result == 0)
				deleteSelectedList();
			return;
		}
		if (GRAPH_LIST_COMMAND.equals(e.getActionCommand())) {
			graphSelectedList();
			return;
		}
		if (GRAPH_SELECTED_COMMAND.equals(e.getActionCommand())) {
			graphSelectedRows();
			return;
		}
		if (GRAPH_FILTERED_COMMAND.equals(e.getActionCommand())) {
			graphFilteredList();
			return;
		}
		if ("plotrepsold".equals(e.getActionCommand())) {
			// if no reps find reps
			if (!myProject.getMetadata().hasRepGroups()) {
				myProject.getMetadata().findReps();
			}
			// plot reps
			int[] selected = getSelectedRowsInList();
			ArrayList<double[]> myVals = new ArrayList();
			ArrayList<double[]> myStddevs = new ArrayList();
			ArrayList<int[]> repCounts = new ArrayList();
			String[] sampleNames = null;
			for (int thisRow : selected) {
				try {
					RepAveragedData result = myProject.getRepAveragedData(myProject, thisRow);
					myVals.add(result.values);
					myStddevs.add(result.stdDevs);
					repCounts.add(result.repCounts);
					sampleNames = result.repGroupNames;
					if (sampleNames.length == 0) {
						JOptionPane.showMessageDialog(this, "There are no sample names!");

						return;
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			MetaOmChartPanel ob = new MetaOmChartPanel(getSelectedRowsInList(), myProject.getDefaultXAxis(),
					myProject.getDefaultYAxis(), myProject.getDefaultTitle(), myProject.getColor1(),
					myProject.getColor2(), myProject, myVals, myStddevs, repCounts, sampleNames, sampleNames, true);
			ob.repFlag = true;
			ob.createInternalFrame();
			/*
			 * new MetaOmChartPanel(getSelectedRowsInList(), myProject.getDefaultXAxis(),
			 * myProject.getDefaultYAxis(), myProject.getDefaultTitle(),
			 * myProject.getColor1(), myProject.getColor2(), myProject, myVals, myStddevs,
			 * repCounts, sampleNames).createInternalFrame();
			 */
			return;
		}
		/**
		 * @author urmi Change plot reps function by default reps are all data cols
		 *         under same parent
		 */
		if ("plot reps".equals(e.getActionCommand())) {
			// if no reps find reps
			if (myProject.getMetadataHybrid() == null) {
				JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "No data to calculate rep information...");
				return;
			}
			// plot reps
			int[] selected = getSelectedRowsInList();
			ArrayList<double[]> myVals = new ArrayList();
			ArrayList<double[]> myStddevs = new ArrayList();
			ArrayList<int[]> repCounts = new ArrayList();
			String[] sampleNames = null;
			String[] groupNames = null;
			for (int thisRow : selected) {
				ReplicateGroups result = new ReplicateGroups(myProject.getMetadataHybrid().getDefaultRepsMap(),
						thisRow);
				myVals.add(result.getValues());
				myStddevs.add(result.getStdDev());
				repCounts.add(result.getRepCounts());
				sampleNames = result.getSampnames();
				groupNames = result.getGroupnames();
				// sampleNames= MetaOmGraph.getActiveProject().getDataColumnHeaders();
				// sampleNames = result.getGroupnames();
				// JOptionPane.showMessageDialog(null, "sampnames:" +
				// Arrays.toString(sampleNames));
				// JOptionPane.showMessageDialog(null, "gnames:" +
				// Arrays.toString(result.getGroupnames()));
				// JOptionPane.showMessageDialog(null, "new data:" +
				// Arrays.toString(result.getValues()));
				// JOptionPane.showMessageDialog(null, "new stddev:" +
				// Arrays.toString(result.getStdDev()));
				if (sampleNames.length == 0) {
					JOptionPane.showMessageDialog(this, "There are no sample names!");
					return;
				}
			}

			// set the replicate names in project
			// MetaOmGraph.getActiveProject().setCurrentRepNames(sampleNames);
			// JOptionPane.showMessageDialog(null, "defcol"+myProject.getDefaultXAxis());
			MetaOmChartPanel ob = new MetaOmChartPanel(getSelectedRowsInList(), "Groups", myProject.getDefaultYAxis(),
					myProject.getDefaultTitle(), myProject.getColor1(), myProject.getColor2(), myProject, myVals,
					myStddevs, repCounts, groupNames, sampleNames, true);
			ob.createInternalFrame();
			return;
		}

		if ("make boxplot".equals(e.getActionCommand())) {
			JPanel boxPlot = BoxPlotter.getSampleBoxPlot(myProject, getSelectedRowsInList());
			String title = "Box Plot";
			MetaOmGraph.addInternalFrame(boxPlot, title);
			return;
		}
		if ("col boxplot".equals(e.getActionCommand())) {
			BoxPlotter.showColumnBoxPlot(myProject);
			return;
		}
		if ("create histogram".equals(e.getActionCommand())) {
			try {
				int[] rows = getSelectedRowsInList();

				JPanel messagePanel = new JPanel();
				SpinnerNumberModel model = new SpinnerNumberModel(Integer.valueOf(50), Integer.valueOf(1), null,
						Integer.valueOf(1));
				JSpinner spinner = new JSpinner(model);
				messagePanel.add(new JLabel("Number of bins:"));
				messagePanel.add(spinner);

				spinner.setMinimumSize(new Dimension(75, spinner.getMinimumSize().height));
				spinner.setPreferredSize(new Dimension(75, spinner.getPreferredSize().height));
				int result = JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(), messagePanel, "Histogram", 2);
				if (result != 0)
					return;

				JPanel histogram = GeneHistogram.makeHistogram(myProject, rows,
						Integer.parseInt(spinner.getValue() + ""));

				if (histogram == null)
					return;

				MetaOmGraph.addInternalFrame(histogram, "Histogram");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
		if (REPORT_COMMAND.equals(e.getActionCommand())) {
			makeReport();
			return;
		}
		if (ATGENESEARCH_COMMAND.equals(e.getActionCommand())) {
			launchAtGeneSearch();
			return;
		}
		if ("list from filter".equals(e.getActionCommand())) {
			makeListFromFilter();
			return;
		}
		if (("edit list".equals(e.getActionCommand())) || ("new list".equals(e.getActionCommand()))) {
			String editMe = null;
			if ("edit list".equals(e.getActionCommand())) {
				editMe = geneLists.getSelectedValue() + "";
			}
			CreateListFrame clf = new CreateListFrame(myProject, editMe);
			clf.setSize(MetaOmGraph.getMainWindow().getWidth() / 2, MetaOmGraph.getMainWindow().getHeight() / 2);
			clf.setResizable(true);
			clf.setMaximizable(true);
			clf.setIconifiable(true);
			clf.setClosable(true);
			clf.setTitle("Edit List");
			MetaOmGraph.getDesktop().add(clf);
			clf.setVisible(true);
		}
		if (TAIR_COMMAND.equals(e.getActionCommand())) {
			launchTAIR();
			return;
		}
		if (ARAPORT_JBROWSE_COMMAND.equals(e.getActionCommand())) {
			launchAraportJbrowse();
			// call metadataviewer
			// MetaOmGraph.getActiveProject().returneditor().displayEditor();
			DisplayMetadataEditor ob = new DisplayMetadataEditor();
			ob.setVisible(true);
			return;
		}
		if (ARAPORT_THALEMINE_COMMAND.equals(e.getActionCommand())) {
			launchAraportThaleMine();
			return;
		}
		if (("pearson correlation".equals(e.getActionCommand()))
				|| ("pearson correlationP".equals(e.getActionCommand()))
				|| ("pearson correlation2".equals(e.getActionCommand()))
				|| ("spearman correlation".equals(e.getActionCommand()))
				|| ("euclidean distance".equals(e.getActionCommand()))
				|| ("canberra distance".equals(e.getActionCommand()))
				|| ("manhattan distance".equals(e.getActionCommand()))
				|| ("weighted euclidean distance".equals(e.getActionCommand()))
				|| ("weighted manhattan distance".equals(e.getActionCommand()))) {
			if (listDisplay.getSelectedRowCount() != 1) {
				JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "You must select a row to analyze!", "Error",
						0);
				return;
			}
			// JOptionPane.showMessageDialog(null, "selected
			// count="+listDisplay.getSelectedRowCount());
			// JOptionPane.showMessageDialog(null, "selected target="+getTrueSelectedRow()+"
			// name="+getSelectedGeneName());
			// JOptionPane.showMessageDialog(null, "selected
			// target="+getTrueSelectedRows()+" name="+getSelectedGeneName());
			// JOptionPane.showMessageDialog(null,
			// "genelist:"+geneLists.getSelectedValue().toString());

			int target = getTrueSelectedRow();
			String targetName = getSelectedGeneName();
			if (myProject.hasLastCorrelation()) {
				int result = JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(),
						"Do you want to keep the results of the last correlation? (If no, the new results will overwrite the old)");
				if (result == 2)
					return;
				if ((result == 0) && (!keepLastCorrelation()))
					return;
			}

			String name = (String) JOptionPane.showInputDialog(MetaOmGraph.getDesktop(),
					"Please enter a name for the correlation", "Store Correlation", 3, null, null,
					targetName + " Correlation");
			if (name == null)
				return;

			name = name.trim();
			try {
				if ("pearson correlation".equals(e.getActionCommand())) {
					long startTime = System.currentTimeMillis();
					MetaOmAnalyzer.doAnalysis(myProject, geneLists.getSelectedValue().toString(), target, name, 1);
					// MetaOmAnalyzer.doComputation(myProject,
					// geneLists.getSelectedValue().toString(), target, name, 1);
					long stopTime = System.currentTimeMillis();
					long elapsedTime = stopTime - startTime;
					// JOptionPane.showMessageDialog(null, "Time elapsed newmethod:" + elapsedTime);

				} else if ("pearson correlationP".equals(e.getActionCommand())) {
					JOptionPane.showMessageDialog(null, "Pooled corr");

					// by default use parent of data column to get groups
					// or display all parents of data column as option
					TreeMap<String, List<Integer>> groupsMap = myProject.getMetadataHybrid().getDefaultRepsMap();
					//JOptionPane.showMessageDialog(null, "reps map:" + groupsMap.toString());

					// for each row in data file calculate corr coeff within groups
					// get entries in a gene list for complete list get all
					final int[] entries = myProject.getGeneListRowNumbers(geneLists.getSelectedValue().toString());
					double[] sourceData = myProject.getIncludedData(entries[target]);
					

					// create list of source data into groups
					List<double[]> sourceGrouped = new ArrayList<>();
					for (Map.Entry<String, List<Integer>> entry : groupsMap.entrySet()) {
						String key = entry.getKey();
						List<Integer> value = entry.getValue();
						double[] thisVals = new double[value.size()];
						for (int i = 0; i < value.size(); i++) {
							thisVals[i] = sourceData[value.get(i)];
						}
						sourceGrouped.add(thisVals);
						// JOptionPane.showMessageDialog(null,"gr:"+entry+"
						// vals:"+Arrays.toString(thisVals));
					}
					
					
					
					//do for each row compute correlations in groups
					int j = 0;
					do {

						double[] data = myProject.getIncludedData(entries[j]);
						//split data into groups
						List<double[]> dataGrouped = new ArrayList<>();
						for (Map.Entry<String, List<Integer>> entry : groupsMap.entrySet()) {
							String key = entry.getKey();
							List<Integer> value = entry.getValue();
							double[] thisVals = new double[value.size()];
							for (int i = 0; i < value.size(); i++) {
								thisVals[i] = data[value.get(i)];
							}
							dataGrouped.add(thisVals);
						}
						
						//JOptionPane.showMessageDialog(null, "this da:"+dataGrouped.toString());
						
						try {
							CorrelationGrouped ob =new CorrelationGrouped(sourceGrouped, dataGrouped, 6);
							double thisrVal=ob.doComputation();
							JOptionPane.showMessageDialog(null, "thisrval:"+thisrVal);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, "InterruptedException!");
							e1.printStackTrace();
						} catch (ExecutionException e2) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, "ExecutionException!");
							e2.printStackTrace();
						}

						// JOptionPane.showMessageDialog(null, "Corrvals for:"+j+"
						// :"+Arrays.toString(result));

						j++;
						if (j >= entries.length)
							break;
					} while (j < entries.length);
					
					JOptionPane.showMessageDialog(null, "done");

				} else if ("pearson correlation2".equals(e.getActionCommand())) {
					JOptionPane.showMessageDialog(null, "PC2");
					// first run regular method to get correlation values
					MetaOmAnalyzer.doAnalysis(myProject, geneLists.getSelectedValue().toString(), target, name, 1);
					//last correlation col is 0
					//check if last corr was canceled
					JOptionPane.showMessageDialog(null, "corrcols:"+myProject.getCorrelationColumns().toString());
					// get significance using permutation test
					final int[] entries = myProject.getGeneListRowNumbers(geneLists.getSelectedValue().toString());
					double[] sourceData = myProject.getIncludedData(entries[target]);
					JOptionPane.showMessageDialog(null, " TO shuff Sourcdata:" + Arrays.toString(sourceData));
					int _N = 100; // num permutations
					int _P = 8; // num threads
					// do _N permutations
					List<Double> sourceDataList = new ArrayList<>();
					for (double d : sourceData) {
						sourceDataList.add(d);
					}

					List<double[]> shuffList = new ArrayList<double[]>();
					// add original data to calculate original correlation val which will be at
					// index 0
					shuffList.add(sourceData);
					// shuffle _N time to create a list of shuffled lists
					for (int i = 0; i < _N; i++) {
						java.util.Collections.shuffle(sourceDataList);
						double[] tempArr = new double[sourceData.length];
						int k = 0;
						for (double d : sourceDataList) {
							tempArr[k++] = d;
						}
						shuffList.add(tempArr);
					}
					
					
					final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(), "Analyzing...","", 0L, entries.length, true);
					SwingWorker analyzeWorker = new SwingWorker() {
						boolean errored = false;
						List<Double> pvres = new ArrayList<>();
						public Object construct() {
							try {
								// for each data row do
								// Number[] result = null;
								int j = 0;
								
								do {
									progress.setProgress(j);
									double[] data = myProject.getIncludedData(entries[j]);

									// for each row calculate p-value
									ComputePval ob = new ComputePval(data, shuffList, _P);
									try {
										double pv = ob.doComputation();
										pvres.add(pv);
										// JOptionPane.showMessageDialog(null, "P val for row:"+j+" is:"+pv);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (ExecutionException e2) {
										// TODO Auto-generated catch block
										e2.printStackTrace();
									}

									// JOptionPane.showMessageDialog(null, "Corrvals for:"+j+"
									// :"+Arrays.toString(result));

									j++;
									if (j >= entries.length)
										break;
								} while (!progress.isCanceled());

							} catch (IOException ioe) {
								JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Error reading project data",
										"IOException", 0);
								ioe.printStackTrace();
								progress.dispose();
								errored = true;
								return null;
							} catch (ArrayIndexOutOfBoundsException oob) {
								progress.dispose();
								errored = true;
								return null;
							}
							return null;
						}

						public void finished() {
							if ((!progress.isCanceled()) && (!errored)) {
							//	project.setLastCorrelation(result, name);
								keepLastCorrelation();
								myProject.setLastPval(pvres.toArray(new Number[pvres.size()]), "pval");
							}
							progress.dispose();
						}
					};
					analyzeWorker.start();
					progress.setVisible(true);

					
					
					// for each data row do
					// Number[] result = null;
					/*int j = 0;
					List<Double> pvres = new ArrayList<>();
					do {

						double[] data = myProject.getIncludedData(entries[j]);

						// for each row calculate p-value
						ComputePval ob = new ComputePval(data, shuffList, _P);
						try {
							double pv = ob.doComputation();
							pvres.add(pv);
							// JOptionPane.showMessageDialog(null, "P val for row:"+j+" is:"+pv);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ExecutionException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}

						// JOptionPane.showMessageDialog(null, "Corrvals for:"+j+"
						// :"+Arrays.toString(result));

						j++;
						if (j >= entries.length)
							break;
					} while (j < entries.length);

					JOptionPane.showMessageDialog(null, "Done");
					JOptionPane.showMessageDialog(null, "pvsre:" + Arrays.toString(pvres.toArray(new Number[pvres.size()])));
					
					keepLastCorrelation();
					myProject.setLastPval(pvres.toArray(new Number[pvres.size()]), "pval");
					*/
				} else if ("spearman correlation".equals(e.getActionCommand())) {
					MetaOmAnalyzer.doAnalysis(myProject, geneLists.getSelectedValue().toString(), target, name, 2);
				} else if ("euclidean distance".equals(e.getActionCommand())) {
					MetaOmAnalyzer.doAnalysis(myProject, geneLists.getSelectedValue().toString(), target, name, 4);
				} else if ("canberra distance".equals(e.getActionCommand())) {
					MetaOmAnalyzer.doAnalysis(myProject, geneLists.getSelectedValue().toString(), target, name, 3);
				} else if ("manhattan distance".equals(e.getActionCommand())) {
					MetaOmAnalyzer.doAnalysis(myProject, geneLists.getSelectedValue().toString(), target, name, 5);
				} else if ("weighted euclidean distance".equals(e.getActionCommand())) {
					MetaOmAnalyzer.doAnalysis(myProject, geneLists.getSelectedValue().toString(), target, name, 6);
				} else if ("weighted manhattan distance".equals(e.getActionCommand())) {
					MetaOmAnalyzer.doAnalysis(myProject, geneLists.getSelectedValue().toString(), target, name, 7);
				}
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Error reading project data", "IOException",
						0);
				ioe.printStackTrace();
			}
			return;
		}
		if ("save correlation".equals(e.getActionCommand())) {
			keepLastCorrelation();
			return;
		}
		if ("remove correlation".equals(e.getActionCommand())) {
			if (!(e.getSource() instanceof JMenuItem))
				return;

			JMenuItem source = (JMenuItem) e.getSource();
			if (JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(),
					"Are you sure you want to delete " + source.getText() + "?", "Delete", 0) == 0) {
				myProject.deleteInfoColumn(Integer.parseInt(source.getName()));
			}
			return;
		}
		if ("remove all correlations".equals(e.getActionCommand())) {
			if (JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(),
					"Are you sure you want to delete EVERY stored correlation?", "Delete ALL", 0) == 0) {
				ArrayList<Integer> corrCols = myProject.getCorrelationColumns();
				for (int i = corrCols.size() - 1; i >= 0; i--) {
					myProject.deleteInfoColumn(corrCols.get(i).intValue());
				}
			}
			return;
		}
		if ("Exclude samples".equals(e.getActionCommand())) {
			MetaOmAnalyzer.showExcludeDialog(myProject, MetaOmGraph.getMainWindow());
			return;
		}
		if (("pairwise pearson".equals(e.getActionCommand())) || ("pairwise spearman".equals(e.getActionCommand()))) {
			String[] names = myProject.getInfoColumnNames();
			String[] options = new String[names.length + 1];
			options[0] = "Row Number";
			System.arraycopy(names, 0, options, 1, names.length);

			Object result = JOptionPane.showInputDialog(this, "Which identifiers should be used for the result?",
					"Pairwise Pearson Correlation", 3, null, options, options[0]);
			System.out.println("Result=" + result);
			if (result == null)
				return;

			int nameCol = -100;
			for (int i = 0; (i < options.length) && (nameCol < 0); i++) {
				if (options[i].equals(result.toString())) {
					nameCol = i - 1;
				}
			}

			if ("pairwise pearson".equals(e.getActionCommand())) {
				MetaOmAnalyzer.pairwise(myProject, geneLists.getSelectedValue().toString(), nameCol, 1);
			} else {
				MetaOmAnalyzer.pairwise(myProject, geneLists.getSelectedValue().toString(), nameCol, 2);
			}

			return;
		}
	}

	private void makeListFromFilter() {
		String filterText = filterField.getText();
		int[] entries = new int[listDisplay.getRowCount()];
		for (int x = 0; x < entries.length; x++) {
			entries[x] = getTrueRow(x);
		}
		myProject.addGeneList(filterText, entries, true);
	}

	public int getTrueRow(int row) {
		return filterModel.getUnfilteredRow(sorter.modelIndex(row));
	}

	public int[] getAllTrueRows() {
		int[] allRows = new int[filterModel.getRowCount()];
		for (int x = 0; x < allRows.length; allRows[x] = (x++)) {
		}

		return filterModel.getUnfilteredRows(allRows);
	}

	public int getTrueSelectedRow() {
		return filterModel.getUnfilteredRow(sorter.modelIndex(listDisplay.getSelectedRow()));
	}

	public int[] getTrueSelectedRows() {
		int[] result = new int[listDisplay.getSelectedRowCount()];
		int[] falseRows = listDisplay.getSelectedRows();
		for (int i = 0; i < result.length; i++) {
			result[i] = filterModel.getUnfilteredRow(sorter.modelIndex(falseRows[i]));
		}

		return result;
	}

	public int getSelectedRowInList() {
		return myProject.getGeneListRowNumbers(getSelectedListName())[getTrueSelectedRow()];
	}

	public int[] getSelectedRowsInList() {
		int[] selected = getTrueSelectedRows();
		int[] result = new int[selected.length];
		int[] entries = myProject.getGeneListRowNumbers(getSelectedListName());
		for (int i = 0; i < result.length; i++) {
			result[i] = entries[selected[i]];
		}
		return result;
	}

	public TableSorter getSorter() {
		return sorter;
	}

	public JTable getTable() {
		return listDisplay;
	}

	public void selectRows(Collection<Integer> rows) {
		for (Iterator localIterator = rows.iterator(); localIterator.hasNext();) {
			int i = ((Integer) localIterator.next()).intValue();
			listDisplay.addRowSelectionInterval(i, i);
		}
	}

	public void selectValues(String[] values) {
		ArrayList<Integer> hits = new ArrayList();
		for (int row = 0; row < listDisplay.getRowCount(); row++) {
			for (int col = 0; col < listDisplay.getColumnCount(); col++) {
				String thisValue = listDisplay.getValueAt(row, col) + "".toLowerCase();
				for (String findMe : values) {
					findMe = findMe.toLowerCase();
					if (thisValue.indexOf(findMe) >= 0) {
						hits.add(Integer.valueOf(row));
					}
				}
			}
		}
		selectRows(hits);
	}

	public void applyFilter(String[] values) {
		if ((values == null) || (values.length <= 0)) {
			filterField.setText("");
			filterModel.clearFilter();
		} else {
			String singleValue = "";
			for (String thisValue : values) {
				singleValue = singleValue + thisValue + ";";
			}
			filterField.setText(singleValue);
			filterModel.applyFilter(values);
		}
	}

	public void applyFilter(String value) {
		filterField.setText(value);
		filterModel.applyFilter(value);
	}

	private class MyComparator implements Comparator {
		private MyComparator() {
		}

		public int compare(Object o1, Object o2) {
			if ((o1 == null) && (o2 == null))
				return 0;
			if (o1 == null)
				return 1;
			if (o2 == null)
				return -1;
			if (("".equals(o1)) && ("".equals(o2)))
				return 0;
			if ("".equals(o1))
				return 1;
			if ("".equals(o2))
				return -1;
			if (((o1 instanceof CorrelationValue)) && ((o2 instanceof CorrelationValue))) {
				int result = ((CorrelationValue) o1).compareTo(o2);
				return result;
			}
			if (((o1 instanceof String)) && ((o2 instanceof String))) {
				return ((String) o1).toLowerCase().compareTo(((String) o2).toLowerCase());
			}

			return (o1 + "").compareTo(o2 + "");
		}
	}

	private class FilterFieldListener implements DocumentListener, ActionListener {
		Timer t;

		public FilterFieldListener() {
			t = new Timer(300, this);
			t.setRepeats(false);
		}

		public void doChange() {
			t.restart();
			if (!Throbber.isAnimating()) {
				throbber.start();
			}
			if (filterField.getText().trim().equals("")) {
				listFromFilterButton.setEnabled(false);
			}
		}

		public void insertUpdate(DocumentEvent e) {
			doChange();
		}

		public void removeUpdate(DocumentEvent e) {
			doChange();
		}

		public void changedUpdate(DocumentEvent e) {
		}

		public void actionPerformed(ActionEvent e) {
			filterModel.applyFilter(filterField.getText().trim());
			throbber.stop();
			boolean success = filterModel.getRowCount() != 0;
			listFromFilterButton.setEnabled((success) && (!filterField.getText().trim().equals("")));
			plotFilterItem.setEnabled((success) && (!filterField.getText().trim().equals("")));
			Utils.setSearchFieldColors(filterField, success);
		}
	}

	public CorrelationValue[] getLastCorrelation() {
		return lastCorrelation;
	}

	public void setLastCorrelation(CorrelationValue[] lastCorrelation) {
		this.lastCorrelation = lastCorrelation;
		mainModel.appendColumn(lastCorrelation, "Correlation");
		System.out.println("Appended!");
	}

	public boolean keepLastCorrelation() {
		if (!myProject.hasLastCorrelation()) {
			throw new NullPointerException("No last correlation to save");
		}
		myProject.keepLastCorrelation();
		return true;
	}

	public JTabbedPane getTabbedPane() {
		return tabby;
	}

	public String getSelectedListName() {
		return geneLists.getSelectedValue() + "";
	}

	private void populateRemoveCorrelationMenu() {
		ArrayList<Integer> colList = myProject.getCorrelationColumns();
		if ((colList == null) || (colList.size() == 0)) {
			removeCorrelationMenu.setEnabled(false);
			return;
		}
		removeCorrelationMenu.removeAll();
		JMenuItem[] items = new JMenuItem[colList.size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = new JMenuItem(myProject.getInfoColumnNames()[colList.get(i).intValue()]);
			if (items[i].getText().length() > 50) {
				items[i].setText(items[i].getText().substring(0, 50) + "...");
			}
			if (items[i].getText().equals("")) {
				items[i].setText("<unnamed correlation>");
			}
			items[i].setName(colList.get(i) + "");
			items[i].setActionCommand("remove correlation");
			items[i].addActionListener(this);
			removeCorrelationMenu.add(items[i]);
		}
		removeCorrelationMenu.addSeparator();
		JMenuItem removeAllItem = new JMenuItem("Remove all correlations");
		removeAllItem.setActionCommand("remove all correlations");
		removeAllItem.addActionListener(this);
		removeCorrelationMenu.add(removeAllItem);
		removeCorrelationMenu.setEnabled(true);
	}

	public JTable getListDisplay() {
		return listDisplay;
	}

	public void refresh() {
		System.out.println("Refreshing");
		geneListDisplayPane.revalidate();
	}

	public void updateMetadataTable() {
		if (mdtablepanel != null) {
			mdtablepanel.updateTable();
			mdtablepanel.getTable().repaint();
		}
	}

	public void updateMetadataTree() {
		if (extInfoPanel2 != null) {
			extInfoPanel2.updateTree();
			// JTree tr=extInfoPanel2.getTree();
			// DefaultTreeModel model = (DefaultTreeModel)tr.getModel();
			// DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
			// model.reload(root);
		}
	}
	/*
	 * public MetadataTableDisplayPanel getMetadataTable() { return
	 * this.mdtablepanel; }
	 */
}
