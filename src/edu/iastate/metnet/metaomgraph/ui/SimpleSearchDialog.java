package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataHybrid;
import edu.iastate.metnet.metaomgraph.SearchMatchType;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import java.awt.event.ActionListener;
import java.util.List;
import java.awt.event.ActionEvent;

public class SimpleSearchDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtToSearch;
	private JComboBox comboBox;
	private JCheckBox chckbxNewCheckBox;
	private JCheckBox chckbxMatchCase;
	private List<String> res;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SimpleSearchDialog dialog = new SimpleSearchDialog();
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SimpleSearchDialog() {
		//ensure modality
		super((java.awt.Frame) null, true);
		setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.NORTH);
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.SOUTH);
			{
				JButton btnSearch = new JButton("Search");
				btnSearch.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						MetadataHybrid obj= MetaOmGraph.getActiveProject().getMetadataHybrid();
						// search for query
						String toSearch = txtToSearch.getText();
						String field = comboBox.getSelectedItem().toString();
						boolean exact = chckbxNewCheckBox.isSelected();
						SearchMatchType matchType = exact ? SearchMatchType.IS : SearchMatchType.CONTAINS;
						boolean matchCase=chckbxMatchCase.isSelected();
						if (field.equals("All Fields")) {
							//set field to data column. will use this to highlight results
							field=obj.getDataColName();
							res = obj.searchByValue(toSearch, field, matchType, true,matchCase);
						} else if (field.equals("Any Field")) {
							field=obj.getDataColName();
							res = obj.searchByValue(toSearch, field, matchType, false,matchCase);
						} else {
							res = obj.searchByValue(field, toSearch, field, matchType, true,matchCase);
						}
						dispose();
					}
				});
				panel.add(btnSearch);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JLabel lblSelectField = new JLabel("Select field");
				panel.add(lblSelectField);
			}
			{
				comboBox = new JComboBox();
				panel.add(comboBox);
			}
			{
				txtToSearch = new JTextField();
				txtToSearch.setText("To search...");
				panel.add(txtToSearch);
				txtToSearch.setColumns(10);
			}
			{
				chckbxNewCheckBox = new JCheckBox("Exact match");
				panel.add(chckbxNewCheckBox);
			}
			{
				chckbxMatchCase = new JCheckBox("Match case");
				panel.add(chckbxMatchCase);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
		}
		initDialog();
	}
	
	private void initDialog() {
		comboBox.addItem("Any Field");
		comboBox.addItem("All Fields");
	}
	public List<String> getResult(){
		return this.res;
	}
	
	public List<String> showDialog(){
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
		return this.res;
	}

}
