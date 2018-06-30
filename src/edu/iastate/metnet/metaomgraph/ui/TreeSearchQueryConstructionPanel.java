package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.HashLoadable;
import edu.iastate.metnet.metaomgraph.HashtableSavePanel;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmHelpListener;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.Metadata;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.utils.qdxml.SimpleXMLElement;
import edu.iastate.metnet.metaomgraph.utils.qdxml.SimpleXMLizable;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

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

	private int queryCount;
	private boolean matchAll;
	// urmi
	private JRadioButton caseButton;
	private boolean matchCase;
	private String secondSort;
	private MetaOmProject myProject;
	private static final String ANY_FIELD_STRING = "Any field";

	private boolean searchDataTable;

	public TreeSearchQueryConstructionPanel(MetaOmProject project) {
		this(project,false);
	 }

	public TreeSearchQueryConstructionPanel(MetaOmProject project, boolean searchDataTable) {
		this.searchDataTable = searchDataTable;
		myProject = project;
		setLayout(new BorderLayout());
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
		// String[] fields = myProject.getMetadata().getFields();

		String[] fields=null;
		if (searchDataTable==false) {
			/**
			 * @author urmi changed to : now fields are from Metadatahybrid
			 */
			fields = myProject.getMetadataHybrid().getMetadataHeaders();
			//JOptionPane.showMessageDialog(null, "flds:"+Arrays.toString(fields));
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
		queryPanel.add(new SearchTermPanel());
		mainScrollPane = new JScrollPane(queryViewport);

		allButton = new JRadioButton("Match all of the following");
		anyButton = new JRadioButton("Match any of the following");
		caseButton = new JRadioButton("Match case");
		/*
		 * ButtonGroup allAnyGroup = new ButtonGroup(); allAnyGroup.add(caseButton);
		 * allAnyGroup.add(allButton); allAnyGroup.add(anyButton);
		 */
		allButton.setSelected(true);
		JPanel allAnyPanel = new JPanel();
		allAnyPanel.add(caseButton);
		allAnyPanel.add(allButton);
		allAnyPanel.add(anyButton);
		add(allAnyPanel, "First");
		add(mainScrollPane, "Center");

		HashtableSavePanel savePanel = new HashtableSavePanel(myProject.getSavedQueries(), this);
		add(savePanel, "Before");
		add(queryButtonPanel, "Last");
		setBorder(BorderFactory.createEtchedBorder());
	}

	private void addQueryField() {
		queryPanel.add(new SearchTermPanel());
		fewerButton.setEnabled(true);
		mainScrollPane.setViewportView(queryViewport);
	}

	private void removeQueryField() {
		queryPanel.remove(queryPanel.getComponentCount() - 1);
		if (queryPanel.getComponentCount() == 1)
			fewerButton.setEnabled(false);
		mainScrollPane.setViewportView(queryViewport);
	}

	public Metadata.MetadataQuery[] showSearchDialog() {
		myDialog = new JDialog(MetaOmGraph.getMainWindow(), "Metadata Search", true);
		myDialog.getContentPane().setLayout(new BorderLayout());
		myDialog.getContentPane().add(this, "Center");
		JPanel buttonPanel = new JPanel();

		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");
		isOK = false;
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isOK = true;
				myDialog.dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isOK = false;
				myDialog.dispose();
			}
		});
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		myDialog.getContentPane().add(buttonPanel, "Last");
		myDialog.setSize(640, 480);
		int width = MetaOmGraph.getMainWindow().getWidth();
		int height = MetaOmGraph.getMainWindow().getHeight();
		myDialog.setLocation((width - myDialog.getWidth()) / 2, (height - myDialog.getHeight()) / 2);
		AbstractAction helpAction = new AbstractAction() {
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
			matchCase = caseButton.isSelected();
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

	public boolean matchCase() {
		return matchCase;
	}

	public String getSecondSort() {
		return secondSort;
	}

	private class SearchTermPanel extends JPanel {
		private JComboBox fieldBox;
		private JComboBox matchBox;
		private JTextField searchTermField;

		public SearchTermPanel() {
			this("Any field", false, "");
		}

		public SearchTermPanel(Metadata.MetadataQuery myQuery) {
			this(myQuery.getField(), myQuery.isExact(), myQuery.getTerm());
		}

		public SearchTermPanel(String field, boolean exact, String term) {
			fieldBox = new JComboBox(fieldBoxTerms);
			fieldBox.setSelectedItem(field);
			matchBox = new JComboBox(new String[] { "contains", "is" });
			matchBox.setSelectedIndex(exact ? 1 : 0);
			searchTermField = new JTextField(term);
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
		}

		public String getField() {
			// changed //urmi
			// if (fieldBox.getSelectedIndex() == 0) return "";

			return fieldBox.getSelectedItem().toString().trim();
		}

		public boolean exactMatch() {
			return matchBox.getSelectedItem().toString().equals("is");
		}

		public String getMatchType() {
			return matchBox.getSelectedItem().toString();
		}

		public String getSearchTerm() {
			return searchTermField.getText().trim();
		}

		public Metadata.MetadataQuery getQuery() {
			Metadata.MetadataQuery result = new Metadata.MetadataQuery();
			result.setField(getField());
			result.setTerm(getSearchTerm());
			result.setExact(exactMatch());
			return result;
		}
	}

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

		public SimpleXMLElement toXML() {
			SimpleXMLElement result = new SimpleXMLElement(getXMLElementName()).setAttribute("matchAll",
					matchAll ? "true" : "false");
			for (int x = 0; x < queries.length; x++) {
				result.add(queries[x].toXML());
			}
			return result;
		}

		public QuerySet fromXML(SimpleXMLElement source) {
			matchAll = "true".equals(source.getAttributeValue("matchAll"));
			queries = new Metadata.MetadataQuery[source.getChildCount()];
			for (int x = 0; x < queries.length; x++) {
				queries[x] = new Metadata.MetadataQuery();
				queries[x].fromXML(source.getChildAt(x));
			}
			return this;
		}

		public static String getXMLElementName() {
			return "querySet";
		}
	}

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
		if (matchAll) {
			allButton.setSelected(true);
		} else {
			anyButton.setSelected(true);
		}
		mainScrollPane.setViewportView(queryViewport);
	}

	public String getNoun() {
		return "query set";
	}
}
