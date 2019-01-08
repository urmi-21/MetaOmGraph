package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;

import javax.swing.JInternalFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridLayout;
import net.miginfocom.swing.MigLayout;
import javax.swing.JTextField;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

public class SetRPaths extends JInternalFrame {
	private JTextField textField_1;
	private JTextField textField_2;
	private JButton browseRPath;
	private JButton browseRScriptsPath;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SetProgramParameters frame = new SetProgramParameters();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SetRPaths() {
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MetaOmGraph.setpathtoRscrips(textField_2.getText().trim());
				MetaOmGraph.setUserRPath(textField_1.getText().trim());
				dispose();

			}
		});
		JPanel buttonPanel=new JPanel();
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		JButton btnDefault = new JButton("Default");
		btnDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MetaOmGraph.useDefaultRPath();
				textField_1.setText(MetaOmGraph.getRPath());
				textField_2.setText(MetaOmGraph.getpathtoRscrips());
			}
		});
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		buttonPanel.add(btnCancel);
		buttonPanel.add(btnDefault);
		buttonPanel.add(btnOk);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		JLabel lblSetParameters = new JLabel("Set R Paths");
		panel.add(lblSetParameters);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("", "[][][][grow]", "[][][][][][][]"));
		
		JLabel lblPathToRscript = new JLabel("Path to \"Rscript.exe\"");
		panel_1.add(lblPathToRscript, "cell 1 0");
		
		textField_1 = new JTextField();
		panel_1.add(textField_1, "cell 3 0,growx");
		textField_1.setColumns(20);
		textField_1.setText(MetaOmGraph.getRPath());
		browseRPath=new JButton("Browse...");
		browseRPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(SetRPaths.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            //JOptionPane.showMessageDialog(null,"fname"+file.getPath());
		            MetaOmGraph.setUserRPath(file.getPath());
		            textField_1.setText(MetaOmGraph.getRPath());
		        } else {
		            return;
		        }
			}
		});
		panel_1.add(browseRPath, "cell 4 0,growx");
		
		
		JLabel lblPathToFolder = new JLabel("Path to folder containing the R scripts");
		panel_1.add(lblPathToFolder, "cell 1 1");
		
		browseRScriptsPath=new JButton("Browse...");
		browseRScriptsPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showOpenDialog(SetRPaths.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            //This is where a real application would open the file.
		            //JOptionPane.showMessageDialog(null,"fname"+file.getPath());
		            MetaOmGraph.setpathtoRscrips(file.getPath());
		            textField_2.setText(MetaOmGraph.getpathtoRscrips());
		        } else {
		            return;
		        }
			
			}
		});
		
		textField_2 = new JTextField();
		panel_1.add(textField_2, "cell 3 1,growx");
		textField_2.setColumns(20);
		textField_2.setText(MetaOmGraph.getpathtoRscrips());
		panel_1.add(browseRScriptsPath, "cell 4 1,growx");
		
		
		
		

		// frame properties
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);

	}

}
