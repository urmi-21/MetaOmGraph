package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.RandomAccessFile;
import edu.iastate.metnet.metaomgraph.utils.MetNetUtils;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class ProjectPropertiesPanel extends JPanel {
	private JTextField xaxisField;
	private JTextField yaxisField;
	private JTextField titleField;
	//private ColorChooseButton color1Button;
	//private ColorChooseButton color2Button;
	private MetaOmProject myProject;

	public ProjectPropertiesPanel(MetaOmProject activeProject) {
		this(activeProject, activeProject.getDefaultXAxis(), activeProject.getDefaultYAxis(),
				activeProject.getDefaultTitle(), activeProject.getColor1(), activeProject.getColor2());
	}

	private ProjectPropertiesPanel(MetaOmProject project, String x, String y, String title, Color color1,
			Color color2) {
		myProject = project;
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JLabel xaxisLabel = new JLabel("Default X-Axis Label: ");
		JLabel yaxisLabel = new JLabel("Default Y-Axis Label: ");
		JLabel titleLabel = new JLabel("Default Chart Title: ");
		JLabel changeColors = new JLabel("MOG colors: ");
		JLabel changeThemeLabel = new JLabel("GUI Themes");
		
		
		// urmi
		JLabel paramLabel = new JLabel("Project parameters");
		JLabel rParams = new JLabel("R path");
		

		
		JLabel rowNamesLabel = new JLabel("Sample Names: ");
		JLabel columnNamesLabel = new JLabel("Column Headers: ");
		xaxisLabel.setHorizontalAlignment(11);
		yaxisLabel.setHorizontalAlignment(11);
		titleLabel.setHorizontalAlignment(11);
		rowNamesLabel.setHorizontalAlignment(11);
		columnNamesLabel.setHorizontalAlignment(11);

		xaxisField = new JTextField(x);
		xaxisField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				myProject.setDefaultXAxis(xaxisField.getText());
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});
		yaxisField = new JTextField(y);
		yaxisField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				myProject.setDefaultYAxis(yaxisField.getText());
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});
		titleField = new JTextField(title);

		titleField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				myProject.setDefaultTitle(titleField.getText());
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});
		JButton rowManageButton = new JButton("Manage...");
		rowManageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				new RowNameManager().manageRows();
			}

		});
		JButton columnManageButton = new JButton("Manage...");
		columnManageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				new ColumnManager().manageCols();
			}

		});
		JButton paramManageButton = new JButton("Change...");
		paramManageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				if(MetaOmGraph.getActiveProject().getMetadataHybrid()==null) {
					JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "No Metadata found");
					return;
				}
				SetProgramParameters frame = new SetProgramParameters();
				frame.setSize(MetaOmGraph.getMainWindow().getWidth() / 2, MetaOmGraph.getMainWindow().getHeight() / 2);
				frame.pack();
				frame.setTitle("Change parameters");
				MetaOmGraph.getDesktop().add(frame);
				frame.setVisible(true);
				
				
				
			}

		});
		
		JButton rPathManageButton = new JButton("Change...");
		rPathManageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SetRPaths frame = new SetRPaths();
				frame.setSize(MetaOmGraph.getMainWindow().getWidth() / 2, MetaOmGraph.getMainWindow().getHeight() / 2);
				frame.pack();
				frame.setTitle("Change R parameters");
				MetaOmGraph.getDesktop().add(frame);
				frame.setVisible(true);
			}

		});
		
		JButton manageColors= new JButton("Manage...");
		manageColors.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ColorProperties frame = new ColorProperties();
				frame.setSize(500,300);
				//frame.pack();
				frame.setTitle("Change colors");
				MetaOmGraph.getDesktop().add(frame);
				frame.setVisible(true);
			}

		});
		
		
		//add to frame
		
		c.gridx = 0;
		c.gridy = 0;
		c.fill = 0;
		c.weightx = 0.0D;
		c.weighty = 0.5D;
		add(xaxisLabel, c);
		c.gridy = 1;
		add(yaxisLabel, c);
		c.gridy = 2;
		add(titleLabel, c);
		c.gridy = 3;
		c.gridy = 4;
		add(paramLabel, c);
		//removed rowNamesLabel and columnNamesLabel urmi
		//c.gridy = 5;
		//add(new JLabel("<html><span style='font-size:15px'>MOG</span></html>"), c);
		//c.gridy = 6;
		//add(columnNamesLabel, c);
		// urmi
		c.gridy = 6;
		add(rParams, c);
		c.gridy = 7;
		add(changeColors, c);
		c.gridy = 8;
		add(changeThemeLabel, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.fill = 2;
		c.weightx = 1.0D;
		add(xaxisField, c);
		c.gridy = 1;
		add(yaxisField, c);
		c.gridy = 2;
		add(titleField, c);
		c.gridy = 3;
		//add(color1Button, c);
		c.gridy = 4;
		add(paramManageButton, c);
		//removed manage sample and cols
		//c.gridy = 5;
		//add(new JLabel("<html><span style='font-size:15px'>properties</span></html>"), c);
		//add(rowManageButton, c);
		//c.gridy = 6;
		//add(columnManageButton, c);
		// urmi
		c.gridy = 6;
		add(rPathManageButton, c);
		c.gridy = 7;
		add(manageColors, c);
		
		String themes[] = {"System","Light", "Dark"};
		JComboBox<String> themeComboBox = new JComboBox<String>(themes);
		themeComboBox.setSelectedItem(MetaOmGraph.getActiveTheme().toString());
		themeComboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == e.SELECTED) {
					MetaOmGraph.Themes theme = MetaOmGraph.Themes.valueOf(themeComboBox.getSelectedItem().toString());
					MetaOmGraph.setTheme(theme);
				}
			}
		});
		themeComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		c.gridy = 8;
		add(themeComboBox, c);	
	}

	public String getXAxisLabel() {
		return xaxisField.getText();
	}

	public String getYAxisLabel() {
		return yaxisField.getText();
	}

	public String getTitle() {
		return titleField.getText();
	}

	

	private class RowNameManager {
		private JTable myTable;
		private NoneditableTableModel model;
		private JScrollPane scrollPane;
		private int defaultColumn;
		private boolean removeLastCorrelation;

		private RowNameManager() {
		}

		public void manageRows() {
			if (myProject.getInfoColumnCount() != 0) {
				model = new NoneditableTableModel(myProject.getRowNames(), myProject.getInfoColumnNames());
			} else
				model = new NoneditableTableModel(null, new String[] { "No row information available" });
			final TaskbarInternalFrame f = new TaskbarInternalFrame("Sample Name Manager");
			
			FrameModel sampleNameFrameModel = new FrameModel("Sample Name Manager","Sample Name Manager",23);
			f.setModel(sampleNameFrameModel);
			
			f.putClientProperty("JInternalFrame.frameType", "normal");
			removeLastCorrelation = false;
			myTable = new JTable(model);
			myTable.setColumnSelectionAllowed(true);
			myTable.setRowSelectionAllowed(false);
			scrollPane = new JScrollPane(myTable);
			defaultColumn = myProject.getDefaultColumn();

			JPanel operatePanel = new JPanel(new BorderLayout());

			JButton importButton = new JButton("Import...");
			JButton deleteButton = new JButton("Delete selected column(s)");
			JButton defaultButton = new JButton("Set selected column as default");
			importButton.addActionListener(new RowImportListener());
			deleteButton.addActionListener(new ColumnDeleteListener());
			defaultButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (myTable.getSelectedColumnCount() == 0)
						return;
					int selected = myTable.getSelectedColumn();
					String newName = model.getColumnName(defaultColumn);
					if (newName.charAt(newName.length() - 1) == '*')
						newName = newName.substring(0, newName.length() - 1);
					model.setColumnName(defaultColumn, newName);
					defaultColumn = selected;
					model.setColumnName(defaultColumn, model.getColumnName(defaultColumn) + "*");
					myTable = new JTable(model);
					myTable.setColumnSelectionAllowed(true);
					myTable.setRowSelectionAllowed(false);
					scrollPane.setViewportView(myTable);
				}

			});
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(importButton);
			buttonPanel.add(deleteButton);
			buttonPanel.add(defaultButton);
			operatePanel.add(scrollPane, "Center");
			operatePanel.add(buttonPanel, "Last");
			operatePanel.setBorder(BorderFactory.createEtchedBorder());
			JPanel confirmPanel = new JPanel();

			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					myProject.setRowNames(model.getData(), model.getHeaders());
					myProject.setDefaultColumn(defaultColumn);
					if (removeLastCorrelation) {
						myProject.removeLastCorrelation();
					}
					MetaOmGraph.getActiveTable().sizeColumnsToFit();
					MetaOmGraph.getActiveTable().getSorter().setSortingStatus(defaultColumn, 1);
					f.dispose();
				}

			});
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					f.dispose();
				}

			});
			confirmPanel.add(okButton);
			confirmPanel.add(cancelButton);
			JPanel mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(operatePanel, "Center");
			mainPanel.add(confirmPanel, "Last");
			f.getContentPane().add(mainPanel);
			f.pack();
			f.setResizable(true);
			f.setIconifiable(true);
			f.setMaximizable(true);
			f.setClosable(true);
			f.setDefaultCloseOperation(2);
			MetaOmGraph.getDesktop().add(f);
			f.setVisible(true);
		}

		private class RowImportListener implements ActionListener {
			private RowImportListener() {
			}

			@Override
			public void actionPerformed(ActionEvent arg0) {
				File source = Utils.chooseFileToOpen();
				if (source == null)
					return;
				try {
					RandomAccessFile dataIn = new RandomAccessFile(source, "r");
					String newHeader = dataIn.readLine();
					Vector<String> newNames = new Vector();
					boolean makeLocus = false;
					String thisName = Utils.clean(dataIn.readLine());
					while (thisName != null) {
						newNames.add(thisName);
						if (Utils.isGeneID(thisName))
							makeLocus = true;
						thisName = Utils.clean(dataIn.readLine());
					}
					dataIn.close();
					Object[] newNameArray = newNames.toArray();
					if (makeLocus) {
						int result = JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(),
								"It looks like you've imported some gene IDs.\nWould you like to automatically add additional gene information as well?",

								"Gene IDs detected", 0, 3);
						makeLocus = result == 0;
					}
					if (makeLocus) {
						String[][] newInfoArray = MetNetUtils.getMetNetInfo(newNameArray);
						String[] col1 = new String[newInfoArray.length];
						String[] col2 = new String[newInfoArray.length];
						String[] col3 = new String[newInfoArray.length];
						for (int x = 0; x < newInfoArray.length; x++) {
							col1[x] = newInfoArray[x][0];
							col2[x] = newInfoArray[x][1];
							col3[x] = newInfoArray[x][2];
						}
						model.appendColumn(col1, "Locus ID");
						model.appendColumn(col2, "Gene Name");
						model.appendColumn(col3, "Pathways");
					}

					model.appendColumn(newNameArray, newHeader);
					myTable = new JTable(model);
					myTable.setColumnSelectionAllowed(true);
					myTable.setRowSelectionAllowed(false);

					scrollPane.setViewportView(myTable);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private class ColumnDeleteListener implements ActionListener {
			private ColumnDeleteListener() {
			}

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int[] selected = myTable.getSelectedColumns();
				for (int x = selected.length - 1; x >= 0; x--) {
					int deleteMe = myTable.convertColumnIndexToModel(selected[x]);
					model.deleteColumn(deleteMe);
					if (deleteMe == defaultColumn) {
						defaultColumn = 0;
						model.setColumnName(0, model.getColumnName(0) + "*");
					} else if (deleteMe < defaultColumn) {
						defaultColumn += 1;
					}
					if ((deleteMe == 0) && (myProject.hasLastCorrelation())) {
						removeLastCorrelation = true;
					}
				}

				myTable = new JTable(model);
				myTable.setColumnSelectionAllowed(true);
				myTable.setRowSelectionAllowed(false);
				scrollPane.setViewportView(myTable);
			}
		}
	}

	private class ColumnManager {
		private JTable myTable;
		private NoneditableTableModel model;
		private JScrollPane scrollPane;

		private ColumnManager() {
		}

		public void manageCols() {
			Object[][] tableData = new Object[myProject.getDataColumnCount()][1];
			for (int x = 0; x < tableData.length; x++)
				tableData[x][0] = myProject.getDataColumnHeader(x);
			model = new NoneditableTableModel(tableData, new String[] { "Name" });
			final TaskbarInternalFrame f = new TaskbarInternalFrame("Column Header Manager");
			f.putClientProperty("JInternalFrame.frameType", "normal");
			
			FrameModel columnHeaderFrameModel = new FrameModel("Column Header Manager","Column Header Manager",24);
			f.setModel(columnHeaderFrameModel);
			myTable = new JTable(model);
			scrollPane = new JScrollPane(myTable);
			JPanel operatePanel = new JPanel(new BorderLayout());
			JPanel buttonPanel = new JPanel();

			JButton importButton = new JButton("Import...");
			importButton.addActionListener(new ColumnImportListener());
			buttonPanel.add(importButton);
			operatePanel.add(scrollPane, "Center");
			operatePanel.add(buttonPanel, "Last");
			operatePanel.setBorder(BorderFactory.createEtchedBorder());
			JPanel confirmPanel = new JPanel();

			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					myProject.setDataColumnHeaders(model.getData());
					f.dispose();
				}

			});
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					f.dispose();
				}

			});
			confirmPanel.add(okButton);
			confirmPanel.add(cancelButton);
			JPanel mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(operatePanel, "Center");
			mainPanel.add(confirmPanel, "Last");
			f.getContentPane().add(mainPanel);
			f.pack();
			f.setResizable(true);
			f.setIconifiable(true);
			f.setMaximizable(true);
			f.setClosable(true);
			f.setDefaultCloseOperation(2);
			MetaOmGraph.getDesktop().add(f);
			myTable.addKeyListener(new TableSearch());
			f.setVisible(true);
		}

		private class ColumnImportListener implements ActionListener {
			private ColumnImportListener() {
			}

			@Override
			public void actionPerformed(ActionEvent arg0) {
				File source = Utils.chooseFileToOpen();
				if (source == null)
					return;
				try {
					RandomAccessFile dataIn = new RandomAccessFile(source, "r");

					String[][] importedHeaders = new String[myProject.getDataColumnCount()][1];
					for (int x = 0; x < importedHeaders.length; x++) {
						String thisName = dataIn.readLine().trim();
						if (thisName == null) {
							model.setValueAt(x, x, 0);
						} else
							model.setValueAt(thisName, x, 0);
					}
					dataIn.close();
					myTable = new JTable(model);

					scrollPane.setViewportView(myTable);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private class TableSearch implements KeyListener {
			private String buffer;

			public TableSearch() {
				buffer = "";
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 27) {
					buffer = "";
				} else {
					int start = myTable.getSelectedRow();
					if (start < 0)
						start = 0;
					String tempBuffer;
					if (e.getKeyCode() != 10) {
						tempBuffer = (buffer + e.getKeyChar()).toUpperCase();
					} else {
						tempBuffer = buffer.toUpperCase();
						start++;
					}
					boolean done = false;
					for (int x = 0; (x < myTable.getRowCount()) && (!done); x++) {
						if (model.getData()[((start + x) % myTable.getRowCount())][0].toString().toUpperCase()
								.indexOf(tempBuffer) >= 0) {
							buffer = tempBuffer;
							myTable.changeSelection((start + x) % myTable.getRowCount(), 0, false, false);
							done = true;
						}
					}
					if (!done) {
						Toolkit.getDefaultToolkit().beep();
					}
				}
				e.consume();
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		}
	}
}
