package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.*;
import edu.iastate.metnet.metaomgraph.SwingWorker;
import edu.iastate.metnet.metaomgraph.MetaOmProject.RepAveragedData;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.chart.BarChart;
import edu.iastate.metnet.metaomgraph.chart.BoxPlot;
import edu.iastate.metnet.metaomgraph.chart.HistogramChart;
import edu.iastate.metnet.metaomgraph.chart.MakeChartWithR;
import edu.iastate.metnet.metaomgraph.chart.MetaOmChartPanel;
import edu.iastate.metnet.metaomgraph.chart.ScatterPlotChart;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.math3.random.RandomDataGenerator;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MetaOmTablePanel extends JPanel implements ActionListener, ListSelectionListener, ChangeListener {

	/*Harsha- Added logger */
	
	private static final Logger logger = MetaOmGraph.logger;
	
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

	// private static int _N = MetaOmGraph.getNumPermutations();
	// private static int _T = MetaOmGraph.getNumThreads();
	private JButton reportButton;
	private JButton listFromFilterButton;
	// urmi
	private JButton saveMainTableButton;
	private MenuButton plotButton;
	private JMenuItem plotListItem;

	private JMenuItem plotRowsItem;
	// urmi
	private JMenuItem plotPairRowsItem;
	private JMenuItem plotFilterItem;
	private JMenuItem plotRepsItem;
	private JMenuItem plotRepsChoose;
	private JMenuItem plotBoxRowItem;
	private JMenuItem plotBoxColItem;
	private JMenuItem plotHistogramItem;

	// urmi plot oclumns
	private JMenu selectedColsMenu;
	private JMenuItem plotCorrHistItem;
	private JMenuItem plotBarChartItem;
	// urmi
	private JMenuItem plotHeatMapItem;
	private JMenuItem runOtherScript;
	private MenuButton analyzeMenuButton;
	private JMenuItem pearsonItem;
	// urmi
	private JMenuItem pearsonItem2;
	private JMenuItem pearsonItem3;
	private JMenuItem pearsonItemPool;
	private JMenuItem pearsonItemPoolFEM;
	private JMenuItem pearsonItemPoolREM;
	private JMenuItem mutualInformationItem;
	private JMenuItem mutualInformationItem2; // pava
	private JMenuItem mutualInformationItem3; // pval
	private JMenuItem mutualInformationItemPairwise;
	private JMenuItem relatednessItem;
	private JMenuItem relatednessPairwise;

	private JMenuItem spearmanItem;
	// urmi
	private JMenuItem spearmanItem2;
	private JMenuItem spearmanItem3;
	private JMenuItem euclideanItem;
	private JMenuItem canberraItem;
	private JMenuItem manhattanItem;
	private JMenuItem weightedEuclideanItem;
	private JMenuItem weightedManhattanItem;
	private JMenuItem saveCorrelationItem;
	

	// urmi
	private JMenuItem diffCorrelation;
	private JMenuItem diffCorrelationWizard;
	private JMenuItem loaddiffCorrResults;
	private JMenuItem removediffCorrResults;

	private JMenuItem pairwisePearsonItem;
	private JMenuItem pairwiseSpearmanItem;
	private JMenu removeCorrelationMenu;
	private JMenu selectedRowsMenu;
	private MenuButton infoButton;
	// urmi
	private JButton metabutton;
	private JMenuItem viewCorrStats;
	private JButton advFilterButton;

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

	JMenu plotRMenu;

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
		plotPairRowsItem = new JMenuItem("Scatter Plot");

		JMenu plotRepsMenu = new JMenu("Line Chart with Averaged Replicates");
		plotRepsItem = new JMenuItem("Default grouping");
		plotRepsChoose = new JMenuItem("Choose grouping");

		plotBoxRowItem = new JMenuItem("Box Plot");
		plotBoxColItem = new JMenuItem("Box Plot Samples");

		plotHistogramItem = new JMenuItem("Histogram");

		// urmi
		plotRMenu = new JMenu("Using R");
		// refresh menuitems before display
		plotRMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JOptionPane.showMessageDialog(null, "cliscked");
				refreshRPlotMenu();
			}
		});
		plotRMenu.addMenuListener(new MenuListener() {
			@Override
			public void menuCanceled(MenuEvent arg0) {

			}

			@Override
			public void menuDeselected(MenuEvent arg0) {

			}

			@Override
			public void menuSelected(MenuEvent arg0) {
				// JOptionPane.showMessageDialog(null, "3");
				refreshRPlotMenu();
			}
		});

		plotListItem.setActionCommand(GRAPH_LIST_COMMAND);
		plotListItem.addActionListener(this);

		plotRowsItem.setActionCommand(GRAPH_SELECTED_COMMAND);
		plotRowsItem.addActionListener(this);
		plotFilterItem.setActionCommand(GRAPH_FILTERED_COMMAND);
		plotFilterItem.addActionListener(this);
		plotFilterItem.setEnabled(false);

		plotPairRowsItem.setActionCommand("scatterplot");
		plotPairRowsItem.addActionListener(this);

		plotRepsItem.setActionCommand("plot reps");
		plotRepsItem.addActionListener(this);
		plotRepsChoose.setActionCommand("choose reps");
		plotRepsChoose.addActionListener(this);

		plotBoxRowItem.setActionCommand("make boxplot");
		plotBoxRowItem.addActionListener(this);
		plotBoxColItem.setActionCommand("col boxplot");
		plotBoxColItem.addActionListener(this);
		plotHistogramItem.setActionCommand("create histogram");
		plotHistogramItem.addActionListener(this);

		JPopupMenu plotPopupMenu = new JPopupMenu();
		selectedRowsMenu.add(plotRowsItem);
		plotRepsMenu.add(plotRepsItem);
		plotRepsMenu.add(plotRepsChoose);
		selectedRowsMenu.add(plotRepsMenu);
		selectedRowsMenu.add(plotPairRowsItem);
		selectedRowsMenu.add(plotBoxRowItem);
		selectedRowsMenu.add(plotHistogramItem);
		selectedRowsMenu.add(plotRMenu);
		plotPopupMenu.add(selectedRowsMenu);

		plotPopupMenu.add(plotFilterItem);
		plotPopupMenu.add(plotListItem);

		selectedColsMenu = new JMenu("Columns");
		plotCorrHistItem = new JMenuItem("Correlation Histogram");
		plotCorrHistItem.setActionCommand("corrHist");
		plotCorrHistItem.addActionListener(this);
		selectedColsMenu.add(plotCorrHistItem);

		plotBarChartItem = new JMenuItem("Bar Chart");
		plotBarChartItem.setActionCommand("barchart");
		plotBarChartItem.addActionListener(this);
		selectedColsMenu.add(plotBarChartItem);

		plotPopupMenu.addSeparator();
		plotPopupMenu.add(selectedColsMenu);

		plotButton.setMenu(plotPopupMenu);
		plotButton.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				selectedRowsMenu.setEnabled(listDisplay.getSelectedRowCount() > 0);
			}

		});
		dataToolbar.add(plotButton);

		// urmi
		// add button to access meta-analysis data
		metabutton = new JButton("Correlation Stats", theme.getReport());
		// add action to view for with metadata
		metabutton.setActionCommand("viewmetaform");
		metabutton.addActionListener(this);

		viewCorrStats = new JMenuItem("View correlation details");
		viewCorrStats.setActionCommand("viewmetaform");
		viewCorrStats.addActionListener(this);

		JPopupMenu analyzePopupMenu = new JPopupMenu();
		// urmi Add menu and sub menu
		JMenu corrMenu = new JMenu("Correlation");
		JMenu pcorrMenu = new JMenu("Pearson's");
		JMenu scorrMenu = new JMenu("Spearman's");
		JMenu poolcorrMenu = new JMenu("Meta-analysis");
		JMenu diffcorrMenu = new JMenu("Differntial Correlation");
		JMenu informationMenu = new JMenu("Mutual Information");
		JMenu distMenu = new JMenu("Distance");

		//////////////////////////
		pearsonItem = new JMenuItem("Pearson Correlation(No pval)");
		pearsonItem.setActionCommand("pearson correlation");
		pearsonItem.addActionListener(this);
		// urmi
		pearsonItem2 = new JMenuItem("Pearson Correlation(permute within groups)");
		pearsonItem2.setActionCommand("pearson correlation2");
		pearsonItem2.addActionListener(this);
		// urmi
		pearsonItem3 = new JMenuItem("Pearson Correlation(permute all)");
		pearsonItem3.setActionCommand("pearson correlation3");
		pearsonItem3.addActionListener(this);

		pearsonItemPool = new JMenuItem("Pearson Correlation(Meta-analysis)");
		pearsonItemPool.setActionCommand("pearson correlationP");
		pearsonItemPool.addActionListener(this);

		pearsonItemPoolFEM = new JMenuItem("Pooled Pearson Correlation(Fixed effects model)");
		pearsonItemPoolFEM.setActionCommand("FEMcorrelationP");
		pearsonItemPoolFEM.addActionListener(this);

		pearsonItemPoolREM = new JMenuItem("Pooled Pearson Correlation(Random effects model)");
		pearsonItemPoolREM.setActionCommand("REMcorrelationP");
		pearsonItemPoolREM.addActionListener(this);

		mutualInformationItem = new JMenuItem("Mutual Information(No pval)");
		mutualInformationItem.setActionCommand("mutualInformation");
		mutualInformationItem.addActionListener(this);

		mutualInformationItem2 = new JMenuItem("Mutual Information(permute within groups)");
		mutualInformationItem2.setActionCommand("mutualInformation2");
		mutualInformationItem2.addActionListener(this);

		mutualInformationItem3 = new JMenuItem("Mutual Information(permute all)");
		mutualInformationItem3.setActionCommand("mutualInformation3");
		mutualInformationItem3.addActionListener(this);

		relatednessItem = new JMenuItem("Relatedness");
		relatednessItem.setActionCommand("relatedness");
		relatednessItem.addActionListener(this);

		mutualInformationItemPairwise = new JMenuItem("Mutual Information matrix");
		mutualInformationItemPairwise.setActionCommand("mutualInformationPairs");
		mutualInformationItemPairwise.addActionListener(this);

		relatednessPairwise = new JMenuItem("Relatedness matrix");
		relatednessPairwise.setActionCommand("relatednessPairs");
		relatednessPairwise.addActionListener(this);

		spearmanItem = new JMenuItem("Spearman Correlation(No pval)");
		spearmanItem.setActionCommand("spearman correlation");
		spearmanItem.addActionListener(this);
		// urmi
		spearmanItem2 = new JMenuItem("Spearman Correlation(permute within groups)");
		spearmanItem2.setActionCommand("spearman correlation2");
		spearmanItem2.addActionListener(this);
		// urmi
		spearmanItem3 = new JMenuItem("Spearman Correlation(permute all)");
		spearmanItem3.setActionCommand("spearman correlation3");
		spearmanItem3.addActionListener(this);

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

		// urmi

		diffCorrelation = new JMenuItem("From Existing Columns");
		diffCorrelation.setActionCommand("DiffCorrelation");
		diffCorrelation.addActionListener(this);

		diffCorrelationWizard = new JMenuItem("New  Differential Correlation");
		diffCorrelationWizard.setActionCommand("NewDiffCorrelation");
		diffCorrelationWizard.addActionListener(this);

		loaddiffCorrResults = new JMenuItem("Load  Differential Correlation Results");
		loaddiffCorrResults.setActionCommand("LoadDiffCorrelation");
		loaddiffCorrResults.addActionListener(this);

		removediffCorrResults = new JMenuItem("Remove Differential Correlation Results");
		removediffCorrResults.setActionCommand("RemoveDiffCorrelation");
		removediffCorrResults.addActionListener(this);

		diffcorrMenu.add(diffCorrelationWizard);
		diffcorrMenu.add(diffCorrelation);
		diffcorrMenu.add(loaddiffCorrResults);
		diffcorrMenu.add(removediffCorrResults);

		pairwisePearsonItem = new JMenuItem("Pearson Correlation matrix");
		pairwisePearsonItem.setActionCommand("pairwise pearson");
		pairwisePearsonItem.addActionListener(this);
		pairwiseSpearmanItem = new JMenuItem("Spearman Correlation matrix");
		pairwiseSpearmanItem.setActionCommand("pairwise spearman");
		pairwiseSpearmanItem.addActionListener(this);

		removeCorrelationMenu = new JMenu("Remove Correlation");

		pcorrMenu.add(pearsonItem);
		pcorrMenu.add(pearsonItem2);
		pcorrMenu.add(pearsonItem3);
		scorrMenu.add(spearmanItem);
		scorrMenu.add(spearmanItem2);
		scorrMenu.add(spearmanItem3);

		poolcorrMenu.add(pearsonItemPool);
		// poolcorrMenu.add(pearsonItemPoolFEM);
		// poolcorrMenu.add(pearsonItemPoolREM);
		corrMenu.add(pcorrMenu);
		corrMenu.add(scorrMenu);
		corrMenu.add(poolcorrMenu);

		corrMenu.addSeparator();
		corrMenu.add(diffcorrMenu);
		corrMenu.add(pairwisePearsonItem);
		corrMenu.add(pairwiseSpearmanItem);
		analyzePopupMenu.add(corrMenu);

		informationMenu.add(mutualInformationItem);
		informationMenu.add(mutualInformationItem2);
		informationMenu.add(mutualInformationItem3);
		// informationMenu.add(relatednessItem);
		informationMenu.addSeparator();
		informationMenu.add(mutualInformationItemPairwise);
		informationMenu.add(relatednessPairwise);
		analyzePopupMenu.add(informationMenu);

		distMenu.add(euclideanItem);
		distMenu.add(canberraItem);
		distMenu.add(manhattanItem);
		distMenu.add(weightedEuclideanItem);
		distMenu.add(weightedManhattanItem);
		analyzePopupMenu.add(distMenu);

		analyzePopupMenu.addSeparator();
		analyzePopupMenu.add(viewCorrStats);
		analyzePopupMenu.addSeparator();
		analyzePopupMenu.add(saveCorrelationItem);
		analyzePopupMenu.add(removeCorrelationMenu);

		// analyzePopupMenu.add(pairwisePearsonItem);
		// analyzePopupMenu.add(pairwiseSpearmanItem);

		/*
		 * analyzePopupMenu.add(pearsonItem); analyzePopupMenu.add(pearsonItem2);
		 * analyzePopupMenu.add(pearsonItem3); analyzePopupMenu.add(pearsonItemPool);
		 * analyzePopupMenu.add(mutualInformationItem);
		 * analyzePopupMenu.add(spearmanItem); analyzePopupMenu.add(euclideanItem);
		 * analyzePopupMenu.add(weightedEuclideanItem);
		 * analyzePopupMenu.add(manhattanItem);
		 * analyzePopupMenu.add(weightedManhattanItem);
		 * 
		 * analyzePopupMenu.add(saveCorrelationItem); analyzePopupMenu.addSeparator();
		 * analyzePopupMenu.add(removeCorrelationMenu);
		 * 
		 * analyzePopupMenu.add(pairwisePearsonItem);
		 * analyzePopupMenu.add(pairwiseSpearmanItem);
		 */
		analyzeMenuButton = new MenuButton("Statistical analysis", theme.getMath(), analyzePopupMenu);

		analyzeMenuButton.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				saveCorrelationItem.setEnabled(myProject.hasLastCorrelation());
				MetaOmTablePanel.this.populateRemoveCorrelationMenu();
			}

		});

		analyzeMenuButton.setToolTipText(
				"Statistically analyze the selected data set against the other sets in the selected list");
		dataToolbar.add(analyzeMenuButton);

		dataToolbar.add(new Separator());
		saveMainTableButton = new JButton(theme.getExcel());
		saveMainTableButton.setActionCommand("savemaintab");
		saveMainTableButton.addActionListener(this);
		dataToolbar.add(saveMainTableButton);

		reportButton = new JButton("Finding samples", theme.getReport());
		reportButton.setToolTipText("Generate report");
		reportButton.setActionCommand(REPORT_COMMAND);
		reportButton.addActionListener(this);
		// reportButton added to projrct menu
		// dataToolbar.add(reportButton);
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

		// infoButton is moved to project menu in the main filemenu
		// dataToolbar.add(infoButton);
		// dataToolbar.add(metabutton);

		String[] listNames = myProject.getGeneListNames();
		Arrays.sort(listNames, new ListNameComparator());
		geneLists = new JList(listNames);
		geneLists.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		geneLists.setSelectedIndex(0);
		geneLists.addListSelectionListener(this);
		// urmi
		geneLists.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				JList l = (JList) e.getSource();
				ListModel m = l.getModel();
				int index = l.locationToIndex(e.getPoint());
				if (index > -1) {
					// create tooltip
					String thisListName = m.getElementAt(index).toString();
					int numElements = myProject.getGeneListRowNumbers(thisListName).length;
					l.setToolTipText(thisListName + ":" + numElements + " Elements");
				}
			}
		});

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

		/*
		 * MyComparator comparator = new MyComparator();
		 * sorter.setColumnComparator(String.class, comparator);
		 * sorter.setColumnComparator(CorrelationValue.class, comparator);
		 * sorter.setColumnComparator(double.class, new AlphanumericComparator());
		 * sorter.setColumnComparator(null, comparator);
		 */

		listDisplay = new StripedTable(sorter);

		sorter.setTableHeader(listDisplay.getTableHeader());

		geneListDisplayPane = new JScrollPane(listDisplay);
		JPanel searchPanel = new JPanel(new BorderLayout());
		searchPanel.add(new JLabel("Filter:"), "Before");
		filterField = new ClearableTextField();
		filterField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 27) {
					filterModel.clearFilter();
					filterField.setText("");
				}
			}
		});
		filterField.getDocument().addDocumentListener(new FilterFieldListener());
		filterField.setDefaultText("Use semicolon (;) for multiple filters");
		filterField.setColumns(20);
		searchPanel.add(filterField, "Center");

		try {
			BufferedImage source = ImageIO
					.read(getClass().getResourceAsStream("/resource/tango/22x22/animations/process-working.png"));
			throbber = new MultiFrameImageThrobber(source, 4, 8);
		} catch (IOException e1) {
			throbber = new MetaOmThrobber();
		}
		searchPanel.add(throbber, "After");
		listFromFilterButton = new JButton(theme.getListSave());
		listFromFilterButton.setActionCommand("list from filter");
		listFromFilterButton.addActionListener(this);
		listFromFilterButton.setEnabled(false);
		listFromFilterButton.setToolTipText("Export the results of the current filter to a new list");
		dataToolbar.add(new Separator());
		dataToolbar.add(searchPanel);
		dataToolbar.add(listFromFilterButton);

		// add advance filter button
		// s
		advFilterButton = new JButton("Advance filter");
		advFilterButton.setActionCommand("advancefilter");
		advFilterButton.addActionListener(this);
		advFilterButton.setToolTipText("Filter/search the table with multiple queries");
		dataToolbar.add(advFilterButton);

		geneListPanel.setMinimumSize(listToolbar.getPreferredSize());
		listSplitPane = new JSplitPane(1, true, geneListPanel, geneListDisplayPane);
		listSplitPane.setDividerSize(1);
		listPanel.add(dataToolbar, "First");
		listPanel.add(listSplitPane, "Center");
		tabby = new JTabbedPane();
		tabby.addTab("Feature Metadata", listPanel);
		if (myProject.getMetadataHybrid() != null)
			addExtInfoTab();
		add(tabby, "Center");
		tabby.addChangeListener(this);
		listDisplay.setAutoResizeMode(0);
		sizeColumnsToFit();
		sorter.setSortingStatus(myProject.getDefaultColumn(), 1);
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		int selectedList = geneLists.getSelectedIndex();
		String[] listNames = myProject.getGeneListNames();
		Arrays.sort(listNames, new ListNameComparator());
		geneLists = new JList(listNames);
		geneLists.addListSelectionListener(this);
		geneLists.setSelectionMode(0);
		// urmi
		geneLists.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				JList l = (JList) e.getSource();
				ListModel m = l.getModel();
				int index = l.locationToIndex(e.getPoint());
				if (index > -1) {
					// create tooltip
					String thisListName = m.getElementAt(index).toString();
					int numElements = myProject.getGeneListRowNumbers(thisListName).length;
					l.setToolTipText(thisListName + ":" + numElements + " Elements");
				}
			}
		});
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

	@Override
	public void valueChanged(ListSelectionEvent event) {
		int[] oldWidths = new int[listDisplay.getColumnCount()];
		for (int x = 0; x < oldWidths.length; x++)
			oldWidths[x] = listDisplay.getColumnModel().getColumn(x).getPreferredWidth();
		try {
			mainModel = new NoneditableTableModel(
					myProject.getGeneListRowNames(geneLists.getSelectedValue().toString()),
					myProject.getInfoColumnNames());
		} catch (NullPointerException npe) {
			return;
		}
		filterModel = new FilterableTableModel(mainModel);
		sorter = new TableSorter(filterModel);
		listDisplay = new StripedTable(sorter);
		listDisplay.setAutoResizeMode(0);
		sorter.setTableHeader(listDisplay.getTableHeader());

		/*
		 * sorter.setColumnComparator(String.class, new MyComparator()); // urmi add
		 * comparator for double when state changed
		 * sorter.setColumnComparator(CorrelationValue.class, new MyComparator());
		 * sorter.setColumnComparator(double.class, new AlphanumericComparator());
		 * sorter.setColumnComparator(null, new MyComparator());
		 */

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

	/**
	 * @author urmi launch ensemble website
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public void launchEnsembl(String db) throws URISyntaxException, IOException {
		int[] selected = listDisplay.getSelectedRows();
		if (selected.length >= 10) {
			int dialogButton = JOptionPane.YES_NO_OPTION;
			int dialogResult = JOptionPane.showConfirmDialog(null,
					"This will open " + selected.length + " web pages. Do you want to continue?", "Continue?",
					dialogButton);
			if (dialogResult == JOptionPane.NO_OPTION) {
				return;
			}
		}
		URI ns = null;
		for (int i = 0; i < selected.length; i++) {
			String selectedID = listDisplay.getValueAt(selected[i], myProject.getDefaultColumn()) + "";
			// check if this id is transcript or gene
			if (db == "all") {
				ns = new URI("https://www.ensembl.org/Multi/Search/Results?q=" + selectedID + ";site=ensembl_all");
			} else if (db == "plants") {
				ns = new URI("https://plants.ensembl.org/Multi/Search/Results?species=all;idx=;q=" + selectedID
						+ ";site=ensemblunit");
			}
			java.awt.Desktop.getDesktop().browse(ns);

		}

	}

	/**
	 * @author urmi launch refseq website
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public void launchRefSeq() throws URISyntaxException, IOException {
		int[] selected = listDisplay.getSelectedRows();
		if (selected.length >= 10) {
			int dialogButton = JOptionPane.YES_NO_OPTION;
			int dialogResult = JOptionPane.showConfirmDialog(null,
					"This will open " + selected.length + " web pages. Do you want to continue?", "Continue?",
					dialogButton);
			if (dialogResult == JOptionPane.NO_OPTION) {
				return;
			}
		}
		URI ns = null;
		for (int i = 0; i < selected.length; i++) {
			String selectedID = listDisplay.getValueAt(selected[i], myProject.getDefaultColumn()) + "";
			if (selectedID.contains(".")) {
				// JOptionPane.showMessageDialog(null, "thisID:"+selectedID);
				selectedID = selectedID.split("\\.")[0];
			}
			// JOptionPane.showMessageDialog(null, "thisID:"+selectedID);
			// check if this id is transcript or gene
			ns = new URI(
					"https://www.ncbi.nlm.nih.gov/nuccore/?term=" + selectedID + "[Text+Word]+AND+srcdb_refseq[PROP]");
			java.awt.Desktop.getDesktop().browse(ns);

		}

	}

	public void launchGeneCards() throws URISyntaxException, IOException {
		int[] selected = listDisplay.getSelectedRows();
		if (selected.length >= 10) {
			int dialogButton = JOptionPane.YES_NO_OPTION;
			int dialogResult = JOptionPane.showConfirmDialog(null,
					"This will open " + selected.length + " web pages. Do you want to continue?", "Continue?",
					dialogButton);
			if (dialogResult == JOptionPane.NO_OPTION) {
				return;
			}
		}
		URI ns = null;
		for (int i = 0; i < selected.length; i++) {
			String selectedID = listDisplay.getValueAt(selected[i], myProject.getDefaultColumn()) + "";
			if (selectedID.contains(".")) {
				// JOptionPane.showMessageDialog(null, "thisID:"+selectedID);
				selectedID = selectedID.split("\\.")[0];
			}
			// JOptionPane.showMessageDialog(null, "thisID:"+selectedID);
			// check if this id is transcript or gene
			ns = new URI("https://www.genecards.org/Search/Keyword?queryString=" + selectedID);
			java.awt.Desktop.getDesktop().browse(ns);

		}

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
			for (int y = 0; (y < listDisplay.getColumnCount()); y++) {
				if (Utils.isGeneID(listDisplay.getValueAt(selected[x], y) + "")) {
					// found = true;
					// pass all selected genes as query
					geneList += listDisplay.getValueAt(selected[x], y) + ";";
				}
			}
		}

		// JOptionPane.showMessageDialog(null, "this gene:"+geneList);
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
				launcher.openURLinBrowser(urlString);
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
				launcher.openURLinBrowser(urlString);
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
		ActionProperties lineChartAction = new ActionProperties("line-chart",Arrays.toString(getSelectedRowsInList()),null,"true",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
		lineChartAction.logActionProperties(logger);
	
		new MetaOmChartPanel(getSelectedRowsInList(), myProject.getDefaultXAxis(), myProject.getDefaultYAxis(),
				myProject.getDefaultTitle(), myProject.getColor1(), myProject.getColor2(), myProject)
						.createInternalFrame();
	}

	public void makeBoxPlot() {
		int[] selected = getSelectedRowsInList();
		
		ActionProperties boxPlotAction = new ActionProperties("box-plot",Arrays.toString(selected),null,"true",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
		
		if (selected.length < 1) {
			JOptionPane.showMessageDialog(null, "Please select one or more rows and try again.",
					"Invalid number of rows selected", JOptionPane.ERROR_MESSAGE);
			boxPlotAction.setResult("Error: Invalid number of rows selected.Please select one or more rows and try again.");
			boxPlotAction.logActionProperties(logger);
			return;
		}
		

		// get data for box plot as hasmap
		HashMap<Integer, double[]> plotData = new HashMap<>();
		for (int i = 0; i < selected.length; i++) {
			double[] dataY = null;
			try {
				// dataY = myProject.getIncludedData(selected[i]);
				// send all data; excluded data will be excluded in the boxplot class; this
				// helps in splitting data by categories by reusing cluster function
				dataY = myProject.getAllData(selected[i]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			plotData.put(selected[i], dataY);
		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {// get data for selected rows

					BoxPlot f = new BoxPlot(plotData, 0, myProject);
					MetaOmGraph.getDesktop().add(f);
					f.setDefaultCloseOperation(2);
					f.setClosable(true);
					f.setResizable(true);
					f.pack();
					f.setSize(1000, 700);
					f.setVisible(true);
					f.toFront();

				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error occured while reading data!!!", "Error",
							JOptionPane.ERROR_MESSAGE);
					boxPlotAction.setResult("Error: Error occured while reading data!!!");
					e.printStackTrace();
					return;
				}
			}
		});

		boxPlotAction.logActionProperties(logger);
		
		return;
	}

	public void graphPairs() {
		int[] selected = getSelectedRowsInList();
		
		ActionProperties scatterPlotAction = new ActionProperties("scatter-plot",Arrays.toString(selected),null,"true",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
		
		
		if (selected.length < 2) {
			JOptionPane.showMessageDialog(null, "Please select two or more rows and try again to plot a scatterplot.",
					"Invalid number of rows selected", JOptionPane.ERROR_MESSAGE);
			
			scatterPlotAction.setResult("Error: Invalid number of rows selected.Please select two or more rows and try again to plot a scatterplot.");
			scatterPlotAction.logActionProperties(logger);
			return;
		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {// get data for selected rows

					ScatterPlotChart f = new ScatterPlotChart(selected, 0, myProject);
					MetaOmGraph.getDesktop().add(f);
					f.setDefaultCloseOperation(2);
					f.setClosable(true);
					f.setResizable(true);
					f.pack();
					f.setSize(1000, 700);
					f.setVisible(true);
					f.toFront();

				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error occured while reading data!!!", "Error",
							JOptionPane.ERROR_MESSAGE);

					scatterPlotAction.setResult("Error: Error occured while reading data!!!");
					e.printStackTrace();
					return;
				}
			}
		});

		scatterPlotAction.logActionProperties(logger);
		
		return;
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
		List sel = geneLists.getSelectedValuesList();

		for (Object s : sel) {
			myProject.deleteGeneList(s.toString());
		}
		// myProject.deleteGeneList(geneLists.getSelectedValue().toString());
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
			if (tabby.getTitleAt(x).equals("Sample Metadata Tree")) {
				tabby.remove(x);
				break;
			}
		}

		for (int x = 0; x < tabby.getTabCount(); x++) {
			if (tabby.getTitleAt(x).equals("Sample Metadata Table")) {
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
			extInfoPanel2 = new MetadataTreeDisplayPanel(mdhObj);
			tabby.addTab("Sample Metadata Tree", extInfoPanel2);
			// build the treemap to map data col index to nodes in Jtree displaying data
			// extInfoPanel2.buildTreemaptoNode();
			mdtablepanel = new MetadataTableDisplayPanel(mdhObj.getMetadataCollection());
			// set hyperlink columns
			mdtablepanel.setsrrColumn(MetaOmGraph._SRR);
			mdtablepanel.setsrpColumn(MetaOmGraph._SRP);
			mdtablepanel.setsrxColumn(MetaOmGraph._SRX);
			mdtablepanel.setsrsColumn(MetaOmGraph._SRS);
			mdtablepanel.setgseColumn(MetaOmGraph._GSE);
			mdtablepanel.setgsmColumn(MetaOmGraph._GSM);
			tabby.addTab("Sample Metadata Table", mdtablepanel);
			mdtablepanel.updateTable();
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
			rect.width += rect.x;
			rect.x = 0;
			tree.scrollRectToVisible(rect);
			tabby.setSelectedComponent(extInfoPanel2);
		}
	}

	public void selecTabRow(String value) {
		// select first val
		/*
		 * java.util.List<String> hits =
		 * MetaOmGraph.getActiveProject().getMetadataHybrid().searchByValue(value,
		 * MetaOmGraph.getActiveProject().getMetadataHybrid().getDataColName(), true,
		 * false, true); JOptionPane.showMessageDialog(null, "hits:"+hits.toString());
		 */

		///// set selected rows /////////

		mdtablepanel.setSelectedRowWithValue(value);
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

		@Override
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

	@Override
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
					"Are you sure you want to delete the selected lists '" + geneLists.getSelectedValue().toString()
							+ "'?",
					"Confirm", 0, 3);
			if (result == 0)
				deleteSelectedList();
			return;
		}

		if (GRAPH_LIST_COMMAND.equals(e.getActionCommand())) {
			graphSelectedList();
			return;
		}

		if ("corrHist".equals(e.getActionCommand())) {

			plotCorrHist(selectCorrColumn());
			return;
		}

		if ("barchart".equals(e.getActionCommand())) {
			plotBarChart(selectFeatureColumn());
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
		if ("scatterplot".equals(e.getActionCommand())) {
			graphPairs();
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
					myProject.getColor2(), myProject, myVals, myStddevs, repCounts, sampleNames, sampleNames, true,
					null);
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
			// JOptionPane.showMessageDialog(null, "Selected rows:" +
			// Arrays.toString(selected));
			ArrayList<double[]> myVals = new ArrayList();
			ArrayList<double[]> myStddevs = new ArrayList();
			ArrayList<int[]> repCounts = new ArrayList();
			String[] sampleNames = null;
			String[] groupNames = null;

			TreeMap<String, List<Integer>> repsMapDefault = myProject.getMetadataHybrid().getDefaultRepsMap();
			for (int thisRow : selected) {
				ReplicateGroups result = new ReplicateGroups(repsMapDefault, thisRow);
				myVals.add(result.getValues());
				myStddevs.add(result.getStdDev());
				repCounts.add(result.getRepCounts());
				sampleNames = result.getSampnames();
				groupNames = result.getGroupnames();
				if (sampleNames.length == 0) {
					JOptionPane.showMessageDialog(this, "There are no sample names!");
					return;
				}
			}

			// plot
			MetaOmChartPanel ob = new MetaOmChartPanel(getSelectedRowsInList(), "Groups", myProject.getDefaultYAxis(),
					myProject.getDefaultTitle(), myProject.getColor1(), myProject.getColor2(), myProject, myVals,
					myStddevs, repCounts, groupNames, sampleNames, true, repsMapDefault);
			ob.createInternalFrame();
			return;
		}

		if ("choose reps".equals(e.getActionCommand())) {
			// if no reps find reps
			if (myProject.getMetadataHybrid() == null) {
				JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "No data to calculate rep information...");
				return;
			}

			// show jdialog to show all metadata attributes
			String[] headers = myProject.getMetadataHybrid().getMetadataHeaders(true);
			String input = (String) JOptionPane.showInputDialog(null, "Please choose attribute to group by",
					"Please choose", JOptionPane.PLAIN_MESSAGE, null, headers, headers[1]);
			if (input == null || input.length() < 1) {
				return;
			}
			TreeMap<String, List<Integer>> repsMap = myProject.getMetadataHybrid().buildRepsMap(input);
			// JOptionPane.showMessageDialog(null, "thisTM:"+repsMap.toString());
			// JOptionPane.showMessageDialog(null, "all keys:"+repsMap.keySet().toString());

			// plot reps
			int[] selected = getSelectedRowsInList();

			ArrayList<double[]> myVals = new ArrayList();
			ArrayList<double[]> myStddevs = new ArrayList();
			ArrayList<int[]> repCounts = new ArrayList();
			String[] sampleNames = null;
			String[] groupNames = null;
			for (int thisRow : selected) {
				ReplicateGroups result = new ReplicateGroups(repsMap, thisRow);
				if (result.getErrorStatus()) {
					return;
				}
				myVals.add(result.getValues());
				myStddevs.add(result.getStdDev());
				repCounts.add(result.getRepCounts());
				sampleNames = result.getSampnames();
				groupNames = result.getGroupnames();
				if (sampleNames.length == 0) {
					JOptionPane.showMessageDialog(this, "There are no sample names!");
					return;
				}
			}
			MetaOmChartPanel ob = new MetaOmChartPanel(getSelectedRowsInList(), "Groups", myProject.getDefaultYAxis(),
					myProject.getDefaultTitle(), myProject.getColor1(), myProject.getColor2(), myProject, myVals,
					myStddevs, repCounts, groupNames, sampleNames, true, repsMap);
			ob.createInternalFrame();
			return;
		}

		if ("make boxplot".equals(e.getActionCommand())) {

			/*
			 * JPanel boxPlot = BoxPlotter.getFeatureBoxPlot(myProject,
			 * getSelectedRowsInList()); String title = "Box Plot";
			 * MetaOmGraph.addInternalFrame(boxPlot, title);
			 */
			makeBoxPlot();
			return;
		}

		if ("col boxplot".equals(e.getActionCommand())) {
			// MetaOmGraph.addInternalFrame(BoxPlotter.getColumnBoxPlot(myProject),"colBP");
			return;
		}
		if ("create histogram".equals(e.getActionCommand())) {
			
			ActionProperties histogramAction = new ActionProperties("histogram",Arrays.toString(getSelectedRowsInList()),null,"true",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
			
			
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {// get data for selected rows
						int[] selected = getSelectedRowsInList();
						// number of bins
						int nBins = myProject.getIncludedDataColumnCount() / 10;
						HistogramChart f = new HistogramChart(selected, nBins, myProject, 1, null);
						MetaOmGraph.getDesktop().add(f);
						f.setDefaultCloseOperation(2);
						f.setClosable(true);
						f.setResizable(true);
						f.pack();
						f.setSize(1000, 700);
						f.setVisible(true);
						f.toFront();

					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Error occured while reading data!!!", "Error",
								JOptionPane.ERROR_MESSAGE);
						histogramAction.setResult("Error: Error occured while reading data!!!");
						histogramAction.logActionProperties(logger);
						e.printStackTrace();
						return;
					}
				}
			});
			histogramAction.logActionProperties(logger);
			return;
		}
		if ("create heatmap".equals(e.getActionCommand())) {
			// create heat map for selected rows over all included columns
			// temp solution
			// 1 write selected data as tabdelimited file
			// 2 use r script to create a plot

			// get data of selected rows
			List<double[]> dataRows = new ArrayList<>();
			int[] selected = getSelectedRowsInList();
			String[] rowNames = new String[selected.length];
			String[] colNames = myProject.getIncludedDataColumnHeaders();
			// s
			for (int i = 0; i < selected.length; i++) {
				try {
					dataRows.add(myProject.getIncludedData(selected[i]));
					rowNames[i] = myProject.getRowName(selected[i])[myProject.getDefaultColumn()].toString();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			// JOptionPane.showMessageDialog(null, "RN:"+Arrays.toString(rowNames));
			// JOptionPane.showMessageDialog(null, "CN:"+Arrays.toString(colNames));
			String chartFileName = JOptionPane.showInputDialog(this, "Please enter name to save heatmap to file",
					"Please enter name", JOptionPane.INFORMATION_MESSAGE);
			if (chartFileName == null || chartFileName.length() < 1) {
				return;
			}
			String datafilePath = "";
			MakeChartWithR ob = new MakeChartWithR();
			try {
				datafilePath = ob.saveDatatoFile(dataRows, rowNames, colNames, chartFileName);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// execute rscript to make plot and save to chartFileName.png
			try {
				ob.makeHeatmap(datafilePath, chartFileName);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		if (e.getActionCommand().startsWith("runuserR")) {

			String rFilepath = "";
			if ("runuserR".equals(e.getActionCommand())) {
				// select user script
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(MetaOmTablePanel.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					rFilepath = file.getAbsolutePath();

				} else {

					return;
				}
			} else if (e.getActionCommand().contains("::")) {
				rFilepath = e.getActionCommand().split("::")[1];
			}

			if (rFilepath == null || rFilepath.length() < 1) {
				JOptionPane.showMessageDialog(null, "Error occured locating R file", "Error",
						JOptionPane.ERROR_MESSAGE);
			}

			// get data of selected rows
			List<double[]> dataRows = new ArrayList<>();
			int[] selected = getSelectedRowsInList();
			String[] rowNames = new String[selected.length];
			String[] colNames = myProject.getIncludedDataColumnHeaders();
			// s
			for (int i = 0; i < selected.length; i++) {
				try {
					dataRows.add(myProject.getIncludedData(selected[i]));
					rowNames[i] = myProject.getRowName(selected[i])[myProject.getDefaultColumn()].toString();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			String outFiledir = JOptionPane.showInputDialog(this, "Please enter a dircetory name to save results",
					"Please enter name", JOptionPane.INFORMATION_MESSAGE);
			if (outFiledir == null || outFiledir.length() < 1) {
				return;
			}

			// save data to file for script to read
			MakeChartWithR ob = new MakeChartWithR();
			String datafilePath = "";
			try {
				datafilePath = ob.saveDatatoFile(dataRows, rowNames, colNames, outFiledir, "mogData");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// execute r script
			try {
				// construct outdir path
				String directory = MetaOmGraph.getActiveProject().getSourceFile().getParent();
				directory = directory + System.getProperty("file.separator") + outFiledir;
				// JOptionPane.showMessageDialog(null, "File to save dir:"+directory);
				ob.runUserR(rFilepath, datafilePath, myProject.getMetadataHybrid().getMetadataFilePath(), directory);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		if (REPORT_COMMAND.equals(e.getActionCommand())) {
			makeReport();
			return;
		}
		if ("viewmetaform".equals(e.getActionCommand())) {
			// JOptionPane.showMessageDialog(null, "showmwta...");
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					try {
						HashMap<String, CorrelationMetaCollection> metaCorrRes = myProject.getMetaCorrRes();
						if (metaCorrRes == null || metaCorrRes.size() < 1) {
							JOptionPane.showMessageDialog(null, "No correlations found...");
							return;
						}
						CorrelationMetaTable frame = new CorrelationMetaTable(metaCorrRes);
						frame.setVisible(true);
						frame.setSize(MetaOmGraph.getMainWindow().getWidth() / 2,
								MetaOmGraph.getMainWindow().getHeight() / 2);
						MetaOmGraph.getDesktop().add(frame);
						frame.setSelected(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

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

		if ("savemaintab".equals(e.getActionCommand())) {

			Utils.saveJTabletofile(listDisplay);
			return;
		}
		if ("advancefilter".equals(e.getActionCommand())) {

			// show advance filter options
			final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(
					MetaOmGraph.getActiveProject(), true);
			final MetadataQuery[] queries;
			queries = tsp.showSearchDialog();
			// boolean matchCase=tsp.matchCase();
			boolean matchAll = tsp.matchAll();
			if (tsp.getQueryCount() <= 0) {
				// System.out.println("Search dialog cancelled");
				// User didn't enter any queries
				return;
			}

			String[] headers = myProject.getInfoColumnNames();
			List<String> headersList = Arrays.asList(headers);

			// JOptionPane.showMessageDialog(null, "h:"+headersList);

			// convert queries to filter string
			String allFilter = "";
			for (int i = 0; i < queries.length; i++) {

				String thisFilter = "";
				String thisField = queries[i].getField();
				boolean thismatchCase = queries[i].isCaseSensitive();
				String thisTerm = queries[i].getTerm();
				// JOptionPane.showMessageDialog(null,"F:" + queries[i].getField() + " T:" +
				// queries[i].getTerm() + " isE:" + queries[i].isExact()+ "mC:"+thismatchCase);
				if (thismatchCase) {
					thisTerm += "--C";
				}
				if (thisField.equals("Any Field") || thisField.equals("All Fields")) {
					thisFilter = thisTerm;
				} else {
					int thisCol = headersList.indexOf(thisField);
					thisFilter = thisTerm + ":::" + String.valueOf(thisCol);
				}

				allFilter += thisFilter + ";";
			}

			filterField.setText(allFilter);

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
				|| ("FEMcorrelationP".equals(e.getActionCommand())) || ("REMcorrelationP".equals(e.getActionCommand()))
				|| ("pearson correlation2".equals(e.getActionCommand()))
				|| ("pearson correlation3".equals(e.getActionCommand()))
				|| ("mutualInformation".equals(e.getActionCommand()))
				|| ("mutualInformation2".equals(e.getActionCommand()))
				|| ("mutualInformation3".equals(e.getActionCommand()))
				|| ("spearman correlation".equals(e.getActionCommand()))
				|| ("spearman correlation2".equals(e.getActionCommand()))
				|| ("spearman correlation3".equals(e.getActionCommand()))
				|| ("euclidean distance".equals(e.getActionCommand()))
				|| ("canberra distance".equals(e.getActionCommand()))
				|| ("manhattan distance".equals(e.getActionCommand()))
				|| ("weighted euclidean distance".equals(e.getActionCommand()))
				|| ("weighted manhattan distance".equals(e.getActionCommand()))) {
			if (listDisplay.getSelectedRowCount() < 1) {
				JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Please select a row to analyze!", "Error",
						0);
				return;
			}

			if (listDisplay.getSelectedRowCount() > 1) {
				JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Please select only one row to analyze!",
						"Error", 0);
				return;
			}

			int target = getTrueSelectedRow();
			String targetName = getSelectedGeneName();
			/*
			 * if (myProject.hasLastCorrelation()) { int result =
			 * JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(),
			 * "Do you want to keep the results of the last correlation? (If no, the new results will overwrite the old)"
			 * ); if (result == 2) return; if ((result == 0) && (!keepLastCorrelation()))
			 * return; }
			 */
			if (myProject.hasLastCorrelation()) {

				keepLastCorrelation();
			}

			String methodName = "";
			if ("pearson correlation".equals(e.getActionCommand())) {
				methodName = "pearson correlation";
			} else if ("pearson correlationP".equals(e.getActionCommand())) {
				methodName = "weighted pearson correlation";
			} else if ("FEMcorrelationP".equals(e.getActionCommand())) {
				methodName = "weighted pearson correlation(FEM)";
			} else if ("REMcorrelationP".equals(e.getActionCommand())) {
				methodName = "weighted pearson correlation(REM)";
			} else if ("pearson correlation2".equals(e.getActionCommand())) {
				methodName = "pearson correlation (shuffle in-group)";
			} else if ("pearson correlation3".equals(e.getActionCommand())) {
				methodName = "pearson correlation (shuffle all)";
			} else if ("mutualInformation".equals(e.getActionCommand())) {
				methodName = "mutual information";
			} else if ("mutualInformation2".equals(e.getActionCommand())) {
				methodName = "mutual information (shuffle in-group)";
			} else if ("mutualInformation3".equals(e.getActionCommand())) {
				methodName = "mutual information (shuffle all)";
			} else if ("spearman correlation".equals(e.getActionCommand())) {
				methodName = "spearman correlation";
			} else if ("spearman correlation2".equals(e.getActionCommand())) {
				methodName = "spearman correlation (shuffle in-group)";
			} else if ("spearman correlation3".equals(e.getActionCommand())) {
				methodName = "spearman correlation (shuffle all)";
			} else if ("euclidean distance".equals(e.getActionCommand())) {
				methodName = "euclidean distance";
			} else if ("canberra distance".equals(e.getActionCommand())) {
				methodName = "canberra distance";
			} else if ("manhattan distance".equals(e.getActionCommand())) {
				methodName = "manhattan distance";
			} else if ("weighted euclidean distance".equals(e.getActionCommand())) {
				methodName = "weighted euclidean distance";
			} else if ("weighted manhattan distance".equals(e.getActionCommand())) {
				methodName = "weighted manhattan distance";
			}

			String name = (String) JOptionPane.showInputDialog(MetaOmGraph.getDesktop(),
					"Please enter a name for the correlation", "Save Correlation", 3, null, null,
					targetName + " " + methodName);
			if (name == null)
				return;

			name = name.trim();
			// check if this name already exists in saved correlations
			if (myProject.correlatioNameExists(name)) {
				while (myProject.correlatioNameExists(name)) {
					name = (String) JOptionPane.showInputDialog(MetaOmGraph.getDesktop(),
							"A previous analysis exists with the same name. Please enter a different name for the correlation",
							"Store Correlation", 3, null, null, targetName + " " + methodName);
				}
			}

			if (name == null || name.equals(""))
				return;

			try {
				if ("pearson correlation".equals(e.getActionCommand())) {
					// measure time
					// long startTime = System.nanoTime();
					MetaOmAnalyzer.doAnalysis(myProject, geneLists.getSelectedValue().toString(), target, name, 1);
					// long endTime = System.nanoTime();
					// get difference of two nanoTime values
					// float timeElapsed = endTime - startTime;
					// timeElapsed = (timeElapsed / (float) 1000000000.00);
					// JOptionPane.showMessageDialog(null, "Time taken:" + timeElapsed);
				} else if ("pearson correlationP".equals(e.getActionCommand())) {
					// Meta-analysis model
					// store all the meta corr results in a list of objects
					List<CorrelationMeta> corrMetaResList = new ArrayList<>();
					if (myProject.getMetadataHybrid() == null) {
						JOptionPane.showMessageDialog(null, "No metadata loaded", "Metadata not found",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					TreeMap<String, List<Integer>> groupsMap = myProject.getMetadataHybrid().getDefaultRepsMap();
					// for each row in data file calculate corr coeff within groups
					// get entries in a gene list for complete list get all
					final int[] entries = myProject.getGeneListRowNumbers(geneLists.getSelectedValue().toString());
					double[] sourceData = myProject.getIncludedData(entries[target]);
					double[] sourceDataAll = myProject.getAllData(entries[target]);
					boolean[] exclude = MetaOmAnalyzer.getExclude();
					// create list of source data into groups
					List<double[]> sourceGrouped = groupDatabyRepColumn(groupsMap, sourceDataAll, exclude);
					//if groping failed
					if(sourceGrouped==null) {
						return;
					}
					final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
							"Computing p-vals...", "", 0L, entries.length, true);
					final String nameSave = name;

					Object[] options = { "Random effects model", "Fixed effect model" };
					final int n = JOptionPane.showOptionDialog(null, "Please choose a model", "Please choose a model",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

					SwingWorker analyzeWorker = new SwingWorker() {
						boolean errored = false;
						List<Double> pvres = new ArrayList<>();
						List<Double> corres = new ArrayList<>();

						@Override
						public Object construct() {
							try {
								int j = 0;
								do {
									progress.setProgress(j);
									double[] data = myProject.getAllData(entries[j]);
									List<double[]> dataGrouped = groupDatabyRepColumn(groupsMap, data, exclude);

									try {
										CorrelationGrouped ob = new CorrelationGrouped(sourceGrouped, dataGrouped,
												MetaOmGraph.getNumThreads());
										CorrelationMeta temp;
										if (n == JOptionPane.YES_OPTION) {
											// random model
											temp = ob.doComputation(true);
										} else {
											// fixed model
											temp = ob.doComputation(false);
										}
										// CorrelationMeta temp = ob.doComputation(remFlag);
										// target gene name to correlation meta object
										String thisgeneName = myProject.getRowName(entries[j])[myProject
												.getDefaultColumn()].toString();
										// JOptionPane.showMessageDialog(null, "this gene name:"+thisgeneName);
										// add gene name in corrmeta obj
										temp.settargetName(thisgeneName);
										corrMetaResList.add(temp);
										double thisrVal = temp.getrVal();
										corres.add(thisrVal);
										// JOptionPane.showMessageDialog(null, "thisrval:" + thisrVal);
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

						@Override
						public void finished() {

							if ((!progress.isCanceled()) && (!errored)) {
								String corrModel = "";
								if (n == JOptionPane.YES_OPTION) {
									corrModel = "Random effects model";
								} else {
									corrModel = "Fixed effects model";
								}
								CorrelationMetaCollection cmcObj = new CorrelationMetaCollection(nameSave, 0, corrModel,
										targetName, corrMetaResList);
								myProject.addMetaCorrRes(nameSave, cmcObj);

								final Number[] result = new Number[myProject.getRowCount()];
								for (int i = 0; i < corres.size(); i++) {

									result[entries[i]] = new CorrelationValue(corres.get(i));

								}
								myProject.setLastCorrelation(result, nameSave);

							}
							progress.dispose();
						}
					};
					analyzeWorker.start();
					progress.setVisible(true);

				} else if ("pearson correlation2".equals(e.getActionCommand())) {
					// shuffle _N time only within groups
					// JOptionPane.showMessageDialog(null, "PC2");
					// first run regular method to get correlation values
					// MetaOmAnalyzer.doAnalysis(myProject, geneLists.getSelectedValue().toString(),
					// target, name, 1);
					final int[] entries = myProject.getGeneListRowNumbers(geneLists.getSelectedValue().toString());
					double[] sourceData = myProject.getIncludedData(entries[target]);

					int _N = MetaOmGraph.getNumPermutations();
					int _T = MetaOmGraph.getNumThreads();
					// int _N = MetaOmGraph.getNumPermutations(); // num permutations
					// int _T = MetaOmGraph.getNumThreads(); // num threads
					// do _N permutations. Start
					List<Double> sourceDataList = new ArrayList<>();
					for (double d : sourceData) {
						sourceDataList.add(d);
					}

					// JOptionPane.showMessageDialog(null, "sd:" + Arrays.toString(sourceData));
					List<double[]> shuffList = new ArrayList<double[]>();
					// add original data to calculate original correlation val which will be at
					// index 0
					// shuffle _N time only within groups
					if (myProject.getMetadataHybrid() == null) {
						JOptionPane.showMessageDialog(null, "No metadata loaded", "Metadata not found",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					// add original data to calculate original correlation val which will be at
					// index 0
					shuffList.add(sourceData);
					///////////////////////// shuffle within group////////////////////////
					TreeMap<String, List<Integer>> groupsMap = myProject.getMetadataHybrid().getDefaultRepsMap();
					// this array contains colindex of the datacolumns used
					int[] sourceDataColNumbers = new int[sourceData.length];
					boolean[] exclude = MetaOmAnalyzer.getExclude();

					final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
							"Analyzing...", "", 0L, entries.length, true);
					// create metacorrobj
					List<CorrelationMeta> corrMetaResList = new ArrayList<>();
					// name to save results
					final String nameSave = name;
					SwingWorker analyzeWorker = new SwingWorker() {
						boolean errored = false;
						List<Double> pvres = new ArrayList<>();
						List<Double> corrres = new ArrayList<>();

						@Override
						public Object construct() {
							try {
								// for each data row do
								List<int[]> shuffInd = groupDataIndexbyRepColumn(groupsMap, exclude,
										sourceDataColNumbers, _N);
								for (int j = 0; j < shuffInd.size(); j++) {
									double[] tempArr = new double[sourceData.length];
									int[] newInd = shuffInd.get(j);
									for (int n = 0; n < tempArr.length; n++) {
										// copy indices.get(n) column from targetwtMat to nth place in tempmat
										tempArr[n] = sourceData[newInd[n]];
									}
									shuffList.add(tempArr);
								}
								int j = 0;

								do {
									progress.setProgress(j);
									double[] data = myProject.getIncludedData(entries[j]);

									// for each row calculate p-value
									ComputePval ob = new ComputePval(data, shuffList, _T);
									try {
										double[] pv = ob.doComputation();

										pvres.add(pv[1]);
										corrres.add(pv[0]);
										double thisrVal = 0;
										CorrelationMeta temp = new CorrelationMeta(pv[0], pv[1]);
										String thisgeneName = myProject.getRowName(entries[j])[myProject
												.getDefaultColumn()].toString();
										temp.settargetName(thisgeneName);
										corrMetaResList.add(temp);
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

						@Override
						public void finished() {
							if ((!progress.isCanceled()) && (!errored)) {
								final Number[] result = new Number[myProject.getRowCount()];
								for (int i = 0; i < corrres.size(); i++) {
									// result[entries[i]] = corrres.get(i);
									result[entries[i]] = new CorrelationValue(corrres.get(i));
								}
								myProject.setLastCorrelation(result, nameSave);
								// String s=String.valueOf(name);
								CorrelationMetaCollection cmcObj = new CorrelationMetaCollection(nameSave, 1,
										"ShuffleWithInGrps", targetName, corrMetaResList);
								myProject.addMetaCorrRes(nameSave, cmcObj);
							}
							progress.dispose();
						}
					};
					analyzeWorker.start();
					progress.setVisible(true);
				} else if ("pearson correlation3".equals(e.getActionCommand())) {
					// shuffle all
					// get significance using permutation test
					final int[] entries = myProject.getGeneListRowNumbers(geneLists.getSelectedValue().toString());
					double[] sourceData = myProject.getIncludedData(entries[target]);
					int _N = MetaOmGraph.getNumPermutations();
					int _T = MetaOmGraph.getNumThreads();
					// do _N permutations
					List<Double> sourceDataList = new ArrayList<>();
					for (double d : sourceData) {
						sourceDataList.add(d);
					}
					List<double[]> shuffList = new ArrayList<double[]>();
					// add original data to calculate original correlation val which will be at
					// index 0

					shuffList.add(sourceData); // shuffle _N time to create a list of shuffledlists
					for (int i = 0; i < _N; i++) {
						java.util.Collections.shuffle(sourceDataList);
						double[] tempArr = new double[sourceData.length];
						int k = 0;
						for (double d : sourceDataList) {
							tempArr[k++] = d;
						}
						shuffList.add(tempArr);
					}

					final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
							"Analyzing...", "", 0L, entries.length, true);
					List<CorrelationMeta> corrMetaResList = new ArrayList<>();
					// name to save results
					final String nameSave = name;
					SwingWorker analyzeWorker = new SwingWorker() {
						boolean errored = false;
						List<Double> pvres = new ArrayList<>();
						List<Double> corrres = new ArrayList<>();

						@Override
						public Object construct() {
							try {
								// for each data row do
								// Number[] result = null;
								int j = 0;

								do {
									progress.setProgress(j);
									double[] data = myProject.getIncludedData(entries[j]);

									// for each row calculate p-value
									ComputePval ob = new ComputePval(data, shuffList, _T);
									try {
										double[] pv = ob.doComputation();
										pvres.add(pv[1]);
										corrres.add(pv[0]);
										double thisrVal = 0;
										CorrelationMeta temp = new CorrelationMeta(pv[0], pv[1]);
										String thisgeneName = myProject.getRowName(entries[j])[myProject
												.getDefaultColumn()].toString();
										temp.settargetName(thisgeneName);
										corrMetaResList.add(temp);

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

						@Override
						public void finished() {
							if ((!progress.isCanceled()) && (!errored)) {
								final Number[] result = new Number[myProject.getRowCount()];
								for (int i = 0; i < corrres.size(); i++) {
									// result[entries[i]] = corrres.get(i);
									result[entries[i]] = new CorrelationValue(corrres.get(i));
								}
								myProject.setLastCorrelation(result, nameSave);
								CorrelationMetaCollection cmcObj = new CorrelationMetaCollection(nameSave, 1,
										"ShuffleAll", targetName, corrMetaResList);
								myProject.addMetaCorrRes(nameSave, cmcObj);
							}
							progress.dispose();
						}
					};
					analyzeWorker.start();
					progress.setVisible(true);
				} else if ("mutualInformation".equals(e.getActionCommand())) {
					// JOptionPane.showMessageDialog(null, "MI");
					// First calculate Wtmatrix for all rows
					final int[] entries = myProject.getGeneListRowNumbers(geneLists.getSelectedValue().toString());
					double[] sourceData = myProject.getIncludedData(entries[target]);
					// let User enter parameters
					int binsM = 0;
					int k = 0;
					try {
						binsM = Integer.parseInt((String) JOptionPane.showInputDialog(null,
								"Please Enter number of bins", "Input number of bins", JOptionPane.QUESTION_MESSAGE,
								null, null, String.valueOf(MetaOmGraph.getNumBins())));
						k = Integer.parseInt((String) JOptionPane.showInputDialog(null, "Please Enter the order k",
								"Input the order", JOptionPane.QUESTION_MESSAGE, null, null,
								String.valueOf(MetaOmGraph.getOrder())));
					} catch (NumberFormatException nfe) {
						JOptionPane.showMessageDialog(null, "Invalid number entered. Please try again.", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					// construct a uniform knot vector
					double[] knotVec = getTvector(k, binsM);
					//////////////////////////////////////////////////////////////////////////
					final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
							"Analyzing...", "", 0L, entries.length, true);

					// name to save results
					final String nameSave = name;
					final int bins = binsM;
					final int kFinal = k;
					// compute entropy for target
					ComputeDensityFromSpline tOb = new ComputeDensityFromSpline(sourceData, bins, kFinal, knotVec);
					double[][] targetwtMat = tOb.getWeightMatrix();

					double targetH = tOb.getEntropy(targetwtMat);

					SwingWorker analyzeWorker = new SwingWorker() {
						boolean errored = false;
						// List<Double> entropyList = new ArrayList<>();
						// List<Double> jointEntropyList = new ArrayList<>();
						List<Double> mutualInfoList = new ArrayList<>();

						@Override
						public Object construct() {
							try {
								// for each data row do
								// Number[] result = null;
								// long startTime = System.nanoTime();
								int j = 0;

								do {
									progress.setProgress(j);
									double[] data = myProject.getIncludedData(entries[j]);

									// for each row get wtMatrix and compute MI
									ComputeDensityFromSpline o = new ComputeDensityFromSpline(data, bins, kFinal,
											knotVec);
									double[][] thiswtMat = o.getWeightMatrix();

									/*
									 * double thisSum = tOb.checkWtMatrix(thiswtMat); if (Math.round(thisSum) != 1)
									 * { JOptionPane.showMessageDialog(null, "Test failed for j: " + j
									 * +"Sum:"+thisSum+ " name:" +
									 * myProject.getRowName(entries[j])[myProject.getDefaultColumn()] .toString());
									 * }
									 */

									double thisEntropy = o.getEntropy(thiswtMat);
									double thisJointEntropy = o.getJointEntropy(targetwtMat, thiswtMat);
									double thisMI = targetH + thisEntropy - thisJointEntropy;

									mutualInfoList.add(thisMI);

									j++;
									if (j >= entries.length)
										break;
								} while (!progress.isCanceled());

								// following code runs out of memory as it stores all the double matrices
								// after getting all wtMat compute Entropies execute in parallel
								// ComputeEntropy ceOb = new ComputeEntropy(wtMatlist, threads);
								// entropyList = ceOb.getEntropy();
								/// after getting all wtMat compute JointEntropies execute in parallel
								// ComputeJointEntropy jeOb = new ComputeJointEntropy(wtMatlist,
								// wtMatlist.get(target), threads);
								// jointEntropyList = jeOb.getJointEntropy();
								// finally compute mutual information of target row with all other using entropy
								// and joint entropy
								/*
								 * for (int i = 0; i < entropyList.size(); i++) { mutualInfoList.add(
								 * entropyList.get(target) + entropyList.get(i) - jointEntropyList.get(i)); }
								 */

								// long endTime = System.nanoTime();
								// long duration = (endTime - startTime);
								// JOptionPane.showMessageDialog(null,"Size:" + wtMatlist.size() + "Time:" +
								// duration / 1000000000);
								// JOptionPane.showMessageDialog(null, entropyList.subList(1, 8).toString());
								// JOptionPane.showMessageDialog(null, jointEntropyList.subList(1,
								// 8).toString());

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

						@Override
						public void finished() {
							if ((!progress.isCanceled()) && (!errored)) {
								final Number[] result = new Number[myProject.getRowCount()];
								for (int i = 0; i < mutualInfoList.size(); i++) {

									result[entries[i]] = new CorrelationValue(mutualInfoList.get(i));
								}
								myProject.setLastCorrelation(result, nameSave);

							}
							progress.dispose();
						}
					};
					analyzeWorker.start();
					progress.setVisible(true);
					/////////////////////////////////////////////////////////////////////////
				}

				else if ("mutualInformation3".equals(e.getActionCommand())) {
					// shuffle all
					// get significance using permutation test
					final int[] entries = myProject.getGeneListRowNumbers(geneLists.getSelectedValue().toString());
					double[] sourceData = myProject.getIncludedData(entries[target]);
					int _N = MetaOmGraph.getNumPermutations();
					int _T = MetaOmGraph.getNumThreads();
					// let User enter parameters
					int binsM = 0;
					int k = 0;
					try {
						binsM = Integer.parseInt((String) JOptionPane.showInputDialog(null,
								"Please Enter number of bins", "Input number of bins", JOptionPane.QUESTION_MESSAGE,
								null, null, String.valueOf(MetaOmGraph.getNumBins())));
						k = Integer.parseInt((String) JOptionPane.showInputDialog(null, "Please Enter the order k",
								"Input the order", JOptionPane.QUESTION_MESSAGE, null, null,
								String.valueOf(MetaOmGraph.getOrder())));
					} catch (NumberFormatException nfe) {
						JOptionPane.showMessageDialog(null, "Invalid number entered. Please try again.", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					// construct a uniform knot vector
					double[] knotVec = getTvector(k, binsM);
					//////////////////////////////////////////////////////////////////////////
					final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
							"Analyzing...", "", 0L, entries.length, true);
					// create metacorrobj
					List<CorrelationMeta> corrMetaResList = new ArrayList<>();
					// name to save results
					final String nameSave = name;
					final int bins = binsM;
					final int kFinal = k;
					// compute entropy for target
					ComputeDensityFromSpline tOb = new ComputeDensityFromSpline(sourceData, bins, kFinal, knotVec);
					double[][] targetwtMat = tOb.getWeightMatrix();

					// JOptionPane.showMessageDialog(null, "OrigMat:" +
					// Arrays.deepToString(targetwtMat));
					// create a list of shuffled targetwtMat
					// int _N = 100;
					List<double[][]> shuffList = new ArrayList<>();
					shuffList.add(targetwtMat); // shuffle _N time to create a list of
												// shuffledlists
					List<Integer> indices = IntStream.range(0, targetwtMat[0].length).boxed()
							.collect(Collectors.toList());// indices of
															// columns
															// 0..num_Cols
					for (int i = 0; i < _N; i++) {
						// create _N shuffled mat and add to list
						java.util.Collections.shuffle(indices);
						double[][] tempMat = new double[targetwtMat.length][targetwtMat[0].length];
						for (int m = 0; m < tempMat.length; m++) {
							for (int n = 0; n < tempMat[0].length; n++) {
								// copy indices.get(n) column from targetwtMat to nth place in tempmat
								tempMat[m][n] = targetwtMat[m][indices.get(n)];
							}
						}
						// JOptionPane.showMessageDialog(null, "this shuff:" +
						// Arrays.deepToString(tempMat));
						shuffList.add(tempMat);
					}
					// calculate entopies of all suffled matrices. at index 0 will be original
					// entropy
					ComputeEntropy ceob = new ComputeEntropy(shuffList, 6);
					List<Double> shuffEntropies = ceob.getEntropy();
					// JOptionPane.showMessageDialog(null, "TH:"+targetH+"
					// L0:"+shuffEntropies.get(0));

					SwingWorker analyzeWorker = new SwingWorker() {
						boolean errored = false;

						// List<Double> entropyList = new ArrayList<>();
						// List<Double> jointEntropyList = new ArrayList<>();
						List<Double> mutualInfoList = new ArrayList<>();
						// get entopies

						double targetH = shuffEntropies.get(0);

						@Override
						public Object construct() {
							try {
								// for each data row do
								// Number[] result = null;
								// long startTime = System.nanoTime();
								int j = 0;

								do {
									progress.setProgress(j);
									double[] data = myProject.getIncludedData(entries[j]);
									// for each row get wtMatrix and compute MI
									ComputeDensityFromSpline o = new ComputeDensityFromSpline(data, bins, kFinal,
											knotVec);
									double[][] thiswtMat = o.getWeightMatrix();
									// double thisEntropy = o.getEntropy(thiswtMat);
									// double thisJointEntropy = o.getJointEntropy(targetwtMat, thiswtMat);
									// double thisMI = targetH + thisEntropy - thisJointEntropy;
									// mutualInfoList.add(thisMI);
									// calculate joint entropy pval and MI
									ComputeJointEntropy cjeob = new ComputeJointEntropy(shuffList, thiswtMat, _T);
									List<Double> jointEntropies = null;
									try {
										jointEntropies = cjeob.getJointEntropy();
									} catch (InterruptedException | ExecutionException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									double thisEntropy = o.getEntropy(thiswtMat);
									double thisJointEntropy = jointEntropies.get(0);
									double thisMI = targetH + thisEntropy - thisJointEntropy;
									mutualInfoList.add(thisMI);
									// compute p-val
									double sum = 0;
									for (int l = 1; l < jointEntropies.size(); l++) {
										double lMI = shuffEntropies.get(l) + thisEntropy - jointEntropies.get(l);
										if (lMI >= thisMI) {
											sum += 1;
										}
									}
									double thisPval = sum / _N;

									// create object to save p-vals
									CorrelationMeta temp = new CorrelationMeta(thisMI, thisPval);
									String thisgeneName = myProject.getRowName(entries[j])[myProject.getDefaultColumn()]
											.toString();
									temp.settargetName(thisgeneName);
									corrMetaResList.add(temp);

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

						@Override
						public void finished() {
							if ((!progress.isCanceled()) && (!errored)) {

								final Number[] result = new Number[myProject.getRowCount()];
								for (int i = 0; i < mutualInfoList.size(); i++) {

									result[entries[i]] = new CorrelationValue(mutualInfoList.get(i));
								}

								myProject.setLastCorrelation(result, nameSave);
								CorrelationMetaCollection cmcObj = new CorrelationMetaCollection(nameSave, 3,
										"ShuffleAll", targetName, corrMetaResList, bins, kFinal);
								myProject.addMetaCorrRes(nameSave, cmcObj);
							}
							progress.dispose();
							System.gc();
						}
					};
					analyzeWorker.start();
					progress.setVisible(true);
				}

				else if ("mutualInformation2".equals(e.getActionCommand())) {
					// shuffle only within groups
					// get significance using permutation test
					final int[] entries = myProject.getGeneListRowNumbers(geneLists.getSelectedValue().toString());
					double[] sourceData = myProject.getIncludedData(entries[target]);
					int _N = MetaOmGraph.getNumPermutations();
					int _T = MetaOmGraph.getNumThreads();
					// let User enter parameters
					int binsM = 0;
					int k = 0;
					try {
						binsM = Integer.parseInt((String) JOptionPane.showInputDialog(null,
								"Please Enter number of bins", "Input number of bins", JOptionPane.QUESTION_MESSAGE,
								null, null, String.valueOf(MetaOmGraph.getNumBins())));
						k = Integer.parseInt((String) JOptionPane.showInputDialog(null, "Please Enter the order k",
								"Input the order", JOptionPane.QUESTION_MESSAGE, null, null,
								String.valueOf(MetaOmGraph.getOrder())));
					} catch (NumberFormatException nfe) {
						JOptionPane.showMessageDialog(null, "Invalid number entered. Please try again.", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					// construct a uniform knot vector
					double[] knotVec = getTvector(k, binsM);
					//////////////////////////////////////////////////////////////////////////
					final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
							"Analyzing...", "", 0L, entries.length, true);
					// create metacorrobj
					List<CorrelationMeta> corrMetaResList = new ArrayList<>();
					// name to save results
					final String nameSave = name;
					final int bins = binsM;
					final int kFinal = k;
					// compute entropy for target
					ComputeDensityFromSpline tOb = new ComputeDensityFromSpline(sourceData, bins, kFinal, knotVec);
					double[][] targetwtMat = tOb.getWeightMatrix();

					// JOptionPane.showMessageDialog(null, "Orig mat:"
					// +Arrays.deepToString(targetwtMat));
					// JOptionPane.showMessageDialog(null, "Orig mat dim:" + targetwtMat.length + "
					// " + targetwtMat[0].length);
					/////////////////////
					List<double[][]> shuffList = new ArrayList<>();
					// add original data to calculate original correlation val which will be at
					// index 0
					// shuffle _N time only within groups
					if (myProject.getMetadataHybrid() == null) {
						JOptionPane.showMessageDialog(null, "No metadata loaded", "Metadata not found",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					///////////////////////// shuffle within group////////////////////////
					TreeMap<String, List<Integer>> groupsMap = myProject.getMetadataHybrid().getDefaultRepsMap();
					// this array contains colindex of the datacolumns used
					int[] sourceDataColNumbers = new int[sourceData.length];
					boolean[] exclude = MetaOmAnalyzer.getExclude();

					SwingWorker analyzeWorker = new SwingWorker() {
						boolean errored = false;

						// List<Double> entropyList = new ArrayList<>();
						// List<Double> jointEntropyList = new ArrayList<>();
						List<Double> mutualInfoList = new ArrayList<>();
						// get entopies

						@Override
						public Object construct() {
							try {
								// for each data row do
								// add original Matrix to calculate original correlation val which will be at
								// index 0
								shuffList.add(targetwtMat);
								List<int[]> shuffInd = groupDataIndexbyRepColumn(groupsMap, exclude,
										sourceDataColNumbers, _N);
								// add to shuffList
								for (int j = 0; j < shuffInd.size(); j++) {
									double[][] tempMat = new double[targetwtMat.length][targetwtMat[0].length];
									int[] newInd = shuffInd.get(j);
									for (int m = 0; m < tempMat.length; m++) {
										for (int n = 0; n < tempMat[0].length; n++) {
											// copy indices.get(n) column from targetwtMat to nth place in tempmat
											tempMat[m][n] = targetwtMat[m][newInd[n]];
										}
									}
									shuffList.add(tempMat);
								}
								int j = 0;
								int _N = MetaOmGraph.getNumPermutations();
								int _T = MetaOmGraph.getNumThreads();

								//////////////////////////////////
								// calculate entopies of all shuffled matrices. at index 0 will be original
								// entropy
								ComputeEntropy ceob = new ComputeEntropy(shuffList, _T);
								List<Double> shuffEntropies = null;
								try {
									shuffEntropies = ceob.getEntropy();
								} catch (InterruptedException | ExecutionException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								double targetH = shuffEntropies.get(0);

								do {
									progress.setProgress(j);
									double[] data = myProject.getIncludedData(entries[j]);
									// for each row get wtMatrix and compute MI
									ComputeDensityFromSpline o = new ComputeDensityFromSpline(data, bins, kFinal,
											knotVec);
									double[][] thiswtMat = o.getWeightMatrix();
									// double thisEntropy = o.getEntropy(thiswtMat);
									// double thisJointEntropy = o.getJointEntropy(targetwtMat, thiswtMat);
									// double thisMI = targetH + thisEntropy - thisJointEntropy;
									// mutualInfoList.add(thisMI);

									// calculate joint entropy pval and MI
									ComputeJointEntropy cjeob = new ComputeJointEntropy(shuffList, thiswtMat, _T);
									List<Double> jointEntropies = null;
									try {
										jointEntropies = cjeob.getJointEntropy();
									} catch (InterruptedException | ExecutionException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									double thisEntropy = o.getEntropy(thiswtMat);
									double thisJointEntropy = jointEntropies.get(0);
									double thisMI = targetH + thisEntropy - thisJointEntropy;
									mutualInfoList.add(thisMI);
									// compute p-val
									double sum = 0;
									// start from as 0th is the original data
									for (int l = 1; l < jointEntropies.size(); l++) {
										double lMI = shuffEntropies.get(l) + thisEntropy - jointEntropies.get(l);
										if (lMI >= thisMI) {
											sum += 1;
										}
									}
									double thisPval = sum / _N;

									// create object to save p-vals
									CorrelationMeta temp = new CorrelationMeta(thisMI, thisPval);
									String thisgeneName = myProject.getRowName(entries[j])[myProject.getDefaultColumn()]
											.toString();
									temp.settargetName(thisgeneName);
									corrMetaResList.add(temp);
									// s

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

						@Override
						public void finished() {
							if ((!progress.isCanceled()) && (!errored)) {

								final Number[] result = new Number[myProject.getRowCount()];
								for (int i = 0; i < mutualInfoList.size(); i++) {
									// result[entries[i]] = mutualInfoList.get(i);
									result[entries[i]] = new CorrelationValue(mutualInfoList.get(i));
								}

								myProject.setLastCorrelation(result, nameSave);
								CorrelationMetaCollection cmcObj = new CorrelationMetaCollection(nameSave, 3,
										"ShuffleWithInGrps", targetName, corrMetaResList, bins, kFinal);

								myProject.addMetaCorrRes(nameSave, cmcObj);
							}
							progress.dispose();
							System.gc();
						}
					};
					analyzeWorker.start();
					progress.setVisible(true);
				}

				else if ("spearman correlation".equals(e.getActionCommand())) {
					MetaOmAnalyzer.doAnalysis(myProject, geneLists.getSelectedValue().toString(), target, name, 2);
				}

				else if ("spearman correlation2".equals(e.getActionCommand())) {
					final int[] entries = myProject.getGeneListRowNumbers(geneLists.getSelectedValue().toString());
					double[] sourceData = myProject.getIncludedData(entries[target]);
					double[] sourceDataAll = myProject.getAllData(entries[target]);
					int _N = MetaOmGraph.getNumPermutations();
					int _T = MetaOmGraph.getNumThreads();

					// do _N permutations. Start
					List<Double> sourceDataList = new ArrayList<>();
					for (double d : sourceData) {
						sourceDataList.add(d);
					}
					List<double[]> shuffList = new ArrayList<double[]>();
					// add original data to calculate original correlation val which will be at
					// index 0
					// shuffle _N time only within groups
					if (myProject.getMetadataHybrid() == null) {
						JOptionPane.showMessageDialog(null, "No metadata loaded", "Metadata not found",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					// add original data to calculate original correlation val which will be at
					// index 0
					shuffList.add(sourceData);
					///////////////////////// shuffle within group////////////////////////
					TreeMap<String, List<Integer>> groupsMap = myProject.getMetadataHybrid().getDefaultRepsMap();
					// this array contains colindex of the datacolumns used
					int[] sourceDataColNumbers = new int[sourceData.length];
					boolean[] exclude = MetaOmAnalyzer.getExclude();

					final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
							"Analyzing...", "", 0L, entries.length, true);
					// create metacorrobj
					List<CorrelationMeta> corrMetaResList = new ArrayList<>();
					// name to save results
					final String nameSave = name;
					SwingWorker analyzeWorker = new SwingWorker() {
						boolean errored = false;
						List<Double> pvres = new ArrayList<>();
						List<Double> corrres = new ArrayList<>();

						@Override
						public Object construct() {
							try {
								// for each data row do
								List<int[]> shuffInd = groupDataIndexbyRepColumn(groupsMap, exclude,
										sourceDataColNumbers, _N);
								for (int j = 0; j < shuffInd.size(); j++) {
									double[] tempArr = new double[sourceData.length];
									int[] newInd = shuffInd.get(j);
									for (int n = 0; n < tempArr.length; n++) {
										// copy indices.get(n) column from targetwtMat to nth place in tempmat
										tempArr[n] = sourceData[newInd[n]];
									}
									shuffList.add(tempArr);
								}
								int j = 0;

								do {
									progress.setProgress(j);
									double[] data = myProject.getIncludedData(entries[j]);

									// for each row calculate p-value
									ComputePval ob = new ComputePval(data, shuffList, _T);
									try {
										double[] pv = ob.doComputationSpearman();

										pvres.add(pv[1]);
										corrres.add(pv[0]);
										// double thisrVal = 0;
										CorrelationMeta temp = new CorrelationMeta(pv[0], pv[1]);
										String thisgeneName = myProject.getRowName(entries[j])[myProject
												.getDefaultColumn()].toString();
										temp.settargetName(thisgeneName);
										corrMetaResList.add(temp);
										// JOptionPane.showMessageDialog(null, "P val for row:"+j+" is:"+pv);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (ExecutionException e2) {
										// TODO Auto-generated catch block
										e2.printStackTrace();
									}

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

						@Override
						public void finished() {
							if ((!progress.isCanceled()) && (!errored)) {
								final Number[] result = new Number[myProject.getRowCount()];
								for (int i = 0; i < corrres.size(); i++) {
									// result[entries[i]] = corrres.get(i);
									result[entries[i]] = new CorrelationValue(corrres.get(i));
								}
								myProject.setLastCorrelation(result, nameSave);
								// String s=String.valueOf(name);
								CorrelationMetaCollection cmcObj = new CorrelationMetaCollection(nameSave, 2, "Shuffle",
										targetName, corrMetaResList);
								myProject.addMetaCorrRes(nameSave, cmcObj);
							}
							progress.dispose();
						}
					};
					analyzeWorker.start();
					progress.setVisible(true);
				} else if ("spearman correlation3".equals(e.getActionCommand())) {
					// shuffle all
					// get significance using permutation test
					final int[] entries = myProject.getGeneListRowNumbers(geneLists.getSelectedValue().toString());
					double[] sourceData = myProject.getIncludedData(entries[target]);
					int _N = MetaOmGraph.getNumPermutations();
					int _T = MetaOmGraph.getNumThreads();

					// do _N permutations
					List<Double> sourceDataList = new ArrayList<>();
					for (double d : sourceData) {
						sourceDataList.add(d);
					}
					List<double[]> shuffList = new ArrayList<double[]>();
					// add original data to calculate original correlation val which will be at
					// index 0

					shuffList.add(sourceData); // shuffle _N time to create a list of shuffledlists
					for (int i = 0; i < _N; i++) {
						java.util.Collections.shuffle(sourceDataList);
						double[] tempArr = new double[sourceData.length];
						int k = 0;
						for (double d : sourceDataList) {
							tempArr[k++] = d;
						}
						shuffList.add(tempArr);
					}

					final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
							"Analyzing...", "", 0L, entries.length, true);
					List<CorrelationMeta> corrMetaResList = new ArrayList<>();
					// name to save results
					final String nameSave = name;
					SwingWorker analyzeWorker = new SwingWorker() {
						boolean errored = false;
						List<Double> pvres = new ArrayList<>();
						List<Double> corrres = new ArrayList<>();

						@Override
						public Object construct() {
							try {
								// for each data row do
								// Number[] result = null;
								int j = 0;

								do {
									progress.setProgress(j);
									double[] data = myProject.getIncludedData(entries[j]);

									// for each row calculate p-value
									ComputePval ob = new ComputePval(data, shuffList, _T);
									try {
										double[] pv = ob.doComputationSpearman();
										pvres.add(pv[1]);
										corrres.add(pv[0]);
										// double thisrVal = 0;
										CorrelationMeta temp = new CorrelationMeta(pv[0], pv[1]);
										String thisgeneName = myProject.getRowName(entries[j])[myProject
												.getDefaultColumn()].toString();
										temp.settargetName(thisgeneName);
										corrMetaResList.add(temp);

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

						@Override
						public void finished() {
							if ((!progress.isCanceled()) && (!errored)) {
								final Number[] result = new Number[myProject.getRowCount()];
								for (int i = 0; i < corrres.size(); i++) {
									// add value as CorrelationValue object

									result[entries[i]] = new CorrelationValue(corrres.get(i));
								}
								myProject.setLastCorrelation(result, nameSave);
								CorrelationMetaCollection cmcObj = new CorrelationMetaCollection(nameSave, 2,
										"ShuffleAll", targetName, corrMetaResList);
								myProject.addMetaCorrRes(nameSave, cmcObj);
							}
							progress.dispose();
						}
					};
					analyzeWorker.start();
					progress.setVisible(true);
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
			} catch (

			IOException ioe) {
				JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Error reading project data", "IOException",
						0);
				ioe.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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

			int nameCol = -1;
			for (int i = 0; (i < options.length) && (nameCol < 0); i++) {
				if (options[i].equals(result.toString())) {
					nameCol = i - 1;
				}
			}

			if ("pairwise pearson".equals(e.getActionCommand())) {
				try {
					// measure time
					// long startTime = System.nanoTime();

					MetaOmAnalyzer.pairwise(myProject, geneLists.getSelectedValue().toString(), nameCol, 1);
					// long endTime = System.nanoTime();
					// get difference of two nanoTime values
					// float timeElapsed = endTime - startTime;
					// timeElapsed = (timeElapsed / (float) 1000000000.00);
					// JOptionPane.showMessageDialog(null, "Time taken:" + timeElapsed);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				try {
					MetaOmAnalyzer.pairwise(myProject, geneLists.getSelectedValue().toString(), nameCol, 2);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			return;
		}

		if (("mutualInformationPairs".equals(e.getActionCommand()))) {
			// JOptionPane.showMessageDialog(null, "PairwiseMI");
			String[] names = myProject.getInfoColumnNames();
			String[] options = new String[names.length + 1];
			options[0] = "Row Number";
			System.arraycopy(names, 0, options, 1, names.length);

			Object result = JOptionPane.showInputDialog(this, "Which identifiers should be used for the result?",
					"Pairwise Mutual Information", 3, null, options, options[0]);
			if (result == null)
				return;
			int nameCol = -100;
			for (int i = 0; (i < options.length) && (nameCol < 0); i++) {
				if (options[i].equals(result.toString())) {
					nameCol = i - 1;
				}
			}

			int binsM = 0;
			int k = 0;
			try {
				binsM = Integer.parseInt((String) JOptionPane.showInputDialog(null, "Please Enter number of bins",
						"Input number of bins", JOptionPane.QUESTION_MESSAGE, null, null,
						String.valueOf(MetaOmGraph.getNumBins())));
				k = Integer.parseInt(
						(String) JOptionPane.showInputDialog(null, "Please Enter the order k", "Input the order",
								JOptionPane.QUESTION_MESSAGE, null, null, String.valueOf(MetaOmGraph.getOrder())));
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "Invalid number entered. Please try again.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			// construct a uniform knot vector
			double[] knotVec = getTvector(k, binsM);

			// call function
			try {
				// long startTime = System.nanoTime();
				MetaOmAnalyzer.pairwiseMI(myProject, geneLists.getSelectedValue().toString(), nameCol, binsM, k,
						knotVec, false);
				// long endTime = System.nanoTime();
				// long totalTime = endTime - startTime;
				// JOptionPane.showMessageDialog(null, "Time taken by old: "+totalTime/1000);

				// s
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		// relatedness matrix
		if (("relatednessPairs".equals(e.getActionCommand()))) {
			// JOptionPane.showMessageDialog(null, "PairwiseMI");
			String[] names = myProject.getInfoColumnNames();
			String[] options = new String[names.length + 1];
			options[0] = "Row Number";
			System.arraycopy(names, 0, options, 1, names.length);

			Object result = JOptionPane.showInputDialog(this, "Which identifiers should be used for the result?",
					"Pairwise Mutual Information", 3, null, options, options[0]);
			if (result == null)
				return;
			int nameCol = -100;
			for (int i = 0; (i < options.length) && (nameCol < 0); i++) {
				if (options[i].equals(result.toString())) {
					nameCol = i - 1;
				}
			}

			int binsM = 0;
			int k = 0;
			try {
				binsM = Integer.parseInt((String) JOptionPane.showInputDialog(null, "Please Enter number of bins",
						"Input number of bins", JOptionPane.QUESTION_MESSAGE, null, null,
						String.valueOf(MetaOmGraph.getNumBins())));
				k = Integer.parseInt(
						(String) JOptionPane.showInputDialog(null, "Please Enter the order k", "Input the order",
								JOptionPane.QUESTION_MESSAGE, null, null, String.valueOf(MetaOmGraph.getOrder())));
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "Invalid number entered. Please try again.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			// construct a uniform knot vector
			double[] knotVec = getTvector(k, binsM);

			// call function
			try {
				// long startTime = System.nanoTime();
				MetaOmAnalyzer.pairwiseMI(myProject, geneLists.getSelectedValue().toString(), nameCol, binsM, k,
						knotVec, true);
				// long endTime = System.nanoTime();
				// long totalTime = endTime - startTime;
				// JOptionPane.showMessageDialog(null, "Time taken by new: "+totalTime/1000);

				// compute relatedness from MI matrix

				// s
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		if (("DiffCorrelation".equals(e.getActionCommand()))) {

			// calculate r and p values using each group
			// create 4 lists r1,pv1,r2,pv2
			// select two corr columns and do z test
			String col1 = selectCorrColumn();
			if (col1 == null) {
				JOptionPane.showMessageDialog(null, "No correlation columns found!", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			int n1 = 0;
			try {
				n1 = Integer.parseInt((String) JOptionPane.showInputDialog(null,
						"Please Enter the sample size for selected correlation (N1)", "Input N1",
						JOptionPane.QUESTION_MESSAGE, null, null, String.valueOf(n1)));

			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "Invalid integer entered. Please try again.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			String col2 = selectCorrColumn();
			if (col2 == null) {
				JOptionPane.showMessageDialog(null, "No correlation columns found!", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			int n2 = 0;
			try {
				n2 = Integer.parseInt((String) JOptionPane.showInputDialog(null,
						"Please Enter the sample size for selected correlation (N2)", "Input N2",
						JOptionPane.QUESTION_MESSAGE, null, null, String.valueOf(n2)));

			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "Invalid integer entered. Please try again.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// get data for both cols
			List<Double> corrVals1 = getCorrData(col1);
			List<Double> corrVals2 = getCorrData(col2);
			List<String> featureNames = new ArrayList<>();

			for (int i = 0; i < listDisplay.getRowCount(); i++) {
				featureNames.add((String) listDisplay.getValueAt(i,
						listDisplay.convertColumnIndexToView(myProject.getDefaultColumn())));
			}
			// calculate z values and p values for the corrValues

			List<Double> zVals1 = CalculateDiffCorr.getConveredttoZ(corrVals1);
			List<Double> zVals2 = CalculateDiffCorr.getConveredttoZ(corrVals2);
			List<Double> diffZvals = CalculateDiffCorr.getDiff(zVals1, zVals2);
			List<Double> zScores = CalculateDiffCorr.computeZscores(diffZvals, n1, n2);
			List<Double> pValues = CalculateDiffCorr.computePVals(zScores);

			DiffCorrResultsTable frame = new DiffCorrResultsTable(featureNames, n1, n2, corrVals1, corrVals2, zVals1,
					zVals2, diffZvals, zScores, pValues, myProject);
			frame.setSize(MetaOmGraph.getMainWindow().getWidth() / 2, MetaOmGraph.getMainWindow().getHeight() / 2);
			frame.setTitle("Fold change results");
			MetaOmGraph.getDesktop().add(frame);
			frame.setVisible(true);

		}

		if (("NewDiffCorrelation".equals(e.getActionCommand()))) {
			if (myProject.getMetadataHybrid() == null) {
				JOptionPane.showMessageDialog(null, "No metadata read", "No metadata", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (listDisplay.getSelectedRowCount() < 1) {
				JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Please select a row to analyze!", "Error",
						0);
				return;
			}

			if (listDisplay.getSelectedRowCount() > 1) {
				JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Please select only one row to analyze!",
						"Error", 0);
				return;
			}

			int selectedInd = getTrueSelectedRow();
			String rowName = getSelectedGeneName();
			DifferentialCorrFrame lframe = new DifferentialCorrFrame(geneLists.getSelectedValue().toString(), rowName,
					selectedInd);
			lframe.setSize(MetaOmGraph.getMainWindow().getWidth() / 2, MetaOmGraph.getMainWindow().getHeight() / 2);
			MetaOmGraph.getDesktop().add(lframe);
			lframe.setVisible(true);
			return;

		}

		if (("LoadDiffCorrelation".equals(e.getActionCommand()))) {
			String[] listOfDC = myProject.getSavedDiffCorrResNames();
			if (listOfDC == null || listOfDC.length < 1) {
				JOptionPane.showMessageDialog(null, "No saved results found", "No results",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			// JOptionPane.showMessageDialog(null, "saved" + Arrays.toString(listOfDE));

			// choose one from the available results
			String chosenVal = (String) JOptionPane.showInputDialog(null, "Choose the DE analysis", "Please choose",
					JOptionPane.PLAIN_MESSAGE, null, listOfDC, listOfDC[0]);
			if (chosenVal == null) {
				return;
			}

			// display chosen value
			DifferentialCorrResults diffcorrresOB = myProject.getDiffCorrResObj(chosenVal);
			// display result using DiffCorrResultsTable

			DiffCorrResultsTable frame = new DiffCorrResultsTable(diffcorrresOB.getFeatureNames(),
					diffcorrresOB.getGrp1Size(), diffcorrresOB.getGrp2Size(), diffcorrresOB.getCorrGrp1(),
					diffcorrresOB.getCorrGrp2(), diffcorrresOB.getzVals(1), diffcorrresOB.getzVals(2),
					diffcorrresOB.getDiffZVals(), diffcorrresOB.getzScores(), diffcorrresOB.getpValues(), myProject);
			frame.setSize(MetaOmGraph.getMainWindow().getWidth() / 2, MetaOmGraph.getMainWindow().getHeight() / 2);
			MetaOmGraph.getDesktop().add(frame);
			frame.setVisible(true);

			return;
		}

		if (("RemoveDiffCorrelation".equals(e.getActionCommand()))) {

			String[] listOfDC = myProject.getSavedDiffCorrResNames();
			if (listOfDC == null || listOfDC.length < 1) {
				JOptionPane.showMessageDialog(null, "No saved results found", "No results",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			// JOptionPane.showMessageDialog(null, "saved" + Arrays.toString(listOfDE));

			// choose one from the available results
			String chosenVal = (String) JOptionPane.showInputDialog(null, "Choose the DE analysis", "Please choose",
					JOptionPane.PLAIN_MESSAGE, null, listOfDC, listOfDC[0]);
			if (chosenVal == null) {
				return;
			}

			int opt = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected?", "Confirm", 0,
					3);
			if (opt != 0) {
				return;
			}

			myProject.removeDiffCorrResults(chosenVal);
			return;

		}
		

	}

	private Map<String, Collection<Integer>> createSplitIndex() {
		Map<String, Collection<Integer>> splitIndex;
		// show metadata categories
		String[] fields = MetaOmGraph.getActiveProject().getMetadataHybrid().getMetadataHeaders();
		String[] fields2 = new String[fields.length + 2];
		for (int i = 0; i < fields.length; i++) {
			fields2[i] = fields[i];
		}
		fields2[fields2.length - 2] = "By Query";
		fields2[fields2.length - 1] = "More...";

		String col_val = (String) JOptionPane.showInputDialog(null, "Choose the column:\n", "Please choose",
				JOptionPane.PLAIN_MESSAGE, null, fields2, fields2[0]);
		if (col_val == null) {
			return null;
		}

		List<String> selectedVals = new ArrayList<>();
		if (col_val.equals("More...")) {
			// display jpanel with check box
			JCheckBox[] cBoxes = new JCheckBox[fields.length];
			JPanel cbPanel = new JPanel();
			cbPanel.setLayout(new GridLayout(0, 3));
			for (int i = 0; i < fields.length; i++) {
				cBoxes[i] = new JCheckBox(fields[i]);
				cbPanel.add(cBoxes[i]);
			}
			int res = JOptionPane.showConfirmDialog(null, cbPanel, "Select categories", JOptionPane.OK_CANCEL_OPTION);
			if (res == JOptionPane.OK_OPTION) {
				for (int i = 0; i < fields.length; i++) {
					if (cBoxes[i].isSelected()) {
						selectedVals.add(fields[i]);
					}
				}

			} else {
				return null;
			}
			splitIndex = myProject.getMetadataHybrid().cluster(selectedVals);

		} else if (col_val.equals("By Query")) {

			// display query panel
			final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(myProject, false);
			final MetadataQuery[] queries;
			queries = tsp.showSearchDialog();
			if (tsp.getQueryCount() <= 0) {
				System.out.println("Search dialog cancelled");
				// User didn't enter any queries
				return null;
			}
			// final int[] result = new int[myProject.getDataColumnCount()];
			Collection<Integer> result = new ArrayList<>();
			List<Collection<Integer>> resList = new ArrayList<>();
			final boolean nohits;
			new AnimatedSwingWorker("Searching...", true) {
				@Override
				public Object construct() {
					ArrayList<Integer> toAdd = new ArrayList<Integer>(result.size());
					for (int i = 0; i < myProject.getDataColumnCount(); i++) {
						toAdd.add(i);
					}
					Integer[] hits = myProject.getMetadataHybrid().search(queries, tsp.matchAll());
					// remove excluded cols from list
					// urmi
					boolean[] excluded = MetaOmAnalyzer.getExclude();
					if (excluded != null) {
						List<Integer> temp = new ArrayList<>();
						for (Integer i : hits) {
							if (!excluded[i]) {
								temp.add(i);
							}
						}
						hits = new Integer[temp.size()];
						hits = temp.toArray(hits);
					}

					int index;
					for (index = 0; index < hits.length; index++) {
						result.add(hits[index]);
						toAdd.remove(hits[index]);
					}
					/*
					 * for (int i = 0; i < toAdd.size(); i++) { other.add(toAdd.get(i)); }
					 */
					resList.add(result);
					resList.add(toAdd);
					return null;
				}
			}.start();

			// create a split index with "hits" as one category and all others as second
			// category
			if (resList.get(0).size() < 1) {
				JOptionPane.showMessageDialog(null, "No hits found", "No hits", JOptionPane.INFORMATION_MESSAGE);
				return null;
			}
			splitIndex = createSplitIndex(resList, Arrays.asList("Hits", "Other"));
		} else {
			// split data set by values of col_val
			selectedVals.add(col_val);
			splitIndex = myProject.getMetadataHybrid().cluster(selectedVals);
		}

		return splitIndex;

	}

	/**
	 * create a map of name to indices
	 * 
	 * @param collList
	 * @param names
	 * @return
	 */
	private Map<String, Collection<Integer>> createSplitIndex(List<Collection<Integer>> collList, List<String> names) {
		Map<String, Collection<Integer>> res = new TreeMap();
		for (int i = 0; i < collList.size(); i++) {
			if (collList.get(i).size() > 0) {
				res.put(names.get(i), collList.get(i));
			}

		}
		return res;
	}

	private double[] getTvector(int k, int binsM) {
		if (k >= binsM) {
			JOptionPane.showMessageDialog(null, "k<M");
		}
		double[] tVec = new double[binsM + k];

		for (int i = 0; i < tVec.length; i++) {
			if (i < k) {
				tVec[i] = 0;
			} else if (k <= i && i <= binsM) {
				tVec[i] = i - k + 1;
			} else {
				tVec[i] = binsM - 1 - k + 2;
			}
		}
		return tVec;
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

	public StripedTable getStripedTable() {
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

	/*
	 * private class MyComparator implements Comparator { private MyComparator() { }
	 * 
	 * public int compare(Object o1, Object o2) { if ((o1 == null) && (o2 == null))
	 * return 0; if (o1 == null) return 1; if (o2 == null) return -1; if
	 * (("".equals(o1)) && ("".equals(o2))) return 0; if ("".equals(o1)) return 1;
	 * if ("".equals(o2)) return -1; if (((o1 instanceof CorrelationValue)) && ((o2
	 * instanceof CorrelationValue))) { int result = ((CorrelationValue)
	 * o1).compareTo(o2); return result; } if (((o1 instanceof String)) && ((o2
	 * instanceof String))) { return ((String) o1).toLowerCase().compareTo(((String)
	 * o2).toLowerCase()); }
	 * 
	 * if (((o1 instanceof Double)) && ((o2 instanceof Double))) { return ((Double)
	 * o1).compareTo((Double) o2); }
	 * 
	 * return (o1 + "").compareTo(o2 + ""); } }
	 */

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

		@Override
		public void insertUpdate(DocumentEvent e) {
			doChange();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			doChange();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
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

	public MetadataTableDisplayPanel getMetadataTableDisplay() {
		return this.mdtablepanel;
	}

	public MetadataTreeDisplayPanel getMetadataTreeDisplay() {
		return this.extInfoPanel2;
	}

	public String getMainTableItemat(int r, int c) {
		if (r < 0 || r > listDisplay.getRowCount()) {
			return null;
		}
		if (c < 0 || c > listDisplay.getColumnCount()) {
			return null;
		}
		String res = listDisplay.getModel().getValueAt(r, c).toString();
		return res;
	}

	/**
	 * Group data into arrays and return a list of these arrays. Grouping is based
	 * on default rep column
	 * 
	 * @param groupsMap
	 * @param sourceDataAll
	 * @param exclude
	 * @return
	 */
	public List<double[]> groupDatabyRepColumn(TreeMap<String, List<Integer>> groupsMap, double[] sourceDataAll,
			boolean[] exclude) {
		List<double[]> sourceGrouped = new ArrayList<double[]>();

		for (Map.Entry<String, List<Integer>> entry : groupsMap.entrySet()) {
			String key = entry.getKey();
			List<Integer> value = entry.getValue();
			//value contains the indices of data column is the current group ("key")
			//return if #samples are less than 4
			if(value.size()<4) {
				JOptionPane.showMessageDialog(null, "Group with less than 4 samples found. Please check the replicate group column in properties. Can't proceed with computation.","Error",JOptionPane.ERROR_MESSAGE);
				return null;
			}
			List<Double> thisVals = new ArrayList<>();

			for (int i = 0; i < value.size(); i++) {
				try {
					// check if val exists
					// value will be -1 if datacol doesn't exist in the repsMap. Potential cause
					// extra and missing data columns. Testing required.
					int thisInd = value.get(i);
					// if datacolumn at thisValue is excluded, continue
					if (exclude != null && exclude[thisInd]) {
						// skip
						continue;
					}
					if (thisInd >= 0) {
						// thisVals[i] = sourceData[thisValue];
						thisVals.add(sourceDataAll[thisInd]);
					} else {
						// error due to mismached data and metadata runs
						JOptionPane.showMessageDialog(null, "Fix this error");
					}
				} catch (ArrayIndexOutOfBoundsException ae) {
					JOptionPane.showMessageDialog(null, "key:" + key);
					JOptionPane.showMessageDialog(null, "key:" + key + "val[i]" + value.get(i) + " sD[vi]:");
					JOptionPane.showMessageDialog(null, "key:" + key + "val[i]" + value.toString());

				}
			}

			if (thisVals.size() > 0) {
				// JOptionPane.showMessageDialog(null, "groups:" + thisVals.toString());
				sourceGrouped.add(thisVals.stream().mapToDouble(d -> d).toArray());
			}

		}

		return sourceGrouped;
	}

	/**
	 * @author urmi Group indices of data columns and create a list. Grouping is
	 *         based on default rep column. Return a list of shuffled indices to be
	 *         used on data or wt matrix
	 * 
	 * @param groupsMap
	 * @param exclude
	 * @param sourceDataColNumbers
	 * @param _N
	 * @return
	 */
	public List<int[]> groupDataIndexbyRepColumn(TreeMap<String, List<Integer>> groupsMap, boolean[] exclude,
			int[] sourceDataColNumbers, int _N) {
		List<int[]> res = new ArrayList<>();
		// get datacolindex in order of source data
		int y = 0;
		if (exclude != null) {
			for (int h = 0; h < exclude.length; h++) {
				if (!exclude[h]) {
					sourceDataColNumbers[y++] = h;
				}
			}
		} else {
			for (int h = 0; h < sourceDataColNumbers.length; h++) {
				sourceDataColNumbers[h] = h;
			}
		}

		// group all the included columns into lists and store in list sourceGroupedInd
		List<List<Integer>> sourceGroupedInd = new ArrayList<>();
		for (Map.Entry<String, List<Integer>> entry : groupsMap.entrySet()) {
			String key = entry.getKey();
			List<Integer> value = entry.getValue();
			java.util.List<Integer> thisindexs = new ArrayList<>();
			for (int i = 0; i < value.size(); i++) {
				try {
					int thisInd = value.get(i);
					if (exclude != null && exclude[thisInd]) {
						// skip
						continue;
					}
					if (thisInd >= 0) {
						thisindexs.add(thisInd);
					} else {
						// error due to mismached data and metadata runs
						JOptionPane.showMessageDialog(null, "Fix this error");
					}
				} catch (ArrayIndexOutOfBoundsException ae) {
					JOptionPane.showMessageDialog(null, "key:" + key);

				}
			}
			if (thisindexs.size() > 0) {
				/*
				 * if(thisindexs.size()>1) { JOptionPane.showMessageDialog(null, "add:" +
				 * thisindexs.toString()+" key:"+key); for(int v: value) {
				 * JOptionPane.showMessageDialog(null, "colanme:" +
				 * myProject.getDataColumnHeader(v)); } }
				 */
				sourceGroupedInd.add(thisindexs);
			}
		}

		// create an array to map colIndex to group index
		// if colIndex is 0,2,3,4,1,9
		// and 0,1,2 are in group 3,4 are in g2 9 is in g3
		// coltoGroup will look like 0,0,1,1,0,2
		int[] coltoGroup = new int[sourceDataColNumbers.length];
		for (int h = 0; h < coltoGroup.length; h++) {
			int thisColInd = sourceDataColNumbers[h];
			for (int g = 0; g < sourceGroupedInd.size(); g++) {
				if (sourceGroupedInd.get(g).contains(thisColInd)) {
					coltoGroup[h] = g;
					break;
				}
			}
		}

		// start shuffle within groups only
		for (int i = 0; i < _N; i++) {
			int[] tempArr = new int[sourceDataColNumbers.length];
			int l = 0;
			// for each int[] in sourceGrouped shuffle each group and add to shufflist
			for (int j = 0; j < sourceGroupedInd.size(); j++) {
				java.util.List<Integer> tempList = sourceGroupedInd.get(j);
				java.util.Collections.shuffle(tempList);
				int g = 0;
				for (int h = 0; h < coltoGroup.length; h++) {
					if (coltoGroup[h] == j) {
						tempArr[h] = tempList.get(g);
						g++;
						// l++;
					}
				}
			}
			int[] newInd = new int[sourceDataColNumbers.length];
			for (int j = 0; j < tempArr.length; j++) {
				for (int h = 0; h < sourceDataColNumbers.length; h++) {
					if (tempArr[j] == sourceDataColNumbers[h]) {
						newInd[j] = h;
						break;
					}
				}
			}
			// new index contains shuffled index
			res.add(newInd);
		}

		return res;
	}

	/**
	 * Choose a correlation column and return that column
	 * 
	 * @return
	 */
	public String selectCorrColumn() {
		ArrayList<Integer> colList = myProject.getCorrelationColumns();
		if ((colList == null) || (colList.size() == 0)) {
			return null;
		}

		String[] items = new String[colList.size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = myProject.getInfoColumnNames()[colList.get(i).intValue()];
			if (items[i].length() > 50) {
				items[i] = items[i].substring(0, 50) + "...";
			}
			if (items[i].equals("")) {
				items[i] = "<unnamed correlation>";
			}
		}

		String col_val = (String) JOptionPane.showInputDialog(null, "Choose the column:\n", "Please choose",
				JOptionPane.PLAIN_MESSAGE, null, items, items[0]);

		return col_val;

	}

	/**
	 * Choose a feature column and return that column
	 * 
	 * @return
	 */
	public String selectFeatureColumn() {

		// don't show correlation columns
		ArrayList<Integer> colList = myProject.getCorrelationColumns();

		String[] corrCols = new String[colList.size()];
		for (int i = 0; i < corrCols.length; i++) {
			corrCols[i] = myProject.getInfoColumnNames()[colList.get(i).intValue()];
		}
		List<String> corrColsList = Arrays.asList(corrCols);
		String[] items = myProject.getInfoColumnNames();
		List<String> newItems = new ArrayList<>();
		for (int i = 0; i < items.length; i++) {

			if (corrColsList.contains(items[i])) {
				continue;
			}

			if (items[i].length() > 50) {
				items[i] = items[i].substring(0, 50) + "...";
			}
			if (items[i].equals("")) {
				items[i] = "<unnamed>";

			}
			newItems.add(items[i]);
		}

		items = newItems.toArray(new String[0]);
		String col_val = (String) JOptionPane.showInputDialog(null, "Choose the column:\n", "Please choose",
				JOptionPane.PLAIN_MESSAGE, null, items, items[0]);

		return col_val;

	}

	/**
	 * Plot histogram of correlation values
	 */
	public void plotCorrHist(String col_val) {

		if (col_val == null || col_val.length() < 1) {
			return;
		}

		List<Double> corrVals = getCorrData(col_val);

		// create histogram

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {// get data for selected rows
					int nBins = corrVals.size() / 100;
					if (nBins < 100) {
						nBins = 100;
					}
					double[] data = corrVals.stream().mapToDouble(d -> d).toArray();
					HistogramChart f = new HistogramChart(null, nBins, null, 2, data);
					MetaOmGraph.getDesktop().add(f);
					f.setDefaultCloseOperation(2);
					f.setClosable(true);
					f.setResizable(true);
					f.pack();
					f.setSize(1000, 700);
					f.setVisible(true);
					f.toFront();

				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error occured while reading data!!!", "Error",
							JOptionPane.ERROR_MESSAGE);

					e.printStackTrace();
					return;
				}
			}
		});

	}

	/**
	 * Plot frequency barchart with selected columns
	 * 
	 * @param colValue
	 */
	public void plotBarChart(String colValue) {

		if (colValue == null) {
			return;
		}

		// gert data for the selected columns
		List<String> chartData = getFeatureMetaData(colValue);
		// add barchart
		// ArrayList<String> list = new ArrayList<String>();
		// list.add("Geeks");
		// list.add("for");
		// list.add("Geeks");
		BarChart f2 = new BarChart(myProject, colValue, chartData, 1);
		MetaOmGraph.getDesktop().add(f2);
		f2.setDefaultCloseOperation(2);
		f2.setClosable(true);
		f2.setResizable(true);
		f2.pack();
		f2.setSize(1000, 700);
		f2.setVisible(true);
		f2.toFront();
	}

	/**
	 * get values in a given correlation column
	 * 
	 * @param colName
	 * @return
	 */
	private List<String> getFeatureMetaData(String colName) {

		List<String> metadataVals = new ArrayList<>();
		// add all values under the colName column
		for (int r = 0; r < listDisplay.getRowCount(); r++) {
			String thisVal = (String) listDisplay.getModel().getValueAt(r,
					listDisplay.getColumn(colName).getModelIndex());
			if (thisVal != null) {
				metadataVals.add(thisVal);
			}
		}

		return metadataVals;
	}

	/**
	 * get values in a given correlation column
	 * 
	 * @param colName
	 * @return
	 */
	private List<Double> getCorrData(String colName) {
		if (colName == null) {
			return null;
		}
		List<Double> corrVals = new ArrayList<>();
		// add all values under the colName column
		for (int r = 0; r < listDisplay.getRowCount(); r++) {
			CorrelationValue thisVal = (CorrelationValue) listDisplay.getModel().getValueAt(r,
					listDisplay.getColumn(colName).getModelIndex());
			if (thisVal != null) {
				corrVals.add(thisVal.doubleValue());
			}
		}

		return corrVals;
	}

	/**
	 * Build menu for executing R scripts
	 */
	private void refreshRPlotMenu() {
		// remove all existing items
		if (plotRMenu.getItemCount() > 0) {
			plotRMenu.removeAll();
		}

		String pathtoRscripts = MetaOmGraph.getpathtoRscrips();
		if (!(pathtoRscripts == null || pathtoRscripts == "")) {
			// get a list of .R files in the directory
			File[] rFiles = Utils.fileFinder(pathtoRscripts, ".R");
			// if files found
			if (rFiles != null) {
				// add each file to menu
				for (File f : rFiles) {
					JMenuItem thisItem = new JMenuItem(f.getName());
					thisItem.setActionCommand("runuserR::" + f.getAbsolutePath());
					thisItem.addActionListener(this);
					plotRMenu.add(thisItem);
				}
			}
		}

		runOtherScript = new JMenuItem("Run other");
		runOtherScript.setActionCommand("runuserR");
		runOtherScript.addActionListener(this);
		plotRMenu.add(runOtherScript);

	}
	


}
