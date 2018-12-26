package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.RandomAccessFile;
import edu.iastate.metnet.metaomgraph.TableSorter;
import edu.iastate.metnet.metaomgraph.utils.MetNetUtils;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ProjectPropertiesPanel extends JPanel {
	private JTextField xaxisField;
	private JTextField yaxisField;
	private JTextField titleField;
	private ColorChooseButton color1Button;
	private ColorChooseButton color2Button;
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
		JLabel color1Label = new JLabel("Background color 1: ");
		JLabel color2Label = new JLabel("Background color 2: ");
		// urmi
		JLabel paramLabel = new JLabel("Program parameters");
		color1Label.setHorizontalAlignment(11);
		color2Label.setHorizontalAlignment(11);

		color1Button = new ColorChooseButton(color1, "Background Color 1");
		color1Button.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				myProject.setColor1(color1Button.getColor());
			}

		});
		color2Button = new ColorChooseButton(color2, "Background Color 2");
		color2Button.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				myProject.setColor2(color2Button.getColor());
			}

		});
		JLabel rowNamesLabel = new JLabel("Sample Names: ");
		JLabel columnNamesLabel = new JLabel("Column Headers: ");
		xaxisLabel.setHorizontalAlignment(11);
		yaxisLabel.setHorizontalAlignment(11);
		titleLabel.setHorizontalAlignment(11);
		rowNamesLabel.setHorizontalAlignment(11);
		columnNamesLabel.setHorizontalAlignment(11);

		xaxisField = new JTextField(x);
		xaxisField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
			}

			public void keyReleased(KeyEvent arg0) {
				myProject.setDefaultXAxis(xaxisField.getText());
			}

			public void keyTyped(KeyEvent arg0) {
			}
		});
		xaxisField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				myProject.setDefaultXAxis(xaxisField.getText());
			}

		});
		yaxisField = new JTextField(y);
		yaxisField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
			}

			public void keyReleased(KeyEvent arg0) {
				myProject.setDefaultYAxis(yaxisField.getText());
			}

			public void keyTyped(KeyEvent arg0) {
			}
		});
		yaxisField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				myProject.setDefaultYAxis(yaxisField.getText());
			}

		});
		titleField = new JTextField(title);

		titleField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
			}

			public void keyReleased(KeyEvent arg0) {
				myProject.setDefaultTitle(titleField.getText());
			}

			public void keyTyped(KeyEvent arg0) {
			}
		});
		titleField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				myProject.setDefaultTitle(titleField.getText());
			}

		});
		JButton rowManageButton = new JButton("Manage...");
		rowManageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				new RowNameManager().manageRows();
			}

		});
		JButton columnManageButton = new JButton("Manage...");
		columnManageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				new ColumnManager().manageCols();
			}

		});
		JButton paramManageButton = new JButton("Change...");
		paramManageButton.addActionListener(new ActionListener() {
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
		add(color1Label, c);
		c.gridy = 4;
		add(color2Label, c);
		c.gridy = 5;
		add(rowNamesLabel, c);
		c.gridy = 6;
		add(columnNamesLabel, c);
		// urmi
		c.gridy = 7;
		add(paramLabel, c);
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
		add(color1Button, c);
		c.gridy = 4;
		add(color2Button, c);
		c.gridy = 5;
		add(rowManageButton, c);
		c.gridy = 6;
		add(columnManageButton, c);
		// urmi
		c.gridy = 7;
		add(paramManageButton, c);
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

	public Color getColor1() {
		return color1Button.getColor();
	}

	public Color getColor2() {
		return color2Button.getColor();
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
			final JInternalFrame f = new JInternalFrame("Sample Name Manager");
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
			final JInternalFrame f = new JInternalFrame("Column Header Manager");
			f.putClientProperty("JInternalFrame.frameType", "normal");
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
				public void actionPerformed(ActionEvent arg0) {
					myProject.setDataColumnHeaders(model.getData());
					f.dispose();
				}

			});
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
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

			public void keyTyped(KeyEvent e) {
			}

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

			public void keyReleased(KeyEvent e) {
			}
		}
	}
}
