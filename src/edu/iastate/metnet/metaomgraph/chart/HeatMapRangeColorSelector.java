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
 *
 */
public class HeatMapRangeColorSelector extends JPanel implements ActionListener{
	
	private JPanel rangePanel;
	private JPanel rangeViewport;
	private JScrollPane mainScrollPane;
	private JDialog rangeDialog;
	
	private JButton addRangeButton;
	private ArrayList<SingleRangePanel> rangePanelsCollection;
	
	public HeatMapRangeColorSelector(TreeMap<Double[], Color> rangeColorMap) {
		setLayout(new BorderLayout());
		JPanel addRangeButtonPanel = new JPanel();
		addRangeButton = new JButton(MetaOmGraph.getIconTheme().getListAdd());
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
	
	private void showRangeDialog() {
		rangeDialog = new JDialog(MetaOmGraph.getMainWindow(), "Set ranges", true);
		rangeDialog.getContentPane().setLayout(new BorderLayout());
		rangeDialog.getContentPane().add(this, "Center");
		JPanel buttonPanel = new JPanel();

		JButton okButton = new JButton("OK");
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
	
	private class SingleRangePanel extends JPanel implements ActionListener{
		private JButton removeButton;
		private JTextField minValueTextField;
		private JTextField maxValueTextField;
		private ColorChooseButton colorChoserButton;
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
			//colorChoserButton.setBackground(color);
			colorChoserButton.setActionCommand("choose color");
			colorChoserButton.setToolTipText("Click to chose the color for this range");
			setLayout(new GridLayout(1, 4));
			add(removeButton);
			add(minValueTextField);
			add(maxValueTextField);
			add(colorChoserButton);
		}
		
		public Double getMinValue() {
			double minValue = Double.parseDouble(minValueTextField.getText());
			return minValue;
		}
		
		public Double getMaxValue() {
			double maxValue = Double.parseDouble(maxValueTextField.getText());
			return maxValue;
		}
		
		public Color getRangeColor() {
			return colorChoserButton.getColor();
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if("remove".equals(e.getActionCommand())) {
				rangePanel.remove(this);
				rangePanelsCollection.remove(this);
				mainScrollPane.setViewportView(rangeViewport);
			}
		}
	}
	
	public TreeMap<Double[], Color> getRangeColorMap(){
		TreeMap<Double[], Color> rangeColorMap = new TreeMap<>((o1, o2) -> {
	        for (int i = 0; i < o1.length; i++) {
	            if (o1[i] > o2[i]) {
	                return 1;
	            } else if (o1[i] < o2[i]) {
	                return -1;
	            }
	        }
	        return 0;
	    });
		for(SingleRangePanel range : rangePanelsCollection) {
			rangeColorMap.put(new Double[] {range.getMinValue(), range.getMaxValue()},
					range.getRangeColor());
			//rangeColorMap.put(range.getMaxValue()-tolerance, range.getRangeColor());
		}
		return rangeColorMap;
	}
	
	private boolean areValidRanges() {
		rangePanelsCollection.sort(
				(r1, r2)->r1.getMinValue().compareTo(r2.getMinValue()));
		double prevEnd = 0.0;
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
