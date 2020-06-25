package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.tree.TreeNode;
import org.dizitart.no2.Document;
import org.dizitart.no2.Filter;
import org.dizitart.no2.filters.Filters;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataCollection;
import edu.iastate.metnet.metaomgraph.MetadataTreeStructure;

public class DisplayMetadataEditor extends JDialog implements ActionListener {

	private JTree tree = null;
	private List<String> headers;
	private DefaultListModel model = new DefaultListModel();
	private JList list;
	private JButton save;
	private JButton reset;
	private JButton _import;
	private MetadataTreeStructure treeStruct = null;
	private List<Document> metadata = null;
	private MetadataCollection mogColl = null;

	public DisplayMetadataEditor() {
		// TODO Auto-generated constructor stub
		//treeStruct = MetaOmGraph.getActiveProject().returntree();
		setModal(true);
		this.tree = treeStruct.getTree();
		this.headers = treeStruct.getList();
		mogColl = MetaOmGraph.getActiveProject().getMetadataHybrid().getMetadataCollection();
		this.metadata = mogColl.getAllData();
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

		// add buttons
		JPanel buttonPanel = new JPanel();

		save = new JButton();
		save.setName("Save Tree");
		save.setText("Save Tree");
		save.addActionListener(this);
		save.setActionCommand("save");
		buttonPanel.add(save);
		reset = new JButton();
		reset.setName("Reset Tree");
		reset.setText("Reset Tree");
		reset.addActionListener(this);
		reset.setActionCommand("reset");
		buttonPanel.add(reset);
		_import = new JButton();
		_import.setName("Import Tree");
		_import.setText("Import Tree");
		_import.addActionListener(this);
		_import.setActionCommand("import");
		buttonPanel.add(_import);
		getContentPane().add(buttonPanel, BorderLayout.NORTH);
		// getContentPane().add(fPane, BorderLayout.CENTER);

		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
		// JOptionPane.showMessageDialog(null, headers.get(1));
		left.add(createList(model, this.headers));
		splitPane.setLeftComponent(left);
		splitPane.setSize(200, 500);
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));
		right.add(new JScrollPane(tree), BoxLayout.X_AXIS);
		right.setBorder(BorderFactory.createTitledBorder("Assigning to MOG data model"));

		splitPane.setRightComponent(right);
		getContentPane().add(splitPane, BoxLayout.X_AXIS);
		getContentPane().setPreferredSize(new Dimension(800, 500));
		getContentPane().setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.pack();
		this.setTitle("Metadata Structure");
	}

	// functions

	private JPanel createList(DefaultListModel listModel, List<String> headers) {
		for (int i = 0; i < headers.size(); i++) {
			listModel.addElement(headers.get(i));
		}

		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setDragEnabled(true);
		list.setTransferHandler(new ListTransferHandler());
		list.setDropMode(DropMode.INSERT);

		// add list to jpanel
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(400, 100));
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createTitledBorder("Imported Headers"));

		return panel;
	}

	// function to parse the csv data to xml format
	// add progress bar
	// change to handle multiple matches; include all
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
			chip_attributes[i] = tree.getModel().getChild(root_chip, i).toString();
		}
		// start building XML string
		String result = "";
		String xml_header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		result += xml_header;
		result += "<Experiments>\n";
		// do for each experiment
		// get list of uniq exps
		String expfield = root_exp.toString();
		String chipfield = root_chip.toString();
		String curr_exp = "";
		String curr_chip = "";
		List<String> uniq_exps = new ArrayList<String>();
		// build list uniq_exps to store all unique exps
		for (int i = 0; i < metadata.size(); i++) {
			curr_exp = metadata.get(i).get(expfield).toString();
			if (!uniq_exps.contains(curr_exp)) {
				uniq_exps.add(curr_exp);
			}
		}

		// add progressbar

		// progressbar 2
		JFrame frame2 = new JFrame("Importing Metadata...");
		JProgressBar pBar = new JProgressBar();
		// init pbar
		pBar.setMinimum(0);
		pBar.setMaximum(uniq_exps.size());
		pBar.setStringPainted(true);
		frame2.add(pBar);
		frame2.setSize(400, 80);
		// frame.pack();
		frame2.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		// set the frame in the middle of screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame2.setLocation(dim.width / 2 - frame2.getSize().width / 2, dim.height / 2 - frame2.getSize().height / 2);
		frame2.setResizable(false);
		frame2.setVisible(true);
		new Thread() {
			@Override
			public void run() {
				pBar.setValue(40);
			}
		}.start();

		int p = 0;

		// go through all experiment names and search data

		for (int i = 0; i < uniq_exps.size(); i++) {
			final int percent = i;
			// update progressbar
			if (i % 10 == 0) {
				// update progress bar
				pBar.setValue(percent);
				pBar.update(pBar.getGraphics());
				pBar.repaint();
			}

			curr_exp = uniq_exps.get(i);
			// check for empty exp_name
			if (uniq_exps.get(i).equals("")) {
				JOptionPane.showMessageDialog(null,
						"Found empty value for Study column. Please check Metadata file and try again.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return "";

			}
			result += "<Experiment name=\"" + uniq_exps.get(i) + "\">\n";
			// find positions(row) of curr exp in metadata and store in p
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
					// check chipname is not null
					if (curr_chip.equals("")) {
						JOptionPane.showMessageDialog(null,
								"Found empty value for Run column. Please check Metadata file and try again.", "Error",
								JOptionPane.ERROR_MESSAGE);
						return "";

					}

					result += "<chip name=\"" + curr_chip + "\">\n";
					for (int ca = 0; ca < chip_attributes.length; ca++) {
						result += "<" + chip_attributes[ca] + ">" + metadata.get(j).get(chip_attributes[ca]).toString()
								+ "</" + chip_attributes[ca] + ">\n";
					}
					result += "</chip>\n";

				}

			}

			result += "</Experiment>\n";

		}
		frame2.dispose();

		result += "</Experiments>";
		// JOptionPane.showMessageDialog(null, "Parsing Complete");
		JOptionPane.showMessageDialog(null, result);
		// save string to file
		/*
		 * JOptionPane.showMessageDialog(null,
		 * "Writing to file: D:\\MOGdata\\mog_testdata\\f2ilename.txt"); try
		 * (PrintWriter out = new
		 * PrintWriter("D:\\MOGdata\\\\mog_testdata\\f1ilename.txt")) {
		 * out.println(result); } catch (FileNotFoundException e) { // TODO
		 * Auto-generated catch e.printStackTrace(); }
		 */
		return result;

	}

	// function to parse the csv data to xml format
	// progress bar added
	// change to handle multiple matches; include all
	// uses hashmaps and use NO2 lib for faster search
	private String parsedata2() {
		// get root node
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		// exp node is at 0, samp is 1 and chip at 2
		TreeNode root_exp = root.getChildAt(0);
		TreeNode root_samp = root.getChildAt(1);
		TreeNode root_chip = root.getChildAt(2);
		/*
		 * XML will have three levels Experiment --OuterSample --Run Change this to be
		 * more general later
		 */
		TreeNode[] structure = { root_exp, root_samp, root_chip }; // this stores the nodes in the tree structure outer
		String parseRes = returnXMLtree(structure);
		// remove all /n MOG doesn't show metadata correctly without this
		parseRes = parseRes.replaceAll("\n", "");

		/*
		 * // save string to file for testing JOptionPane.showMessageDialog(null,
		 * "Writing to file: D:\\MOGdata\\mog_testdata\\f2ilename.txt"); try
		 * (PrintWriter out = new
		 * PrintWriter("D:\\MOGdata\\\\mog_testdata\\f2ilename.txt")) {
		 * out.println(parseRes); } catch (FileNotFoundException e) { // TODO
		 * Auto-generated catch e.printStackTrace(); }
		 */
		return parseRes;
	}

	private String returnXMLtree(TreeNode[] structure) {
		org.jdom.Document res = new org.jdom.Document();
		XMLOutputter outter = new XMLOutputter();
		outter.setFormat(Format.getPrettyFormat());
		Element root1 = new Element("Experiments");
		res.setRootElement(root1);
		res = putThisElement(structure, res);
		String resDoc = new XMLOutputter().outputString(res);
		return resDoc;
	}

	// urmi add XML node to a tree and recursively add its children
	private Element addDatatoNode(TreeNode[] nodes, String val, int thisPos, List<Document> data) {

		// change this later to be more general
		if (thisPos > 2) {
			Element target = new Element("");
			return target;
		}

		Element target = null;
		Deque<String> stack = new ArrayDeque<String>();
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		TreeNode node = nodes[thisPos];
		int exp_root_child_count = tree.getModel().getChildCount(node);
		String nodeName = node.toString();
		// if this is exp asmp or run node
		if (nodeName == root.getChildAt(0).toString()) {
			target = new Element("Experiment");
			target.setAttribute("name", val);

		} else if (nodeName == root.getChildAt(1).toString()) {
			target = new Element("OuterSamp");
			target.setAttribute("name", val);
		} else if (nodeName == root.getChildAt(2).toString()) {
			target = new Element("chip");
			target.setAttribute("name", val);
		}

		// add all attributes of current node
		String[] attributes = new String[exp_root_child_count];
		for (int i = 0; i < exp_root_child_count; i++) {
			attributes[i] = tree.getModel().getChild(node, i).toString();
		}
		for (int j = 0; j < attributes.length; j++) {
			// String thisVal = "" + Integer.toString(j);
			// find all instance of attribute colums such that exp node col==uniqExp.get(i)
			List<String> matches = new ArrayList();
			for (int d = 0; d < data.size(); d++) {
				/*
				 * if (data.get(d).get(nodeName).toString().equals(val)) {
				 * matches.add(data.get(d).get(attributes[j]).toString()); }
				 */
				matches.add(data.get(d).get(attributes[j]).toString());
			}
			// remove duplicate matches using set
			Set<String> tempset = new HashSet<String>();
			tempset.addAll(matches);
			matches.clear();
			matches.addAll(tempset);
			// add all attributes in matches
			if (matches.size() > 1) {
				// add multiple vals; add _m
				for (int m = 0; m < matches.size(); m++) {
					target.addContent(
							new Element(attributes[j] + "-" + Integer.toString(m + 1)).addContent(matches.get(m)));
				}
			} else {
				// only one value to add
				target.addContent(new Element(attributes[j]).addContent(matches.get(0)));
			}

		} // all attributes of node added

		// add next level to stack and recursively build tree
		// this pos should be less than total levels-1
		if (thisPos < 2) {
			String nextCol = nodes[thisPos + 1].toString();
			List<String> nxtlist = new ArrayList();
			for (int d = 0; d < data.size(); d++) {
				nxtlist.add(data.get(d).get(nextCol).toString());
			}
			// remove duplicate matches using set
			Set<String> tempset = new HashSet<String>();
			tempset.addAll(nxtlist);
			nxtlist.clear();
			nxtlist.addAll(tempset);
			// add to stack
			for (int s = 0; s < nxtlist.size(); s++) {
				stack.push(nxtlist.get(s));
			}

			while (!stack.isEmpty()) {
				String thisval = stack.pop();
				List<Document> thisdata = new ArrayList<>();
				// filter out list for match
				for (int d = 0; d < data.size(); d++) {
					if (data.get(d).get(nodes[thisPos + 1].toString()).toString().equals(thisval)) {
						thisdata.add(data.get(d));
					}
				}
				target.addContent(addDatatoNode(nodes, thisval, thisPos + 1, thisdata));
			}
		}

		return target;
	}

	private org.jdom.Document putThisElement(TreeNode[] nodes, org.jdom.Document doc) {

		// progressbar 2
		JFrame frame2 = new JFrame("Importing Metadata...");
		JProgressBar pBar = new JProgressBar();
		// init pbar
		pBar.setMinimum(0);
		pBar.setStringPainted(true);
		frame2.add(pBar);
		frame2.setSize(400, 80);
		// frame.pack();
		frame2.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		// set the frame in the middle of screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame2.setLocation(dim.width / 2 - frame2.getSize().width / 2, dim.height / 2 - frame2.getSize().height / 2);
		frame2.setResizable(false);
		frame2.setVisible(true);
		new Thread() {
			@Override
			public void run() {
				pBar.setValue(0);
			}
		}.start();

		Element root = doc.getRootElement();
		TreeNode node = nodes[0];
		// add first element right now doc has only root
		List<String> uniqExp = mogColl.getSortedUniqueValuesByHeaderName(node.toString(), false, false);
		pBar.setMaximum(uniqExp.size());
		// get all attributes for this node
		int exp_root_child_count = tree.getModel().getChildCount(node);
		String[] exp_attributes = new String[exp_root_child_count];
		for (int i = 0; i < exp_root_child_count; i++) {
			exp_attributes[i] = tree.getModel().getChild(node, i).toString();
		}

		// for each item in uniqExp, add to doc
		for (int i = 0; i < uniqExp.size(); i++) {
			// create a new Element
			// Element thisChild = new Element("Experiment");
			Element thisChild = null;
			// thisChild.setAttribute("name", uniqExp.get(i));
			// find all rows corresponding to thisChild
			Filter f = Filters.regex(node.toString(), "^" + uniqExp.get(i) + "$");
			List<Document> thisDoc = mogColl.getDatabyAttributes(f, true);
			// JOptionPane.showMessageDialog(null, "total rows" + thisDoc.size());
			thisChild = addDatatoNode(nodes, uniqExp.get(i), 0, thisDoc);
			root.addContent(thisChild);

			// update progress bar
			if (i % 10 == 0) {
				// update progress bar
				pBar.setValue(i);
				pBar.update(pBar.getGraphics());
				pBar.repaint();
			}
		}
		//remove progress bar
		frame2.dispose();

		return doc;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if ("save".equals(e.getActionCommand())) {
			treeStruct.setTree(tree);
			JOptionPane.showMessageDialog(null, "tree saved");
		}

		if ("reset".equals(e.getActionCommand())) {
			// reset tree here

			int result = JOptionPane.showConfirmDialog(null, "Are you sure ? This can't be undone");
			if (result == JOptionPane.YES_OPTION) {
				// Saving code here
				treeStruct.resetTree();
				DisplayMetadataEditor ob = new DisplayMetadataEditor();
				ob.setVisible(true);
				this.dispose();
			}

		}

		if ("import".equals(e.getActionCommand())) {
			// import data to MOG
			// JOptionPane.showMessageDialog(null, "Started to build data model2...",
			// getTitle(),JOptionPane.WARNING_MESSAGE);
			// parsedata();
			String finalfile = parsedata2();
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

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			treeStruct.setTree(tree);
		}
	}

}
