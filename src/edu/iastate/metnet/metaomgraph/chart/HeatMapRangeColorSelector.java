/**
 * 
 */
package edu.iastate.metnet.metaomgraph.chart;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;

/**
 * @author sumanth
 *
 */
public class HeatMapRangeColorSelector extends JPanel{
	private JButton addRangeButton;
	private LinkedHashMap<Double, Color> rangeColorMap;
	
	public HeatMapRangeColorSelector(LinkedHashMap<Double, Color> rangeColorMap) {
		this.rangeColorMap = rangeColorMap;
		for()
	}
	

	private class SingleRangePanel extends JPanel implements ActionListener{
		private JButton removeButton;
		private JTextField minValueTextField;
		private JTextField maxValueTextField;
		private JButton colorChoserButton;
		public SingleRangePanel(double minValue, double maxValue, Color color) {
			removeButton = new JButton(MetaOmGraph.getIconTheme().getListDelete());
			removeButton.addActionListener(this);
			removeButton.setActionCommand("remove");
			minValueTextField = new JTextField(Double.toString(minValue));
			maxValueTextField = new JTextField(Double.toString(maxValue));
			colorChoserButton = new JButton();
			colorChoserButton.setBackground(color);
		}
		
		public double getMinValue() {
			double minValue = Double.parseDouble(minValueTextField.getText());
			return minValue;
		}
		
		public double getMaxValue() {
			double maxValue = Double.parseDouble(maxValueTextField.getText());
			return maxValue;
		}
		
		public Color getRangeColor() {
			return colorChoserButton.getBackground();
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if("remove".equals(e.getActionCommand())) {
				this.removeAll();
			}
		}
	}

}
