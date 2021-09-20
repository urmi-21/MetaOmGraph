/**
 * 
 */
package edu.iastate.metnet.metaomgraph.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.ui.ColorChooseButton;

/**
 * @author sumanth
 * HeatMapRangeColorSelector class displays the panel to the user 
 * User can provide the ranges and its related color to reflect back in the heatmap
 */
public class HeatMapRangeColorSelector extends JPanel implements ActionListener{
	
	private JPanel rangePanel;
	private JPanel rangeViewport;
	private JScrollPane mainScrollPane;
	private JDialog rangeDialog;	
	private JButton addRangeButton;
	
	private ArrayList<SingleRangePanel> rangePanelsCollection;
	
	/**
	 * Constructor
	 * @param rangeColorMap map that hold the ranges and its color
	 * Key: Double[] is an array which should only have 2 values, 
	 * Start of the range at 0 index and end of the range at index 1
	 */
	public HeatMapRangeColorSelector(TreeMap<Double[], Color> rangeColorMap) {
		setLayout(new BorderLayout());
		JPanel addRangeButtonPanel = new JPanel();
		// Add button to add a range
		addRangeButton = new JButton(MetaOmGraph.getIconTheme().getListAdd());
		addRangeButton.setToolTipText("Click to add a new range");
		addRangeButton.setActionCommand("Add");
		addRangeButton.addActionListener(this);
		addRangeButtonPanel.add(addRangeButton);
		
		rangePanel = new JPanel();
		rangePanel.setLayout(new BoxLayout(rangePanel, 1));
		
		rangeViewport = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		JPanel minMaxLabelsPanel = new JPanel();
		minMaxLabelsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 23, 0));
		// Delete a range
		JLabel removeRangeLabel = new JLabel("Delete");
		JLabel minValueLabel = new JLabel("Min");
		JLabel maxValueLabel = new JLabel("Max");
		JLabel colorLabel = new JLabel("Color");
		
		minMaxLabelsPanel.add(removeRangeLabel);
		minMaxLabelsPanel.add(new JSeparator(SwingConstants.VERTICAL));
		minMaxLabelsPanel.add(minValueLabel);
		minMaxLabelsPanel.add(new JSeparator(SwingConstants.VERTICAL));
		minMaxLabelsPanel.add(maxValueLabel);
		minMaxLabelsPanel.add(new JSeparator(SwingConstants.VERTICAL));
		minMaxLabelsPanel.add(colorLabel);
		rangeViewport.add(minMaxLabelsPanel, c);
		c.gridx = 0;
		c.gridy = 1;
		rangeViewport.add(rangePanel, c);
		c.gridy = 2;
		c.weighty = 1.0D;
		rangeViewport.add(new JPanel(), c);
		addRangePanels(rangeColorMap);
		mainScrollPane = new JScrollPane(rangeViewport);
		
		add(addRangeButtonPanel, "First");
		add(mainScrollPane, "Center");
		showRangeDialog();
	}
	
	// Displays the range dialog to the user
	private void showRangeDialog() {
		rangeDialog = new JDialog(MetaOmGraph.getMainWindow(), "Set ranges", true);
		rangeDialog.getContentPane().setLayout(new BorderLayout());
		rangeDialog.getContentPane().add(this, "Center");
		JPanel buttonPanel = new JPanel();

		JButton okButton = new JButton("Ok");
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rangeDialog.dispose();
			}
		});
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		rangeDialog.getContentPane().add(buttonPanel, "Last");
		rangeDialog.setSize(640, 480);
		rangeDialog.pack();
		int width = MetaOmGraph.getMainWindow().getWidth();
		int height = MetaOmGraph.getMainWindow().getHeight();
		rangeDialog.setLocation((width - rangeDialog.getWidth()) / 2,
				(height - rangeDialog.getHeight()) / 2);
		
		rangeDialog.setVisible(true);		
	}
	
	// Create each range from map as a panel and add it to the existing panel
	private void addRangePanels(TreeMap<Double[], Color> rangeColorMap) {
		rangePanelsCollection = new ArrayList<SingleRangePanel>();
		for(Map.Entry<Double[], Color> entry : rangeColorMap.entrySet()) {
			SingleRangePanel singleRangePanel = 
					new SingleRangePanel(entry.getKey()[0], 
							entry.getKey()[1], entry.getValue());
			rangePanelsCollection.add(singleRangePanel);
			rangePanel.add(singleRangePanel);
		}
	}
	
	/**
	 * 
	 * @author sumanth
	 * SingleRangePanel class creates a panel for each range with the text fields, color and remove buttons
	 *
	 */
	private class SingleRangePanel extends JPanel implements ActionListener{
		private JButton removeButton;
		private JTextField minValueTextField;
		private JTextField maxValueTextField;
		private ColorChooseButton colorChoserButton;
		
		/**
		 * Constructor
		 * @param minValue minimum value of the range
		 * @param maxValue maximum value of the range
		 * @param color Color for that range
		 */
		public SingleRangePanel(double minValue, double maxValue, Color color) {
			removeButton = new JButton(MetaOmGraph.getIconTheme().getListDelete());
			removeButton.addActionListener(this);
			removeButton.setActionCommand("remove");
			removeButton.setToolTipText("Click to delete this range line");
			removeButton.setPreferredSize(new Dimension(2, 2));
			minValueTextField = new JTextField(Double.toString(minValue));
			minValueTextField.setToolTipText("Add min value in this range");
			maxValueTextField = new JTextField(Double.toString(maxValue));
			maxValueTextField.setToolTipText("Add max value in this range");
			colorChoserButton = new ColorChooseButton(color, "");
			colorChoserButton.setActionCommand("choose color");
			colorChoserButton.setToolTipText("Click to chose the color for this range");
			setLayout(new GridLayout(1, 4));
			add(removeButton);
			add(minValueTextField);
			add(maxValueTextField);
			add(colorChoserButton);
		}
		
		/**
		 * 
		 * @return minimum value set in this panel
		 */
		public Double getMinValue() {
			double minValue = Double.parseDouble(minValueTextField.getText());
			return minValue;
		}
		
		/**
		 * 
		 * @return maximum value set in this panel
		 */
		public Double getMaxValue() {
			double maxValue = Double.parseDouble(maxValueTextField.getText());
			return maxValue;
		}
		
		/**
		 * 
		 * @return color set for that range in the panel
		 */
		public Color getRangeColor() {
			return colorChoserButton.getColor();
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// If the panel is removed, remove it from the collections as well
			if("remove".equals(e.getActionCommand())) {
				rangePanel.remove(this);
				rangePanelsCollection.remove(this);
				mainScrollPane.setViewportView(rangeViewport);
			}
		}
	}
	
	/**
	 * 
	 * @return rangeColorMap with key as ranges and the value as color in key(range) sorted order
	 */
	public TreeMap<Double[], Color> getRangeColorMap(){
		// Custom sorter for TreeMap class key since Double[] is the key and tree map sorts the
		// key in order, we provide the TreeMap a custom method to sort by ranges
		TreeMap<Double[], Color> rangeColorMap = new TreeMap<>((k1, k2) -> {
	        for (int i = 0; i < k1.length; i++) {
	            if (k1[i] > k2[i]) {
	                return 1;
	            } else if (k1[i] < k2[i]) {
	                return -1;
	            }
	        }
	        return 0;
	    });
		// From all the rangepanels, fill the rangeColorMap which will be returned to the caller
		for(SingleRangePanel range : rangePanelsCollection) {
			rangeColorMap.put(new Double[] {range.getMinValue(), range.getMaxValue()},
					range.getRangeColor());
		}
		return rangeColorMap;
	}
	
	// Check if the set range is valid or not
	// Checkf for overlapping ranges, duplicate ranges
	private boolean areValidRanges() {
		if(rangePanelsCollection.size() <= 0) {
			JOptionPane.showMessageDialog(this, 
					"You should have atleast one range", 
					"Range error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		// Sort the rangePanelsCollection by minvalue
		rangePanelsCollection.sort(
				(r1, r2)->r1.getMinValue().compareTo(r2.getMinValue()));
		double prevEnd = 0.0;
		// For each range, check if it overlaps with the previous range
		for(SingleRangePanel range : rangePanelsCollection) {
			if(range.getMaxValue() - range.getMinValue() <= 0) {
				JOptionPane.showMessageDialog(this, 
						"One of the ranges have same max and min values", 
						"Range error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if(prevEnd > range.getMinValue() && prevEnd <= range.getMaxValue()) {
				JOptionPane.showMessageDialog(this, 
						"Ranges are overlapping, please correct them", 
						"Range error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			prevEnd = range.getMaxValue();
		}
		return true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Add a new range panel
		if("Add".equals(e.getActionCommand())) {
			SingleRangePanel singleRangePanel = 
					new SingleRangePanel(0.0, 0.0, Color.WHITE);
			rangePanelsCollection.add(singleRangePanel);
			rangePanel.add(singleRangePanel);
			mainScrollPane.setViewportView(rangeViewport);
		}
		if("OK".equals(e.getActionCommand())) {
			if(areValidRanges()) {
				rangeDialog.dispose();
			}
		}
	}

}
