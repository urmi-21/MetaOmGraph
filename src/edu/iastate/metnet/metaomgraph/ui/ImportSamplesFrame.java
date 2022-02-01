package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;


public class ImportSamplesFrame
extends TaskbarInternalFrame implements ActionListener{
	private JTextArea textArea;
	private DifferentialExpFrame diffExpFrame;
	private DifferentialCorrFrame diffCorrFrame;
	private JTable tableToAdd;
	private List<String> result;
	private JPanel contentPane;
	private JScrollPane scroll;
	private ImportSamplesFrame thisInternalFrame;
	private String analysisType;

	public ImportSamplesFrame(DifferentialExpFrame diffExpFrame, JTable tableToAdd) {
		
		this.analysisType = "DiffExp";

		this.diffExpFrame = diffExpFrame;
		this.tableToAdd = tableToAdd;

		setBounds(50, 50, 450, 300);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));

		textArea = new JTextArea();
		textArea.setRows(20);
		textArea.setColumns(10);

		scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		thisInternalFrame = getCurrentFrame();

		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		JLabel instructions = new JLabel(
				"Type/paste the entries you want to add, one per line.  Entered values must match a row name value exactly.");
		contentPane.add(instructions, BorderLayout.NORTH);
		contentPane.add(scroll, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);


		add(contentPane);

		this.setTitle("Import Samples by name");

		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.pack();

		setClosable(true);
		setMaximizable(false);
		setIconifiable(true);



	}
	
	
	
	public ImportSamplesFrame(DifferentialCorrFrame diffCorrFrame, JTable tableToAdd) {
		
		this.analysisType = "DiffCorr";

		this.diffCorrFrame = diffCorrFrame;
		this.tableToAdd = tableToAdd;

		setBounds(50, 50, 450, 300);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));

		textArea = new JTextArea();
		textArea.setRows(20);
		textArea.setColumns(10);

		scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		thisInternalFrame = getCurrentFrame();

		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		JLabel instructions = new JLabel(
				"Type/paste the entries you want to add, one per line.  Entered values must match a row name value exactly.");
		contentPane.add(instructions, BorderLayout.NORTH);
		contentPane.add(scroll, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);


		add(contentPane);

		this.setTitle("Import Samples by name");

		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.pack();

		setClosable(true);
		setMaximizable(false);
		setIconifiable(true);



	}


	public ImportSamplesFrame getCurrentFrame() {
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		try {
		if (e.getActionCommand().equals("cancel")) {
			result = null;
			dispose();
			MetaOmGraph.getTaskBar().removeFromTaskbar(thisInternalFrame);
			return;
		}
		if (e.getActionCommand().equals("ok")) {
			textArea.setText(textArea.getText().trim());
			if (textArea.getText().length() <= 0) {
				JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(),
						"You must enter some value!", "Error",
						0);
				return;
			}
			result = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(textArea.getText(), "\n");
			List<String> allSamples = MetaOmGraph.getActiveProject().getSampleDataListRowNames("Complete List");

			while (st.hasMoreTokens()) {

				result.add(st.nextToken());


			}

			if (!allSamples.containsAll(result)) {
				JOptionPane.showMessageDialog(
						MetaOmGraph.getMainWindow(),
						"Some of the values you entered correspond to any sample names.",
						"No matches found", 0);
			} else {

				if(this.analysisType == "DiffExp")
					diffExpFrame.addRows(tableToAdd, result);
				else if(this.analysisType == "DiffCorr")
					diffCorrFrame.addRows(tableToAdd, result);
				dispose();
				MetaOmGraph.getTaskBar().removeFromTaskbar(thisInternalFrame);
			}
			return;
		}
		
		}
		catch(Exception e1) {
			JOptionPane.showMessageDialog(
					MetaOmGraph.getMainWindow(),
					"Unable to import metadata",
					"No matches found", 0);
		}
	}
}
