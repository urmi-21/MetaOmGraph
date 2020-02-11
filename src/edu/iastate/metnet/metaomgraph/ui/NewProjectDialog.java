package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultFormatterFactory;

import org.dizitart.no2.Document;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.GraphFileFilter;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataCollection;
import edu.iastate.metnet.metaomgraph.RandomAccessFile;
import edu.iastate.metnet.metaomgraph.utils.MetNetUtils;
import edu.iastate.metnet.metaomgraph.utils.Utils;

public class NewProjectDialog extends JDialog implements ActionListener, ItemListener {

	private JSpinner infoColumnSpinner;

	private ButtonGroup delimiterGroup, blanksGroup;

	private JTextField sourceField, rowsField, colsField, infoField;
	// urmi
	private JTextField infoField2;
	public int csvFlag = -1;
	private char delimiter;
	// urmi
	private char metadatadelimiter;

	private Object[][] rowArray;

	private Object[] colArray;

	private boolean cancelled;

	private JCheckBox allowBlanksBox;

	private JLabel statusLabel;

	private JRadioButton tabButton, spaceButton, commaButton, semicolonButton;

	private JRadioButton ignoreBlanksButton, replaceBlanksButton;

	private JFormattedTextField blankValueField;

	// urmi for choosing delimiter
	private JComboBox delimiterDatafile;
	private JComboBox delimiterMetadatafile;

	public NewProjectDialog() {
		this((Frame) null);
	}

	public NewProjectDialog(Frame parent) {
		super(parent, "Create New Project", true);
		initComponents();
	}

	public NewProjectDialog(Dialog parent) {
		super(parent, "Create New Project", true);
		initComponents();
	}

