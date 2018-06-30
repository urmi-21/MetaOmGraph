package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.Metadata;
import edu.iastate.metnet.metaomgraph.MetadataCollection;

import javax.swing.*;
import javax.swing.tree.*;

import org.dizitart.no2.Document;

import apple.laf.JRSUIUtils.Tree;

import javax.swing.event.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class MetadataEditor extends JFrame implements ActionListener {

	private DefaultListModel model = new DefaultListModel();
	private int count = 0;
	private JTree tree;
	
	private JList list;
	private JButton buildButton;
	private DefaultTreeModel treeModel;
	private static List<String> headers;
	// private List<String> headers2;
	private static List<Document> metadata = null;
	private static MetadataCollection mo = null;
	private static MetaOmProject active_project = null;
	private static String exp_root = "";
	private static String chip_root = "";
	public Metadataviewer objView=null;
	public List<String> getHeaders() {
		return this.headers;
	}

	// function to parse the csv data to xml format
	private String parsedata() {

		// get root node
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		// exp node is at 0 and chip at 1
		TreeNode root_exp = root.getChildAt(0);
		TreeNode root_chip = root.getChildAt(1);
		// get attributes of exp and chip node
		int exp_root_child_count = tree.getModel().getChildCount(root_exp);
		int chip_root_child_count = tree.getModel().getChildCount(root_chip);
		String[] exp_attributes = new String[exp_root_child_count];
		String[] chip_attributes = new String[chip_root_child_count];

		for (int i = 0; i < exp_root_child_count; i++) {
			exp_attributes[i] = tree.getModel().getChild(root_exp, i).toString();
		}

		for (int i = 0; i < chip_root_child_count; i++) {
			chip_attributes[i] = (String) tree.getModel().getChild(root_chip, i).toString();
		}

		// JOptionPane.showMessageDialog(null, exp_attributes);
		// JOptionPane.showMessageDialog(null, chip_attributes);

		// start building XML string
		String result = "";
		String xml_header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		result += xml_header;
		result += "<Experiments>\n";

		// for each experiment
		// get list of uniq exps
		String expfield = exp_root;
		String chipfield = chip_root;
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
			result += "<Experiment name=\"" + uniq_exps.get(i) + "\">\n";
			// finalfile+="<Title>" + "Title:" + i + "</Title>\n";

			for (int l = 0; l < metadata.size(); l++) {
				String curr_exp2 = metadata.get(l).get(expfield).toString();
				if (curr_exp2 == curr_exp) {
					p = l;
				}

			}

			// for all exp attributes

			for (int ea = 0; ea < exp_attributes.length; ea++) {
				result += "<" + exp_attributes[ea] + ">" + metadata.get(p).get(exp_attributes[ea]).toString() + "</"
						+ exp_attributes[ea] + ">\n";
			}

			for (int j = 0; j < metadata.size(); j++) {

				if (curr_exp.equals(metadata.get(j).get(expfield).toString())) {
					curr_chip = metadata.get(j).get(chipfield).toString();
					// System.out.println("<chip name=\"" + curr_chip + "\">");
					// System.out.println("<A>" + metadata.get(j).get("study_type").toString() +
					// "</A>");
					// System.out.println("<B>" +
					// metadata.get(j).get("design_description").toString() + "</B>");
					// System.out.println("</chip>");

					result += "<chip name=\"" + curr_chip + "\">\n";

					for (int ca = 0; ca < chip_attributes.length; ca++) {
						result += "<" + chip_attributes[ca] + ">" + metadata.get(p).get(chip_attributes[ca]).toString()
								+ "</" + chip_attributes[ca] + ">\n";
					}
					// result+="<experiment_attribute>" +
					// metadata.get(j).get("experiment_attribute").toString() +
					// "</experiment_attribute>\n";

					result += "</chip>\n";

				}

			}

			result += "</Experiment>\n";

		}
		result += "</Experiments>";
		// JOptionPane.showMessageDialog(null, result);
		return result;
	}

	public void actionPerformed(ActionEvent e) {
		if ("buildxml".equals(e.getActionCommand())) {
			JOptionPane.showMessageDialog(null, "Started to build data model", getTitle(), JOptionPane.WARNING_MESSAGE);

			/*
			 * String xml_header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; String
			 * finalfile = xml_header + "\n"; finalfile += "<Experiments>\n"; finalfile +=
			 * "<Experiment name=\"" + "U1" + "\">\n"; finalfile += "<chip name=\"" +
			 * "curr_chip" + "\">\n"; finalfile += "</chip>\n"; finalfile +=
			 * "</Experiment>\n"; finalfile += "</Experiments>\n";
			 */

			String finalfile = parsedata();
			InputStream stream = null;
			try {
				stream = new ByteArrayInputStream(finalfile.getBytes(StandardCharsets.UTF_8.name()));

			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				MetaOmGraph.getActiveProject().loadMetadata((InputStream) null);
				MetaOmGraph.getActiveProject().loadMetadata(stream);
				// MetaOmGraph.getActiveProject().setChanged(true);
				MetaOmGraph.updateWindow();
				treeModel=(DefaultTreeModel) tree.getModel();
				// MetaOmGraph.returnprojectTableFrame().setVisible(true);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//objView=new Metadataviewer(metadata, headers);
			objView=new Metadataviewer("MetadataViewer");
			objView.setVisible(true);
			
		}
	}

	private static DefaultTreeModel getDefaultTreeModel() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("MetaOmGraph");
		DefaultMutableTreeNode parent;
		DefaultMutableTreeNode nparent;
		parent = new DefaultMutableTreeNode(exp_root);
		root.add(parent);
		parent = new DefaultMutableTreeNode(chip_root);
		root.add(parent);
		
		return new DefaultTreeModel(root);
	}

	private void expandTree(JTree tree) {

		// System.out.println("call expand tree");
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
		Enumeration e = root.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (node.isLeaf())
				continue;
			int row = tree.getRowForPath(new TreePath(node.getPath()));
			tree.expandRow(row);
		}
	}

	// constructor
	public MetadataEditor(List<String> list, List<Document> md, MetadataCollection obj, String exp, String chip,
			MetaOmProject mop_obj) {
		super("Metadata Editor for MOG");
		active_project = mop_obj;
		mo = obj;
		metadata = md;
		headers = new ArrayList<>();
		headers = list;
		// headers2 = new ArrayList<>();
		// headers2 = list;
		exp_root = exp;
		chip_root = chip;

		// add tree
		treeModel = getDefaultTreeModel();
		tree = new JTree(treeModel);
		tree.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setDropMode(DropMode.ON_OR_INSERT);
		tree.setDragEnabled(true);
		
		tree.setTransferHandler(new MogTreeTransferHandler());
		expandTree(tree);
		///////////////////////////////
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
		left.add(createList(model, this.headers));

		splitPane.setLeftComponent(left);
		splitPane.setSize(200, 500);

		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));
		right.add(new JScrollPane(tree), BoxLayout.X_AXIS);
		right.setBorder(BorderFactory.createTitledBorder("Assigning to MOG data model"));

		buildButton = new JButton();
		buildButton.setName("Build XML");
		buildButton.setText("Importing this data model to MOG");
		buildButton.addActionListener(this);
		buildButton.setActionCommand("buildxml");
		getContentPane().add(buildButton, BorderLayout.SOUTH);

		splitPane.setRightComponent(right);
		getContentPane().add(splitPane, BoxLayout.X_AXIS);
		getContentPane().setPreferredSize(new Dimension(800, 500));
		getContentPane().setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.pack();
	}

	private JPanel createList(DefaultListModel listModel, List<String> headers) {
		for (int i = 0; i < headers.size(); i++) {
			listModel.addElement(headers.get(i));
		}

		list = new JList(listModel);
		
		list.setDragEnabled(true);
		
		list.setTransferHandler(new ListTransferHandler());
		// list.setTransferHandler(new MogTreeTransferHandler());
		list.setDropMode(DropMode.INSERT);
		list.getSelectionModel().setSelectionMode(
			      ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		// add list to jpanel
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(400, 100));
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createTitledBorder("Imported Headers"));

		
		return panel;
	}
	
	//urmi
	public void displayEditor() {
		//this.setVisible(true);
		JFrame jf=new JFrame("new");
		expandTree(tree);
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
		left.add(createList(model, this.headers));
		splitPane.setLeftComponent(left);
		splitPane.setSize(200, 500);
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));
		right.add(new JScrollPane(tree), BoxLayout.X_AXIS);
		right.setBorder(BorderFactory.createTitledBorder("Assigning to MOG data model"));
		jf.getContentPane().add(buildButton, BorderLayout.SOUTH);

		splitPane.setRightComponent(right);
		jf.getContentPane().add(splitPane, BoxLayout.X_AXIS);
		jf.getContentPane().setPreferredSize(new Dimension(800, 500));
		jf.getContentPane().setVisible(true);
		jf.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
		
		
	}
	
	public int getnum() {
		int x=0;
		x=metadata.size();
		return x;
		
	}

	public static void increaseFont(String type) {
		Font font = UIManager.getFont(type);
		font = font.deriveFont(font.getSize() + 4f);
		UIManager.put(type, font);
	}
	

	public static void createAndShowGUI() {

		// Create and set up the window.
		MetadataEditor test = new MetadataEditor(headers, metadata, mo, exp_root, chip_root, active_project);
		test.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// Display the window.
		test.pack();
		test.setVisible(true);
	}

	public static void main(String[] args) {
		System.out.println("MAIN");

	}
}
