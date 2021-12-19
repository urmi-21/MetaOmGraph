package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.HashLoadable;
import edu.iastate.metnet.metaomgraph.HashtableSavePanel;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.Metadata;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.SearchMatchType;
import edu.iastate.metnet.metaomgraph.utils.qdxml.SimpleXMLElement;
import edu.iastate.metnet.metaomgraph.utils.qdxml.SimpleXMLizable;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class TreeSearchQueryConstructionPanel extends JPanel
		implements ActionListener, HashLoadable<TreeSearchQueryConstructionPanel.QuerySet> {

	private JPanel queryPanel;
	private JPanel queryViewport;
	private String[] fieldBoxTerms;
	private JButton moreButton;
	private JButton fewerButton;
	private JDialog myDialog;
	private JScrollPane mainScrollPane;
	private boolean isOK;
	private JRadioButton allButton;
	private JRadioButton anyButton;
	private JRadioButton invisibleButton;
	private ButtonGroup groupBtnRadio;
	private List<SearchTermPanel> searchTermPanels;
	private JTextField queryDisplay;

	private int queryCount;
	private boolean matchAll;
	// urmi
	//private JRadioButton caseButton;
	//private boolean matchCase;
	private String secondSort;
	private MetaOmProject myProject;
	private static final String ANY_FIELD_STRING = "Any field";

	public TreeSearchQueryConstructionPanel(MetaOmProject project) {
		this(project,false);
	 }
	
	/**
	 * 
	 * @param project activeproject object
	 * @param searchFeatureMetaDataTable: true for search feature meta data table, false for sample metadata 
	 */
	public TreeSearchQueryConstructionPanel(MetaOmProject project, boolean searchFeatureMetaDataTable) {
		myProject = project;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		searchTermPanels = new ArrayList<>();
		queryPanel = new JPanel();
		queryPanel.setLayout(new BoxLayout(queryPanel, 1));
		JPanel queryButtonPanel = new JPanel();
		moreButton = new JButton("More");
		fewerButton = new JButton("Fewer");
		moreButton.setActionCommand("more");
		moreButton.addActionListener(this);
		fewerButton.setActionCommand("fewer");
		fewerButton.addActionListener(this);
		queryButtonPanel.add(moreButton);
		queryButtonPanel.add(fewerButton);
		queryDisplay = new JTextField(" Complete Query for Preview ");
		queryDisplay.setEditable(false);
		queryDisplay.setToolTipText("A preview of the logic that will be applied when using this filter");
		queryDisplay.setMaximumSize(new Dimension(getWidth(), queryDisplay.getHeight()));
		// String[] fields = myProject.getMetadata().getFields();

		String[] fields=null;
		if (searchFeatureMetaDataTable == false) {
			/**
			 * @author urmi changed to : now fields are from Metadatahybrid
			 */
			fields = myProject.getMetadataHybrid().getMetadataHeaders();
			Arrays.sort(fields);
		}else {
			fields=myProject.getInfoColumnNames();
		}
		
		fieldBoxTerms = new String[fields.length + 2];
		fieldBoxTerms[0] = "Any Field";
		fieldBoxTerms[1] = "All Fields";
		for (int x = 2; x < fieldBoxTerms.length; x++)
			fieldBoxTerms[x] = fields[(x - 2)];
		fewerButton.setEnabled(false);
		queryViewport = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5D;
		c.weighty = 0.0D;
		c.fill = 2;
		queryViewport.add(queryPanel, c);
		c.gridy = 1;
		c.weighty = 1.0D;
		queryViewport.add(new JPanel(), c);
		searchTermPanels.add(new SearchTermPanel());
		queryPanel.add(searchTermPanels.get(searchTermPanels.size() - 1));
		mainScrollPane = new JScrollPane(queryViewport);

		allButton = new JRadioButton("Match all of the following");
		anyButton = new JRadioButton("Match any of the following");
		invisibleButton = new JRadioButton("");
		invisibleButton.setVisible(false);
		//urmi make only one selectable add to group
		groupBtnRadio = new ButtonGroup();
		groupBtnRadio.add(allButton);
		groupBtnRadio.add(anyButton);
		groupBtnRadio.add(invisibleButton);
		//caseButton = new JRadioButton("Match case");
		
		allButton.setSelected(true);

		allButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (SearchTermPanel panel : searchTermPanels) {
					panel.setMatchAsAND();
				}
				groupBtnRadio.setSelected(allButton.getModel(), true);
			}
		});
		anyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (SearchTermPanel panel : searchTermPanels) {
					panel.setMatchAsOR();
				}
				groupBtnRadio.setSelected(anyButton.getModel(), true);
			}
		});

		JPanel allAnyPanel = new JPanel();
		//allAnyPanel.add(caseButton);
		allAnyPanel.add(allButton);
		allAnyPanel.add(anyButton);
		add(allAnyPanel, 0);
		JPanel queryDisplayPanel = new JPanel();
		queryDisplayPanel.add(queryDisplay);
		add(queryDisplayPanel, 1);
		JPanel queryPanel = new JPanel();
		queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.X_AXIS));
		HashtableSavePanel savePanel = new HashtableSavePanel(myProject.getSavedQueries(), this);
		savePanel.setSize(queryPanel.getWidth() / 6, queryPanel.getHeight());
		queryPanel.add(savePanel, 0);
		queryPanel.add(mainScrollPane, 1);
		add(queryPanel, 2);
		add(queryButtonPanel, 3);
		setBorder(BorderFactory.createEtchedBorder());
	}

	private void addQueryField() {
		searchTermPanels.get(searchTermPanels.size() - 1).getAndOrBox().setVisible(true);
		searchTermPanels.get(searchTermPanels.size() - 1).validate();
		queryDisplay.setText("Complete Query for Preview");
		queryDisplay.setToolTipText("A preview of the logic that will be applied when using this filter");
		queryDisplay.setColumns(16);
		SearchTermPanel stp = new SearchTermPanel();
		searchTermPanels.add(stp);
		queryPanel.add(stp);
		fewerButton.setEnabled(true);
		mainScrollPane.setViewportView(queryViewport);
		revalidate();
	}

	private void removeQueryField() {
		searchTermPanels.remove(searchTermPanels.size() - 1);
		searchTermPanels.get(searchTermPanels.size() - 1).getAndOrBox().setVisible(false);
		searchTermPanels.get(searchTermPanels.size() - 1).validate();
		searchTermPanels.get(0).searchTermField.setText(searchTermPanels.get(0).searchTermField.getText());
		queryPanel.remove(queryPanel.getComponentCount() - 1);
		if (queryPanel.getComponentCount() == 1)
			fewerButton.setEnabled(false);
		mainScrollPane.setViewportView(queryViewport);
		revalidate();
	}

	public Metadata.MetadataQuery[] showSearchDialog() {
		return showSearchDialog("Metadata Search");
	}
	public Metadata.MetadataQuery[] showSearchDialog(String title) {
		myDialog = new JDialog(MetaOmGraph.getMainWindow(), title, true);
		myDialog.getContentPane().setLayout(new BorderLayout());
		myDialog.getContentPane().add(this, "Center");
		JPanel buttonPanel = new JPanel();

		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");
		isOK = false;
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isOK = true;
				myDialog.dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isOK = false;
				myDialog.dispose();
			}
		});
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		myDialog.getContentPane().add(buttonPanel, "Last");
		myDialog.setSize(1000, 300);
		//myDialog.pack();
		int width = MetaOmGraph.getMainWindow().getWidth();
		int height = MetaOmGraph.getMainWindow().getHeight();
		myDialog.setLocation((width - myDialog.getWidth()) / 2, (height - myDialog.getHeight()) / 2);
		AbstractAction helpAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MetaOmGraph.getHelpListener().actionPerformed(new ActionEvent(myDialog, 0, "metadatasort.php"));
			}
		};
		myDialog.getRootPane().getActionMap().put("help", helpAction);
		InputMap im = getRootPane().getInputMap(1);
		im.put(KeyStroke.getKeyStroke(112, 0), "help");
		myDialog.setVisible(true);
		if (isOK) {
			matchAll = allButton.isSelected();
			//matchCase = caseButton.isSelected();
			//JOptionPane.showMessageDialog(null, "mcb:"+matchCase);
			queryCount = 0;
			ArrayList<Metadata.MetadataQuery> result = new ArrayList();
			for (int x = 0; x < queryPanel.getComponentCount(); x++) {
				Component thisComponent = queryPanel.getComponent(x);
				if ((thisComponent instanceof SearchTermPanel)) {
					if (!((SearchTermPanel) thisComponent).getQuery().getTerm().equals("")) {
						result.add(((SearchTermPanel) thisComponent).getQuery());
					}
				}
			}

			queryCount = result.size();
			Metadata.MetadataQuery[] resultArray = new Metadata.MetadataQuery[queryCount];
			for (int i = 0; i < queryCount; i++)
				resultArray[i] = result.get(i);

			return resultArray;
		}
		return null;
	}

	public int getQueryCount() {
		return queryCount;
	}

	public boolean matchAll() {
		return matchAll;
	}

	/*public boolean matchCase() {
		return matchCase;
	}*/

	public String getSecondSort() {
		return secondSort;
	}

	private class SearchTermPanel extends JPanel {
		private JComboBox fieldBox;
		private JComboBox matchBox;
		private JComboBox andOrBox;
		private JTextField searchTermField;
		private JCheckBox matchCasebox;
		private boolean populated = false;

		public SearchTermPanel() {
			this("Any field", SearchMatchType.CONTAINS, "",false);
		}

		public SearchTermPanel(Metadata.MetadataQuery myQuery) {
			this(myQuery.getField(), myQuery.getMatchType(), myQuery.getTerm(),myQuery.isCaseSensitive());
		}

		public SearchTermPanel(String field, SearchMatchType matchType, String term,boolean matchCase) {
			fieldBox = new JComboBox(fieldBoxTerms);
			fieldBox.setSelectedItem(field);
			matchBox = new JComboBox(new String[] { "contains", "is", "is not", "does not contain" });
			matchBox.setSelectedIndex(matchType.ordinal());
			searchTermField = new JTextField(term);
			searchTermField.setColumns(20);
			searchTermField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e) {
					changedUpdate(e);
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
					changedUpdate(e);
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					if (searchTermField.getText() != null && !searchTermField.getText().trim().equals("")) {
						populated = true;
					} else {
						populated = false;
					}
					boolean shouldDisplayQuery = true;
					boolean lastWasOr = false;
					String result = "";
					for (int i = 0; i < searchTermPanels.size(); i++) {
						SearchTermPanel panel = searchTermPanels.get(i);
						if (!panel.isPopulated()) {
							shouldDisplayQuery = false;
							queryDisplay.setText("Complete Query for Preview");
							queryDisplay.setToolTipText("A preview of the logic that will be applied when using this filter");
							queryDisplay.setColumns(16);
							TreeSearchQueryConstructionPanel.this.revalidate();
							break;
						} else {
							if (panel.isMatchAND()) {
								if (lastWasOr) {
									result += panel.getColString() + " " + panel.getMatchString() + " " + panel.getSearchTerm() + ")" + " and ";
								} else {
									result += panel.getColString() + " " + panel.getMatchString() + " " + panel.getSearchTerm() + " and ";
								}
								lastWasOr = false;
							} else if (panel.isMatchOR()) {
								if (lastWasOr) {
									result += panel.getColString() + " " + panel.getMatchString() + " " + panel.getSearchTerm() + " or ";
								} else {
									result += "(" + panel.getColString() + " " + panel.getMatchString() + " " + panel.getSearchTerm() + " or ";
								}
								lastWasOr = true;
							} else {
								if (lastWasOr) {
									result += panel.getColString() + " " + panel.getMatchString() + " " + panel.getSearchTerm() + ")";
								} else {
									result += panel.getColString() + " " + panel.getMatchString() + " " + panel.getSearchTerm();
								}
							}
						}
					}
					if (shouldDisplayQuery) {
						if (result.length() > 150) {
							queryDisplay.setText("Hover mouse here to display query");
							queryDisplay.setColumns(19);
						} else {
							queryDisplay.setText(result);
							queryDisplay.setColumns((int) (result.length() / 1.7));

						}
						queryDisplay.setToolTipText(result);
						TreeSearchQueryConstructionPanel.this.revalidate();
					}
				}
			});
			andOrBox = new JComboBox(new String[] { "AND", "OR" });
			andOrBox.setToolTipText("For results to be included:\nAll queries marked with AND must be true\nOr any query marked with OR can be true");
			andOrBox.setVisible(false);
			andOrBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					searchTermField.setText(searchTermField.getText());
				}
			});
			matchBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					searchTermField.setText(searchTermField.getText());
				}
			});
			fieldBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					searchTermField.setText(searchTermField.getText());
				}
			});
			matchCasebox = new JCheckBox("Match case");
			if(matchCase) {
				matchCasebox.setSelected(true);
			}
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.fill = 0;
			c.weightx = 0.0D;
			c.weighty = 0.5D;
			c.anchor = 19;
			add(fieldBox, c);
			c.gridx = 1;
			add(matchBox, c);
			c.gridx = 2;
			c.weightx = 1.0D;
			c.fill = 1;
			add(searchTermField, c);
			c.fill = 0;
			c.gridx = 3;
			add(andOrBox, c);
			c.gridx = 4;
			c.weightx = 0.0D;
			c.fill = 1;
			add(matchCasebox, c);

			andOrBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					groupBtnRadio.setSelected(invisibleButton.getModel(), true);
				}
			});
		}

		public void setMatchAsAND() {
			andOrBox.setSelectedIndex(0);
		}

		public void setMatchAsOR() {
			andOrBox.setSelectedIndex(1);
		}

		public boolean isMatchAND() {
			return andOrBox.isVisible() && andOrBox.getSelectedIndex() == 0;
		}

		public boolean isMatchOR() {
			return andOrBox.isVisible() && andOrBox.getSelectedIndex() == 1;
		}

		public JComboBox getAndOrBox() {
			return andOrBox;
		}

		public String getField() {
			return fieldBox.getSelectedItem().toString().trim();
		}
		
		public boolean matchCase() {
			return matchCasebox.isSelected();
		}

		public boolean isPopulated() {
			return populated;
		}

		public JTextField getSearchTermField() {
			return searchTermField;
		}
		
		public SearchMatchType getMatchType() {
			String selectedVal = matchBox.getSelectedItem().toString();
			if(selectedVal.equalsIgnoreCase("is")) {
				return SearchMatchType.IS;
			}else if(selectedVal.equalsIgnoreCase("contains")) {
				return SearchMatchType.CONTAINS;
			}else if(selectedVal.equalsIgnoreCase("is not")) {
				return SearchMatchType.NOT;
			}else {
				return SearchMatchType.DOES_NOT_CONTAIN;
			}
		}

		public String getMatchString() {
			return matchBox.getSelectedItem().toString();
		}

		public String getColString() {
			return fieldBox.getSelectedItem().toString();
		}

		public String getSearchTerm() {
			return searchTermField.getText().trim();
		}

		public Metadata.MetadataQuery getQuery() {
			Metadata.MetadataQuery result = new Metadata.MetadataQuery();
			result.setAND(isMatchAND());
			result.setOR(isMatchOR());
			result.setField(getField());
			result.setTerm(getSearchTerm());
			result.setMatchType(getMatchType());
			result.setCaseSensitive(matchCase());
			return result;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("more".equals(e.getActionCommand())) {
			addQueryField();
			return;
		}
		if ("fewer".equals(e.getActionCommand())) {
			removeQueryField();
			return;
		}
	}

	public static class QuerySet implements Serializable, SimpleXMLizable<QuerySet> {
		public Metadata.MetadataQuery[] queries;
		public boolean matchAll;

		public QuerySet(Metadata.MetadataQuery[] queries, boolean matchAll) {
			this.queries = queries;
			this.matchAll = matchAll;
		}

		public QuerySet() {
		}

		@Override
		public SimpleXMLElement toXML() {
			SimpleXMLElement result = new SimpleXMLElement(getXMLElementName()).setAttribute("matchAll",
					matchAll ? "true" : "false");
			for (int x = 0; x < queries.length; x++) {
				result.add(queries[x].toXML());
			}
			return result;
		}
		
		
		/**
		 * 
		 * @param xMLStreamWriter
		 * @param name
		 * @throws XMLStreamException
		 * 
		 * Method to write Advanced Search queries to the .mog file using StAX parser
		 * 
		 */
		public void writeToXML(XMLStreamWriter xMLStreamWriter, String name) throws XMLStreamException {
			
			xMLStreamWriter.writeStartElement(getXMLElementName());
			xMLStreamWriter.writeAttribute("matchAll", matchAll ? "true" : "false");
			xMLStreamWriter.writeAttribute("name", name);
			
			for (int x = 0; x < queries.length; x++) {
				queries[x].writeToXML(xMLStreamWriter);
			}
			xMLStreamWriter.writeEndElement();
		}

		@Override
		public QuerySet fromXML(SimpleXMLElement source) {
			matchAll = "true".equals(source.getAttributeValue("matchAll"));
			queries = new Metadata.MetadataQuery[source.getChildCount()];
			for (int x = 0; x < queries.length; x++) {
				queries[x] = new Metadata.MetadataQuery();
				queries[x].fromXML(source.getChildAt(x));
			}
			return this;
		}
		
		
		/**
		 * 
		 * @param querysetMatchAll
		 * @param queriesList
		 * 
		 * Method to initialize Advanced Search queries
		 * This method is called from the MetaOmProject loadProject method during the XML read
		 * 
		 */
		public void initializeQuerySet(boolean querysetMatchAll, List<MetadataQuery> queriesList) {
			matchAll = querysetMatchAll;
			
			if(queriesList != null && !queriesList.isEmpty()) {
				
			queries = new Metadata.MetadataQuery[queriesList.size()];
			
			for (int x = 0; x < queries.length; x++) {
				queries[x] = queriesList.get(x);
			}
			
			}
			
		}

		public static String getXMLElementName() {
			return "querySet";
		}
	}

	@Override
	public QuerySet getSaveData() {
		boolean all = allButton.isSelected();
		ArrayList<Metadata.MetadataQuery> result = new ArrayList();
		for (int x = 0; x < queryPanel.getComponentCount(); x++) {
			Component thisComponent = queryPanel.getComponent(x);
			if ((thisComponent instanceof SearchTermPanel)) {
				if (!((SearchTermPanel) thisComponent).getQuery().getTerm().equals(""))
					result.add(((SearchTermPanel) thisComponent).getQuery());
			}
		}
		Metadata.MetadataQuery[] resultArray = new Metadata.MetadataQuery[result.size()];
		for (int i = 0; i < result.size(); i++)
			resultArray[i] = result.get(i);
		
		return new QuerySet(resultArray, all);
	}

	@Override
	public void loadData(QuerySet data) {
		queryPanel.removeAll();
		Metadata.MetadataQuery[] queries = data.queries;
		for (int x = 0; x < queries.length; x++) {
			queryPanel.add(new SearchTermPanel(queries[x]));
		}
		if (queries.length > 1) {
			fewerButton.setEnabled(true);
		} else {
			fewerButton.setEnabled(false);
		}
		
		if (data.matchAll) {
			allButton.setSelected(true);
		} else {
			anyButton.setSelected(true);
		}
		mainScrollPane.setViewportView(queryViewport);
	}

	@Override
	public String getNoun() {
		return "query set";
	}
}