	private void initComponents() {
		cancelled = true;
		statusLabel = new JLabel("<html><font size=-2>Begin by selecting a data file.</font></html>");

		// Prompt for the number of info columns
		JPanel infoColumnPanel = new JPanel();
		JLabel infoColumnLabel = new JLabel("How many columns in the data file contain the feature metadata?");
		infoColumnLabel.setToolTipText(
				"The data file can have additional columns containg feature metadata. \nChoose how many columns in the data file have the additional feature metadata.");
		infoColumnSpinner = new JSpinner(
				new SpinnerNumberModel(new Integer(-1), new Integer(-1), null, new Integer(1)));
		final JComponent origEditor = infoColumnSpinner.getEditor();
		JFormattedTextField.AbstractFormatter af = new JFormattedTextField.AbstractFormatter() {

			@Override
			public Object stringToValue(String text) throws ParseException {
				try {
					return new Integer(text);
				} catch (NumberFormatException nfe) {
					return new Integer(-1);
				}
			}

			@Override
			public String valueToString(Object value) throws ParseException {
				if (value instanceof Integer) {
					Integer intValue = (Integer) value;
					if (intValue.intValue() < 0)
						return "???";
					else
						return ((Integer) value).intValue() + "";
				}
				return null;
			}

		};
		((JSpinner.NumberEditor) origEditor).getTextField().setFormatterFactory(new DefaultFormatterFactory(af));
		infoColumnPanel.add(infoColumnLabel);
		infoColumnPanel.add(infoColumnSpinner);

		// Prompt for the delimiter character
		delimiterGroup = new ButtonGroup();
		tabButton = new JRadioButton("Tab");
		tabButton.setName("\t");
		delimiterGroup.add(tabButton);
		spaceButton = new JRadioButton("Space");
		spaceButton.setName(" ");
		delimiterGroup.add(spaceButton);
		commaButton = new JRadioButton("Comma (,)");
		commaButton.setName(",");
		delimiterGroup.add(commaButton);
		semicolonButton = new JRadioButton("Semicolon (;)");
		semicolonButton.setName(";");
		delimiterGroup.add(semicolonButton);

		// urmi make delimiter dropdown
		delimiterDatafile = new JComboBox();
		delimiterDatafile.addItem("Tab (\\t)");
		delimiterDatafile.addItem("Comma (,)");
		delimiterDatafile.addItem("Semicolon (;)");
		delimiterDatafile.addItem("Space");
		delimiterMetadatafile = new JComboBox();
		delimiterMetadatafile.addItem("Tab (\\t)");
		delimiterMetadatafile.addItem("Comma (,)");
		delimiterMetadatafile.addItem("Semicolon (;)");
		delimiterMetadatafile.addItem("Space");
		
		delimiterDatafile.addActionListener(new ActionListener(){
		    @Override
			public void actionPerformed(ActionEvent e)
		    {
		        int selectedIndex =  delimiterMetadatafile.getSelectedIndex();
		       if(selectedIndex==0) {
		    	   metadatadelimiter='\t';
		       }else if(selectedIndex==1) {
		    	   metadatadelimiter=',';
		       }else if(selectedIndex==2) {
		    	   metadatadelimiter=';';
		       }else if(selectedIndex==3) {
		    	   metadatadelimiter=' ';
		       }
		    }
		});
		delimiterMetadatafile.addActionListener(new ActionListener(){
		    @Override
			public void actionPerformed(ActionEvent e)
		    {
		        int selectedIndex =  delimiterMetadatafile.getSelectedIndex();
		       if(selectedIndex==0) {
		    	   metadatadelimiter='\t';
		       }else if(selectedIndex==1) {
		    	   metadatadelimiter=',';
		       }else if(selectedIndex==2) {
		    	   metadatadelimiter=';';
		       }else if(selectedIndex==3) {
		    	   metadatadelimiter=' ';
		       }
		    }
		});

		allowBlanksBox = new JCheckBox("Source file contains \"blank\" values", false);
		ignoreBlanksButton = new JRadioButton("Skip blank values");
		replaceBlanksButton = new JRadioButton("Treat blank values as: ");
		ignoreBlanksButton.setEnabled(false);
		replaceBlanksButton.setEnabled(false);
		replaceBlanksButton.addItemListener(this);
		blankValueField = new JFormattedTextField(new Double(0));
		blankValueField.setEnabled(false);
		blankValueField.setPreferredSize(new Dimension(35, 20));
		blanksGroup = new ButtonGroup();
		blanksGroup.add(ignoreBlanksButton);
		blanksGroup.add(replaceBlanksButton);
		ignoreBlanksButton.setSelected(false);
		replaceBlanksButton.setSelected(true);
		allowBlanksBox.addItemListener(this);
		JPanel delimiterPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = .5;
		c.weighty = .5;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		// delimiterPanel.add(tabButton, c);
		// c.gridx = 1;
		// delimiterPanel.add(spaceButton, c);
		// c.gridx = 0;
		// c.gridy = 1;
		// delimiterPanel.add(commaButton, c);
		// c.gridx = 1;
		// delimiterPanel.add(semicolonButton, c);
		// urmi add delimiter combo box
		c.gridx = 0;
		JLabel delimDatalabel = new JLabel("For data file:");
		delimiterPanel.add(delimDatalabel, c);
		c.gridx = 1;
		delimiterPanel.add(delimiterDatafile, c);
		c.gridx = 2;
		JLabel delimMetadatalabel = new JLabel("For Metadata file:");
		delimiterPanel.add(delimMetadatalabel, c);
		c.gridx = 3;
		delimiterPanel.add(delimiterMetadatafile, c);

		c.gridy = 2;
		c.gridx = 0;
		c.gridwidth = 2;
		delimiterPanel.add(allowBlanksBox, c);
		c.gridy = 3;
		c.insets = new Insets(0, 10, 0, 0);
		delimiterPanel.add(ignoreBlanksButton, c);
		c.gridy = 4;
		c.insets = new Insets(0, 5, 0, 0);
		// c.gridwidth=1;
		// delimiterPanel.add(replaceBlanksButton,c);
		// c.gridx=1;
		// delimiterPanel.add(blankValueField,c);

		JPanel blankValuePanel = new JPanel();
		blankValuePanel.add(replaceBlanksButton);
		blankValuePanel.add(blankValueField);
		delimiterPanel.add(blankValuePanel, c);

		// JPanel buttonPanel = new JPanel(new GridLayout(0, 2));
		// buttonPanel.add(tabButton);
		// buttonPanel.add(spaceButton);
		// buttonPanel.add(commaButton);
		// buttonPanel.add(semicolonButton);
		// JPanel delimiterPanel = new JPanel(new BorderLayout());
		// delimiterPanel.add(new JLabel(
		// "What character does this file use as a delimiter?"),
		// BorderLayout.PAGE_START);
		// delimiterPanel.add(buttonPanel, BorderLayout.CENTER);
		// allowBlanksBox = new JCheckBox(
		// "Source file contains \"blank\" values", false);
		// delimiterPanel.add(allowBlanksBox, BorderLayout.PAGE_END);

		JPanel filePanel = new JPanel(new GridBagLayout());

		///////////////////////////////
		// GridBagConstraints c = new GridBagConstraints();
		JLabel sourceLabel, rowsLabel, colsLabel, infoLabel;
		JLabel infoLabel2;
		JButton sourceButton, rowsButton, colsButton, infoButton;
		JButton infoButton2;
		sourceLabel = new JLabel("<html><pre>Data File (csv/tsv):</pre></html>");
		sourceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		sourceLabel.setToolTipText("Data file should be a tab or commma-delimited file. Can be .csv or .txt format");
		sourceField = new JTextField();
		sourceField.setEditable(false);
		sourceButton = new JButton("Browse...");
		GraphFileFilter filter = new GraphFileFilter();
		filter.setMode(GraphFileFilter.TEXT);
		FileBrowseListener sourceListener = new FileBrowseListener(sourceField, filter) {
			@Override
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
				if (source == null)
					return;
				new SourceAnalyzeWorker(source, true).start();
			}
		};
		sourceButton.addActionListener(sourceListener);
		rowsLabel = new JLabel("<html><pre>Row Names<font size=-2>&nbsp;(optional)</font>:</pre></html>");
		rowsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		rowsField = new JTextField();
		rowsField.setEditable(false);
		rowsButton = new JButton("Browse...");
		rowsButton.addActionListener(new FileBrowseListener(rowsField, null));
		colsLabel = new JLabel("<html><pre>Column Names<font size=-2>&nbsp;(optional)</font>:</pre></html>");
		colsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		colsField = new JTextField();
		colsField.setEditable(false);
		colsButton = new JButton("Browse...");
		colsButton.addActionListener(new FileBrowseListener(colsField, null));

