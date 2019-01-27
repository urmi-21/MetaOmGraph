package edu.iastate.metnet.metaomgraph.ui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.math3.analysis.function.Atan;
import org.apache.commons.math3.analysis.function.Atanh;
import org.apache.commons.math3.distribution.NormalDistribution;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import net.iharder.dnd.TransferableObject.Fetcher;

public class DiffCorrResultsTable extends JInternalFrame {
	private JTable table;
	private List<String> featureNames;
	private List<Double> corrVals1;
	private List<Double> corrVals2;
	private List<Double> zVals1;
	private List<Double> zVals2;
	private List<Double> diff;
	private List<Double> zScores;
	private List<Double> pVals;

	private int n1;
	private int n2;

	/**
	 * Default Properties
	 */

	private Color SELECTIONBCKGRND = MetaOmGraph.getTableSelectionColor();
	private Color BCKGRNDCOLOR1 = MetaOmGraph.getTableColor1();
	private Color BCKGRNDCOLOR2 = MetaOmGraph.getTableColor2();
	private Color HIGHLIGHTCOLOR = MetaOmGraph.getTableHighlightColor();
	private Color HYPERLINKCOLOR = MetaOmGraph.getTableHyperlinkColor();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DiffCorrResultsTable frame = new DiffCorrResultsTable();
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
	public DiffCorrResultsTable() {

	}

	/*
	 * public DiffCorrResultsTable(List<String> featureNames,List<Double>
	 * corrVals1,List<Double> corrVals2) {
	 * 
	 * this.featureNames=featureNames; this.corrVals1=corrVals1;
	 * this.corrVals2=corrVals2; zVals1=converttoZ(corrVals1);
	 * zVals2=converttoZ(corrVals2); diff=getDiff(zVals1,zVals2);
	 * zScores=getZscores(diff); pVals=getPVals(zScores);
	 * 
	 * 
	 * }
	 * 
	 * public DiffCorrResultsTable(List<String> featureNames,List<Double> corrVals1,
	 * List<Double> corrVals2, List<Double> zVals1, List<Double> zVals2,
	 * List<Double> diff, List<Double> zScores, List<Double> pVals) {
	 */

	public DiffCorrResultsTable(List<String> featureNames, int n1, int n2, List<Double> corrVals1,
			List<Double> corrVals2) {
		this.featureNames = featureNames;
		this.n1 = n1;
		this.n2 = n2;
		this.corrVals1 = corrVals1;
		this.corrVals2 = corrVals2;
		zVals1 = converttoZ(this.corrVals1);
		zVals2 = converttoZ(this.corrVals2);
		diff = getDiff(zVals1, zVals2);
		zScores = getZscores(diff);
		pVals = getPVals(zScores);

		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.NORTH);

		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);

		initTableModel();
		updateTable();
		scrollPane.setViewportView(table);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// frame properties
		this.setClosable(true);
		// pack();
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);

	}

	private void initTableModel() {
		table = new JTable() {
			public boolean getScrollableTracksViewportWidth() {
				return getPreferredSize().width < getParent().getWidth();
			}

			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);

				if (!isRowSelected(row)) {
					c.setBackground(getBackground());
					int modelRow = convertRowIndexToModel(row);

					if (row % 2 == 0) {
						c.setBackground(BCKGRNDCOLOR1);
					} else {
						c.setBackground(BCKGRNDCOLOR2);
					}

				} else {
					c.setBackground(SELECTIONBCKGRND);
				}

				return c;
			}

		};

		// table mouse listener
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// only do if double click
				if (e.getClickCount() < 2) {
					return;
				}
				int row = table.convertRowIndexToModel(table.rowAtPoint(new Point(e.getX(), e.getY())));
				int col = table.convertColumnIndexToModel(table.columnAtPoint(new Point(e.getX(), e.getY())));

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				int col = table.columnAtPoint(new Point(e.getX(), e.getY()));

			}

			@Override
			public void mouseExited(MouseEvent e) {
				int col = table.columnAtPoint(new Point(e.getX(), e.getY()));

			}
		});
		// end mouse listner

		// disable colum drag
		table.getTableHeader().setReorderingAllowed(false);

		DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public Class<?> getColumnClass(int column) {
				switch (column) {
				case 0:
					return String.class;
				default:
					return Double.class;
				}
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};
		table.setModel(model);
	}

	private void updateTable() {

		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		tablemodel.setRowCount(0);
		tablemodel.setColumnCount(0);
		// add data
		tablemodel.addColumn("Name");
		tablemodel.addColumn("r1");
		tablemodel.addColumn("r2");
		tablemodel.addColumn("z1");
		tablemodel.addColumn("z2");
		tablemodel.addColumn("z1-z2");
		tablemodel.addColumn("zScore");
		tablemodel.addColumn("pVal");

		// for each row add each coloumn
		for (int i = 0; i < featureNames.size(); i++) {
			// create a temp string storing all col values for a row
			Vector temp = new Vector<>();
			temp.add(featureNames.get(i));
			temp.add(corrVals1.get(i));
			temp.add(corrVals2.get(i));
			temp.add(zVals1.get(i));
			temp.add(zVals2.get(i));
			temp.add(diff.get(i));
			temp.add(zScores.get(i));
			temp.add(pVals.get(i));

			// add ith row in table
			tablemodel.addRow(temp);

		}

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoCreateRowSorter(true);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 14));

	}

	/**
	 * convert r values to z applying Fisher's transform
	 * 
	 * @param rVals
	 * @return
	 */
	private List<Double> converttoZ(List<Double> rVals) {
		List<Double> res = new ArrayList<>();
		Atanh atan = new Atanh();
		for (double d : rVals) {
			res.add(atan.value(d));
			//JOptionPane.showMessageDialog(null, "val:"+d+" atan:"+atan.value(d));
		}
		return res;
	}

	private List<Double> getDiff(List<Double> rVals1, List<Double> rVals2) {
		List<Double> res = new ArrayList<>();
		for (int i = 0; i < rVals1.size(); i++) {
			res.add(rVals1.get(i) - rVals2.get(i));
		}
		return res;
	}

	private List<Double> getZscores(List<Double> diff) {
		List<Double> res = new ArrayList<>();
		for (int i = 0; i < diff.size(); i++) {
			double thisZ = diff.get(i);
			double denom = Math.sqrt((1 / ((double) n1 - 3)) + (1 / ((double) n2 - 3)));
			//JOptionPane.showMessageDialog(null, "denom:" + denom);
			thisZ = thisZ / denom;
			res.add(thisZ);
		}
		return res;
	}

	private List<Double> getPVals(List<Double> zScores) {
		List<Double> res = new ArrayList<>();
		NormalDistribution nob = new NormalDistribution();

		for (int i = 0; i < zScores.size(); i++) {
			double thisZ = zScores.get(i);
			if (thisZ > 0) {
				thisZ = thisZ * -1;
			}
			res.add(nob.cumulativeProbability(thisZ)*2);
		}
		return res;
	}

}
