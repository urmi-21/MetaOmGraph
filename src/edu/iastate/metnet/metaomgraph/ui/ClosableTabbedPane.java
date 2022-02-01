/**
 * This is a custom tabbed pane class which has close button
 */
package edu.iastate.metnet.metaomgraph.ui;


import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

public class ClosableTabbedPane extends JTabbedPane {

	List<Integer> actionLogList = new ArrayList<>();

	public ClosableTabbedPane() {
		
		super();
		
	}
	
	public void addTab(String title, Icon icon, Component component, String tip, int parentActionNumber) {

		actionLogList.add(parentActionNumber);

		UIManager.put("TabbedPane.selected", Color.red);
		// TODO Auto-generated method stub
		super.addTab(title, icon, component, tip);
		int tabNo = this.getTabCount() - 1;
		setTabComponentAt(tabNo, new ClosablePanel(component,title,icon));
	}
	
	public void addNonClosableTab(String title, Icon icon, Component component, String tip, int parentActionNumber) {

		actionLogList.add(parentActionNumber);

		UIManager.put("TabbedPane.selected", Color.red);
		super.addTab(title, icon, component, tip);
	}

	public int getSelectedTabActionNumber() {
		return actionLogList.get(getSelectedIndex());
	}
	
	private class ClosablePanel extends JPanel{
		private Component comp;
		
		public ClosablePanel(Component comp, String title, Icon icon) {
			this.comp = comp;
			setOpaque(false);
			
			FlowLayout tabLayout = new FlowLayout(FlowLayout.CENTER,3,3);
			setLayout(tabLayout);
			
			JLabel tabLabel = new JLabel(title);
			tabLabel.setIcon(icon);
			add(tabLabel);
			JButton closeButton = new JButton("x");
			closeButton.setMargin(new Insets(0,4,0,4));
			closeButton.setOpaque(false);
			closeButton.setBorderPainted(false);
			closeButton.addMouseListener(new CloseButtonListener(comp));
			add(closeButton);
			
		}
	}
	
	private class CloseButtonListener implements MouseListener{
		
		Component tab;
		public CloseButtonListener(Component tab) {
			this.tab = tab;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
			if(e.getSource() instanceof JButton) {
				JButton exitButton = (JButton)e.getSource();
				JTabbedPane tabbedPane = (JTabbedPane)exitButton.getParent().getParent().getParent();
				tabbedPane.remove(tab);
				actionLogList.remove(getSelectedIndex());
			}
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
}