		infoLabel = new JLabel("<html><pre>Metadata (xml)<font size=-2>&nbsp;(optional)</font>:</pre></html>");
		infoLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		infoLabel.setToolTipText("Reads .XML file containing the metadata");
		infoField = new JTextField();
		infoField.setEditable(false);
		infoButton = new JButton("Browse...");
		infoButton.addActionListener(new FileBrowseListener(infoField, new GraphFileFilter(GraphFileFilter.XML)));
		infoButton.setActionCommand("xml");

		// by urmi to read csv files
		infoLabel2 = new JLabel("<html><pre>Metadata (csv/tsv)<font size=-2>&nbsp;(optional)</font>:</pre></html>");
		infoLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
		infoLabel2.setToolTipText(
				"Reads .csv or .txt file containing the metadata. This file should be tab or commma-delimited.");
		infoField2 = new JTextField();
		infoField2.setEditable(false);
		infoButton2 = new JButton("Browse...");

		GraphFileFilter filter2 = new GraphFileFilter();
		filter2.setMode(GraphFileFilter.TEXT);
		FileBrowseListener sourceListener2 = new FileBrowseListener(infoField2, filter2) {
			@Override
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
				if (source == null)
					return;
				new SourceAnalyzeWorker(source, false).start();
			}
		};
		infoButton2.addActionListener(sourceListener2);
		// infoButton2.addActionListener(new FileBrowseListener(infoField2, new
		// GraphFileFilter(GraphFileFilter.TEXT)));
		infoButton2.setActionCommand("csv");
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = .5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.insets = new Insets(0, 0, 0, 0);
		filePanel.add(sourceLabel, c);
		c.gridy = 1;
		// filePanel.add(rowsLabel, c);
		c.gridy = 2;
		// filePanel.add(colsLabel, c);
		c.gridy = 3;
		//filePanel.add(infoLabel, c);
		c.gridy = 4;
		filePanel.add(infoLabel2, c);
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		filePanel.add(sourceField, c);
		c.gridy = 1;
		// filePanel.add(rowsField, c);
		c.gridy = 2;
		// filePanel.add(colsField, c);
		c.gridy = 3;
		//filePanel.add(infoField, c);
		c.gridy = 4;
		filePanel.add(infoField2, c);
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0;
		filePanel.add(sourceButton, c);
		c.gridy = 1;
		// filePanel.add(rowsButton, c);
		c.gridy = 2;
		// filePanel.add(colsButton, c);
		c.gridy = 3;
		//filePanel.add(infoButton, c);
		c.gridy = 4;
		filePanel.add(infoButton2, c);

		JPanel mainPanel, mainButtonPanel;
		mainButtonPanel = new JPanel();
		final JButton okButton, cancelButton, helpButton;
		this.addWindowListener(MetaOmGraph.getModalMaker());
		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		okButton = new JButton("OK");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		// okButton.setBorder(BorderFactory.createCompoundBorder(new
		// MarchingFocusBorder(okButton),okButton.getBorder()));
		helpButton = new JButton("Help");
		helpButton.addActionListener(MetaOmGraph.getHelpListener());
		helpButton.setActionCommand("newproject-delimited.php");
		Border etched = BorderFactory.createEtchedBorder();
		mainButtonPanel.add(okButton);
		mainButtonPanel.add(cancelButton);
		mainButtonPanel.add(helpButton);
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		filePanel.setBorder(BorderFactory.createTitledBorder(etched, "Input Files"));
		infoColumnPanel.setBorder(BorderFactory.createTitledBorder(etched, "Feature Metadata Columns"));
		delimiterPanel.setBorder(BorderFactory.createTitledBorder(etched, "Delimiter"));
		mainPanel.add(filePanel);
		mainPanel.add(infoColumnPanel);
		mainPanel.add(delimiterPanel);
		JPanel statusPanel = new JPanel();
		statusPanel.add(statusLabel);
		mainPanel.add(statusPanel);
		mainPanel.add(mainButtonPanel);
		this.getContentPane().add(mainPanel);
		int width = MetaOmGraph.getMainWindow().getWidth();
		int height = MetaOmGraph.getMainWindow().getHeight();
		this.pack();
		this.setLocation((width - getWidth()) / 2 + MetaOmGraph.getMainWindow().getX(),
				(height - getHeight()) / 2 + MetaOmGraph.getMainWindow().getY());
		AbstractAction act = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MetaOmGraph.getHelpListener().actionPerformed(
						new ActionEvent(helpButton, ActionEvent.ACTION_PERFORMED, "newproject-delimited.php"));
			}
		};
		this.getRootPane().getActionMap().put("help", act);
		InputMap im = this.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "help");

		///////////////////////////////////////todo//////////////////////////////////////////////
		// change colors urmi
		//filePanel.setBackground(Color.DARK_GRAY);
	}

	public class FileBrowseListener implements ActionListener {
		public JTextField pathField;

		public FileFilter filter;

		public File source;

		public FileBrowseListener(JTextField pathField, FileFilter filter) {
			this.pathField = pathField;
			this.filter = filter;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			source = Utils.chooseFileToOpen(filter, MetaOmGraph.getMainWindow());
			if (source != null) {
				pathField.setText(source.getAbsolutePath());
			} else {
				pathField.setText("");
			}

			if (arg0.getActionCommand() == "xml") {
				infoField2.setText("");
			}
			if (arg0.getActionCommand() == "csv") {
				infoField.setText("");
			}

		}

	}

	// action for OK button
	public void doOK() {
		if (sourceField.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "You must choose a source data file!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if ((infoColumnSpinner.getValue() == null) || (((Integer) infoColumnSpinner.getValue()).intValue() < 0)) {
			JOptionPane.showMessageDialog(this, "You must enter the number of information columns!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		Enumeration enumer = delimiterGroup.getElements();
		delimiter = 0;
		while (enumer.hasMoreElements()) {
			JRadioButton thisButton = (JRadioButton) enumer.nextElement();
			if (thisButton.isSelected())
				delimiter = thisButton.getName().charAt(0);
		}
		if (delimiter == 0) {
			// urmi get selected delim
			int sind = delimiterDatafile.getSelectedIndex();
			if (sind == 0) {
				delimiter = '\t';
			} else if (sind == 1) {
				delimiter = ',';
			} else if (sind == 2) {
				delimiter = ';';
			} else if (sind == 3) {
				delimiter = ' ';
			}
			// delimiter=delimiterDatafile.getSelectedItem().toString().charAt(index);
			// JOptionPane.showMessageDialog(this, "You must select a delimiter!", "Error",
			// JOptionPane.ERROR_MESSAGE);
			// return;
		}
		File source = new File(sourceField.getText());
		if (!source.exists()) {
			JOptionPane.showMessageDialog(this, "Invalid source file!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		dispose();
		final Vector<String> rowNames;
		boolean makeLocus = false;
		Object[][] locusIDs;
		if (!rowsField.getText().equals("")) {
			rowNames = new Vector<String>();
			try {
				RandomAccessFile dataIn = new RandomAccessFile(rowsField.getText(), "r");
				String thisName = Utils.clean(dataIn.readLine());
				while (thisName != null) {
					rowNames.add(thisName);
					if ((thisName != null) && (thisName.length() > 6)
							&& (thisName.substring(thisName.length() - 3).equals("_at")))
						makeLocus = true;
					thisName = Utils.clean(dataIn.readLine());
				}
				dataIn.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			if (makeLocus) {
				int result = JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(),
						"It looks like you've imported some " + "gene IDs.\nWould you "
								+ "like to automatically add additional " + "gene information as well?",
						"Gene IDs detected", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				makeLocus = (result == JOptionPane.YES_OPTION);
			}
			if (makeLocus) {
				// make an array of the IDs, skipping the first row since it
				// will be an identifier
				String[] idCol = new String[rowNames.size() - 1];
				for (int i = 0; i < idCol.length; i++) {
					idCol[i] = rowNames.get(i + 1).toString();
				}
				String[][] metnetInfo = MetNetUtils.getMetNetInfo(idCol);
				locusIDs = new Object[metnetInfo.length + 1][3];
				locusIDs[0][0] = "Locus ID";
				locusIDs[0][1] = "Gene Name";
				locusIDs[0][2] = "Pathways";
				for (int i = 0; i < metnetInfo.length; i++) {
					locusIDs[i + 1] = metnetInfo[i];
				}
				// System.out.println("metnetInfo[0][0]:"+metnetInfo[0][0]);
				// System.out.println("metnetInfo[0][1]:"+metnetInfo[0][1]);
				// System.out.println("metnetInfo[1][0]:"+metnetInfo[1][0]);
				// System.out.println("locusIDs[0][0]:"+locusIDs[0][0]);
				// System.out.println("locusIDs[1][0]:"+locusIDs[1][0]);
				// for (int x = 0; x < locusIDs.length; x++)
				// locusIDs[x] = Affy2Locus.converter().convert(
				// rowNames.get(x).toString());
			} else {
				locusIDs = null;
			}
		} else {
			rowNames = null;
			locusIDs = null;
		}
		Vector<String> colNames = null;
		if (!colsField.getText().equals("")) {
			try {
				BufferedReader dataIn = new BufferedReader(new FileReader(colsField.getText()));
				colNames = new Vector<String>();
				String temp;
				while (dataIn.ready()) {
					temp = Utils.clean(dataIn.readLine());
					if ((temp != null) && (!temp.equals("")))
						colNames.add(temp);
				}
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
				colNames = null;
			} catch (IOException ioe) {
				colNames = null;
				ioe.printStackTrace();
			}
		}
		rowArray = null;
		colArray = null;
		if (rowNames != null) {
			if (makeLocus)
				rowArray = new Object[rowNames.size()][4];
			else
				rowArray = new Object[rowNames.size()][1];
			for (int x = 0; x < rowArray.length; x++) {
				if (makeLocus) {
					rowArray[x][0] = locusIDs[x][0];
					rowArray[x][1] = locusIDs[x][1];
					rowArray[x][2] = locusIDs[x][2];
					rowArray[x][3] = rowNames.get(x);
				} else {
					rowArray[x][0] = rowNames.get(x);
				}
			}
		}
		if (colNames != null) {
			colArray = colNames.toArray();
		}

		// indicates reading csv file for metadata
		if (!infoField2.getText().equals("")) {
			csvFlag = 1;
		}
	}

	// action for OK button
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ok")) {
			cancelled = false;
			doOK();
			return;
		}
		if (e.getActionCommand().equals("cancel")) {
			cancelled = true;
			dispose();
			return;
		}
	}

	public Object[] getColArray() {
		return colArray;
	}

	public char getDelimiter() {
		return delimiter;
	}

	// urmi function to return delimiter value of metadata file
	public char getMetadataDelimiter() {
		return metadatadelimiter;
	}

	public Object[][] getRowArray() {
		return rowArray;
	}

	// return File to metadata xml file
	public File getExtendedInfoFile() {
		if (csvFlag == -1) {
			return new File(infoField.getText());
		} else {
			// JOptionPane.showMessageDialog(null, "readingcsv");
			// return (getMetadatafromcsv(infoField2.getText()));
			return new File(infoField2.getText());
		}
	}

	private InputStream getMetadatafromcsv2(String filepath) {

		String finalfile = "";

		// call metadataCollection class and read csv file
		MetadataCollection obj = new MetadataCollection();
		try {
			obj.readMetadataTextFile(filepath, "\\t", true);
			// now file is read in obj.mogCollection
			String[] headers = obj.getHeaders();
			List<Document> metadata = obj.getAllData();

			// currently bypass the GUI.......
			JOptionPane.showMessageDialog(null, "Started to build data model", getTitle(), JOptionPane.WARNING_MESSAGE);

			// find the tags to make XML
			String xml_header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			finalfile += xml_header + "\n";
			finalfile += "<Experiments>\n";
			System.out.println(xml_header);

			// assume col0 is exp and 10 for chip
			int expfield_col = 0;
			int chipfield_col = 10;

			// step2
			String expfield = "study_accession";
			String chipfield = "sample_title";
			String curr_exp = "";
			String curr_chip = "";
			List<String> uniq_exps = new ArrayList<String>();
			for (int i = 0; i < metadata.size(); i++) {
				curr_exp = metadata.get(i).get(expfield).toString();
				if (!uniq_exps.contains(curr_exp)) {
					uniq_exps.add(curr_exp);
				}

			}

			int p = 0;
			for (int i = 0; i < uniq_exps.size(); i++) {

				curr_exp = uniq_exps.get(i);
				// System.out.println("<Experiment name=\"" + uniq_exps.get(i) + "\">");
				// System.out.println("<Title>" + metadata.get(i).get("Expname").toString() +
				// "</Title>");
				finalfile += "<Experiment name=\"" + uniq_exps.get(i) + "\">\n";
				// finalfile+="<Title>" + "Title:" + i + "</Title>\n";

				for (int l = 0; l < metadata.size(); l++) {
					String curr_exp2 = metadata.get(l).get(expfield).toString();
					if (curr_exp2 == curr_exp) {
						p = l;
					}

				}

				// JOptionPane.showMessageDialog(, finalfile);

				finalfile += "<Title>" + metadata.get(p).get("study_title").toString() + "</Title>\n";
				finalfile += "<Design_description>" + metadata.get(p).get("design_description").toString()
						+ "</Design_description>\n";
				finalfile += "<Study_summary>" + metadata.get(p).get("study_summary").toString() + "</Study_summary>\n";
				finalfile += "<library_construction_protocol>"
						+ metadata.get(p).get("library_construction_protocol").toString()
						+ "</library_construction_protocol>\n";

				for (int j = 0; j < metadata.size(); j++) {

					if (curr_exp.equals(metadata.get(j).get(expfield).toString())) {
						curr_chip = metadata.get(j).get(chipfield).toString();
						// System.out.println("<chip name=\"" + curr_chip + "\">");
						// System.out.println("<A>" + metadata.get(j).get("study_type").toString() +
						// "</A>");
						// System.out.println("<B>" +
						// metadata.get(j).get("design_description").toString() + "</B>");
						// System.out.println("</chip>");

						finalfile += "<chip name=\"" + curr_chip + "\">\n";
						// finalfile+="<A>" + metadata.get(j).get("study_type").toString() + "</A>\n";
						finalfile += "<experiment_attribute>" + metadata.get(j).get("experiment_attribute").toString()
								+ "</experiment_attribute>\n";
						finalfile += "<replication>" + metadata.get(j).get("replication").toString()
								+ "</replication>\n";
						finalfile += "<strain>" + metadata.get(j).get("strain").toString() + "</strain>\n";
						finalfile += "<genotype>" + metadata.get(j).get("genotype").toString() + "</genotype>\n";
						finalfile += "<ploid>" + metadata.get(j).get("ploid").toString() + "</ploid>\n";
						finalfile += "<growth_medium>" + metadata.get(j).get("growth_medium").toString()
								+ "</growth_medium>\n";
						finalfile += "<treatment>" + metadata.get(j).get("treatment").toString() + "</treatment>\n";
						finalfile += "<time_after_treatment>" + metadata.get(j).get("time_after_treatment").toString()
								+ "</time_after_treatment>\n";
						finalfile += "<aging_or_stage>" + metadata.get(j).get("aging_or_stage").toString()
								+ "</aging_or_stage>\n";
						finalfile += "<library_name>" + metadata.get(j).get("library_name").toString()
								+ "</library_name>\n";

						finalfile += "</chip>\n";

					}

				}

				System.out.println("</Experiment>");
				finalfile += "</Experiment>\n";

			}
			System.out.println("</Experiments>");
			finalfile += "</Experiments>";
			// JOptionPane.showMessageDialog(null, finalfile);
			// temporary solution write to file make it read directly from file stream
			// PrintWriter out = new
			// PrintWriter("/home/usingh/Desktop/mog_testdata/tempxml.txt");
			// out.println(finalfile);
			BufferedWriter out2 = null;
			try {
				out2 = new BufferedWriter(new FileWriter("/home/usingh/Desktop/mog_testdata/tempxml.xml"));
				out2.write(finalfile); // Replace with the string
										// you are trying to write
			} catch (IOException e) {
				System.out.println("Exception ");

			} finally {
				out2.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		InputStream stream = null;
		try {
			stream = new ByteArrayInputStream(finalfile.getBytes(StandardCharsets.UTF_8.name()));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stream;

	}

	private File getMetadatafromcsv(String filepath) {
		File result = null;

		// call metadataCollection class and read csv file
		MetadataCollection obj = new MetadataCollection();
		try {
			obj.readMetadataTextFile(filepath, "\\t", true);
			// now file is read in obj.mogCollection
			String[] headers = obj.getHeaders();
			List<Document> metadata = obj.getAllData();

			// currently bypass the GUI.......
			JOptionPane.showMessageDialog(null, "Started to build data model", getTitle(), JOptionPane.WARNING_MESSAGE);
			String finalfile = "";
			// find the tags to make XML
			String xml_header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			finalfile += xml_header + "\n";
			finalfile += "<Experiments>\n";
			System.out.println(xml_header);

			// assume col0 is exp and 10 for chip
			int expfield_col = 0;
			int chipfield_col = 10;

			// step2
			String expfield = "study_accession";
			String chipfield = "sample_title";
			String curr_exp = "";
			String curr_chip = "";
			List<String> uniq_exps = new ArrayList<String>();
			for (int i = 0; i < metadata.size(); i++) {
				curr_exp = metadata.get(i).get(expfield).toString();
				if (!uniq_exps.contains(curr_exp)) {
					uniq_exps.add(curr_exp);
				}

			}

			int p = 0;
			for (int i = 0; i < uniq_exps.size(); i++) {

				curr_exp = uniq_exps.get(i);
				// System.out.println("<Experiment name=\"" + uniq_exps.get(i) + "\">");
				// System.out.println("<Title>" + metadata.get(i).get("Expname").toString() +
				// "</Title>");
				finalfile += "<Experiment name=\"" + uniq_exps.get(i) + "\">\n";
				// finalfile+="<Title>" + "Title:" + i + "</Title>\n";

				for (int l = 0; l < metadata.size(); l++) {
					String curr_exp2 = metadata.get(l).get(expfield).toString();
					if (curr_exp2 == curr_exp) {
						p = l;
					}

				}

				// JOptionPane.showMessageDialog(, finalfile);

				finalfile += "<Title>" + metadata.get(p).get("study_title").toString() + "</Title>\n";
				finalfile += "<Design_description>" + metadata.get(p).get("design_description").toString()
						+ "</Design_description>\n";
				finalfile += "<Study_summary>" + metadata.get(p).get("study_summary").toString() + "</Study_summary>\n";
				finalfile += "<library_construction_protocol>"
						+ metadata.get(p).get("library_construction_protocol").toString()
						+ "</library_construction_protocol>\n";

				for (int j = 0; j < metadata.size(); j++) {

					if (curr_exp.equals(metadata.get(j).get(expfield).toString())) {
						curr_chip = metadata.get(j).get(chipfield).toString();
						// System.out.println("<chip name=\"" + curr_chip + "\">");
						// System.out.println("<A>" + metadata.get(j).get("study_type").toString() +
						// "</A>");
						// System.out.println("<B>" +
						// metadata.get(j).get("design_description").toString() + "</B>");
						// System.out.println("</chip>");

						finalfile += "<chip name=\"" + curr_chip + "\">\n";
						// finalfile+="<A>" + metadata.get(j).get("study_type").toString() + "</A>\n";
						finalfile += "<experiment_attribute>" + metadata.get(j).get("experiment_attribute").toString()
								+ "</experiment_attribute>\n";
						finalfile += "<replication>" + metadata.get(j).get("replication").toString()
								+ "</replication>\n";
						finalfile += "<strain>" + metadata.get(j).get("strain").toString() + "</strain>\n";
						finalfile += "<genotype>" + metadata.get(j).get("genotype").toString() + "</genotype>\n";
						finalfile += "<ploid>" + metadata.get(j).get("ploid").toString() + "</ploid>\n";
						finalfile += "<growth_medium>" + metadata.get(j).get("growth_medium").toString()
								+ "</growth_medium>\n";
						finalfile += "<treatment>" + metadata.get(j).get("treatment").toString() + "</treatment>\n";
						finalfile += "<time_after_treatment>" + metadata.get(j).get("time_after_treatment").toString()
								+ "</time_after_treatment>\n";
						finalfile += "<aging_or_stage>" + metadata.get(j).get("aging_or_stage").toString()
								+ "</aging_or_stage>\n";
						finalfile += "<library_name>" + metadata.get(j).get("library_name").toString()
								+ "</library_name>\n";

						finalfile += "</chip>\n";

					}

				}

				System.out.println("</Experiment>");
				finalfile += "</Experiment>\n";

			}
			System.out.println("</Experiments>");
			finalfile += "</Experiments>";
			// JOptionPane.showMessageDialog(null, finalfile);
			// temporary solution write to file make it read directly from file stream
			// PrintWriter out = new
			// PrintWriter("/home/usingh/Desktop/mog_testdata/tempxml.txt");
			// out.println(finalfile);
			BufferedWriter out2 = null;
			try {
				out2 = new BufferedWriter(new FileWriter("/home/usingh/Desktop/mog_testdata/tempxml.xml"));
				out2.write(finalfile); // Replace with the string
										// you are trying to write
			} catch (IOException e) {
				System.out.println("Exception ");

			} finally {
				out2.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		result = new File("/home/usingh/Desktop/mog_testdata/tempxml.xml");
		return result;

	}

	public int getInfoColumns() {
		return ((Number) infoColumnSpinner.getValue()).intValue();
	}

	public File getSourceFile() {
		return new File(sourceField.getText());
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean getIgnoreConsecutiveDelimiters() {
		return !allowBlanksBox.isSelected();
	}

	public Double getBlankValue() {
		if (ignoreBlanksButton.isSelected()) {
			return null;
		} else {
			return (Double) blankValueField.getValue();
		}
	}

	private class SourceAnalyzeWorker extends AnimatedSwingWorker {

		File source;
		boolean datafile; // if true analyze data file else just find delimiter for metadata file

		public SourceAnalyzeWorker(File source, boolean flag) {
			super("Analyzing", true);
			this.source = source;
			this.datafile = flag;
		}

		@Override
		public Object construct() {
			// Determine the delimiter and number of info columns, if
			// possible.
			char delimiter = 0; // changed from zero to \t
			// char delimiter = '\t'; //changed from zero to \t
			int infoColumns = 0;
			try {
				RandomAccessFile dataIn = new RandomAccessFile(source, "r");
				if (!dataIn.nextLine()) {
					Enumeration enumer = delimiterGroup.getElements();
					infoColumnSpinner.setValue(new Integer(-1));
					while (enumer.hasMoreElements()) {
						((JRadioButton) enumer.nextElement()).setSelected(false);
					}
					System.err.println("!dataIn.nextLine()");
					statusLabel.setText(
							"<html><font size=-2 color=\"#FF0000\">Unable to auto-detect delimiter/feature metadata columns.  Please enter values manually.</font></html>");
					return null;
				}
				String firstLine = dataIn.readLine();
				String secondLine = dataIn.readLine();
				System.out.println("First line:");
				System.out.println(firstLine);
				System.out.println("Second line:");
				System.out.println(secondLine);
				if ((firstLine == null) || (secondLine == null)) {
					Enumeration enumer = delimiterGroup.getElements();
					infoColumnSpinner.setValue(new Integer(-1));
					while (enumer.hasMoreElements()) {
						((JRadioButton) enumer.nextElement()).setSelected(false);
					}
					if (firstLine == null)
						System.err.println("firstline==null");
					else
						System.err.println("secondLine==null");
					statusLabel.setText(
							"<html><font size=-2 color=\"#FF0000\">Unable to auto-detect delimiter/feature metadata columns.  Please enter values manually.</font></html>");
					return null;
				} else if ((firstLine.equals("")) || (secondLine.equals(""))) {
					Enumeration enumer = delimiterGroup.getElements();
					infoColumnSpinner.setValue(new Integer(-1));
					while (enumer.hasMoreElements()) {
						((JRadioButton) enumer.nextElement()).setSelected(false);
					}
					if (firstLine.equals(""))
						System.err.println("firstLine.equals(\"\")");
					else
						System.err.println("secondLine.equals(\"\"))");
					statusLabel.setText(
							"<html><font size=-2 color=\"#FF0000\">Unable to auto-detect delimiter/feature metadata columns.  Please enter values manually.</font></html>");
					return null;
				}
				// dataIn.close();
				int firstTabs, secondTabs;
				int firstSpaces, secondSpaces;
				int firstCommas, secondCommas;
				int firstSemis, secondSemis;
				firstTabs = 0;
				secondTabs = 0;
				firstSpaces = 0;
				secondSpaces = 0;
				firstCommas = 0;
				secondCommas = 0;
				firstSemis = 0;
				secondSemis = 0;
				for (int i = 0; i < firstLine.length(); i++) {
					switch (firstLine.charAt(i)) {
					case '\t':
						firstTabs++;
						break;
					case ' ':
						firstSpaces++;
						break;
					case ',':
						firstCommas++;
						break;
					case ';':
						firstSemis++;
						break;
					}
				}
				for (int i = 0; i < secondLine.length(); i++) {
					switch (secondLine.charAt(i)) {
					case '\t':
						secondTabs++;
						break;
					case ' ':
						secondSpaces++;
						break;
					case ',':
						secondCommas++;
						break;
					case ';':
						secondSemis++;
						break;
					}
				}

				// urmi only update metadata delimiter box and save delimiter to
				// metadatadelimiter var. returned by getMetadataDelimiter()
				if (!datafile) {

					if ((firstTabs == secondTabs) && (firstTabs != 0)) {
						delimiter = '\t';
						// tabButton.setSelected(true);
						delimiterMetadatafile.setSelectedIndex(0);
					} else if ((firstSemis == secondSemis) && (firstSemis != 0)) {
						delimiter = ';';
						delimiterMetadatafile.setSelectedIndex(2);
					} else if ((firstCommas == secondCommas) && (firstCommas != 0)) {
						delimiter = ',';
						delimiterMetadatafile.setSelectedIndex(1);
					} else if ((firstSpaces == secondSpaces) && (firstSpaces != 0)) {
						delimiter = ' ';
						delimiterMetadatafile.setSelectedIndex(3);
					}
					metadatadelimiter = delimiter;
				}

				else {
					/*
					 * if ((firstTabs == secondTabs) && (firstTabs != 0)) { delimiter = '\t';
					 * tabButton.setSelected(true); } else if ((firstSemis == secondSemis) &&
					 * (firstSemis != 0)) { delimiter = ';'; semicolonButton.setSelected(true); }
					 * else if ((firstCommas == secondCommas) && (firstCommas != 0)) { delimiter =
					 * ','; commaButton.setSelected(true); } else if ((firstSpaces == secondSpaces)
					 * && (firstSpaces != 0)) { delimiter = ' '; spaceButton.setSelected(true); }
					 */
					// changed to work with JCombobox
					if ((firstTabs == secondTabs) && (firstTabs != 0)) {
						delimiter = '\t';
						// tabButton.setSelected(true);
						delimiterDatafile.setSelectedIndex(0);
					} else if ((firstSemis == secondSemis) && (firstSemis != 0)) {
						delimiter = ';';
						delimiterDatafile.setSelectedIndex(2);
					} else if ((firstCommas == secondCommas) && (firstCommas != 0)) {
						delimiter = ',';
						delimiterDatafile.setSelectedIndex(1);
					} else if ((firstSpaces == secondSpaces) && (firstSpaces != 0)) {
						delimiter = ' ';
						delimiterDatafile.setSelectedIndex(3);
					}

					if (delimiter != 0) {
						String[] firstLineValues = firstLine.split("" + delimiter);
						infoColumns = firstLineValues.length;
						for (int i = firstLineValues.length - 1; i >= 0; i--) {
							try {
								if (!firstLineValues[i].trim().equals("")) {
									Double.parseDouble(Utils.clean(firstLineValues[i]));
								}
								infoColumns--;
							} catch (NumberFormatException nfe) {
								i = -1;
							}
						}
						infoColumnSpinner.setValue(new Integer(infoColumns));
						dataIn.seek(0);
						String thisValue;
						boolean blankFound = false;
						while (((thisValue = dataIn.readString(delimiter, false)) != null) && (!blankFound)) {
							// if (thisValue.equals("")) {
							// blankFound=true;
							// System.out.println("Blank found!");
							// }
							try {
								if (Double.isNaN(Double.valueOf(thisValue))) {
									blankFound = true;
									System.out.println("NaN found");
								}
							} catch (NumberFormatException nfe) {
								blankFound = true;
								System.out.println("Unparseable: " + thisValue);
							}
						}
						allowBlanksBox.setSelected(blankFound);
						statusLabel.setText(
								"<html><font size=-2 color=\"#FF0000\">Delimiter and feature metadata columns automatically detected.</font></html>");
					} else {
						Enumeration enumer = delimiterGroup.getElements();
						infoColumnSpinner.setValue(new Integer(-1));
						while (enumer.hasMoreElements()) {
							((JRadioButton) enumer.nextElement()).setSelected(false);
						}
						System.err.println("couldn't figure delimiter");
						System.err.println("tabs: " + firstTabs + "/" + secondTabs);
						System.err.println("commas: " + firstCommas + "/" + secondCommas);
						System.err.println("semis: " + firstSemis + "/" + secondSemis);
						System.err.println("spaces: " + firstSpaces + "/" + secondSpaces);
						statusLabel.setText(
								"<html><font size=-2 color=\"#FF0000\">Unable to auto-detect delimiter/feature metadata columns.  Please enter values manually.</font></html>");
					}
					dataIn.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			return null;
		}

	}

	// create analyze class for metadata file source. This is quick fix ideally
	// should merge two classes together

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == allowBlanksBox) {
			ignoreBlanksButton.setEnabled(allowBlanksBox.isSelected());
			replaceBlanksButton.setEnabled(allowBlanksBox.isSelected());
			blankValueField.setEnabled(allowBlanksBox.isSelected() && replaceBlanksButton.isSelected());
			return;
		}
		if (e.getSource() == replaceBlanksButton) {
			blankValueField.setEnabled(replaceBlanksButton.isSelected());
			return;
		}
	}
}
