package edu.iastate.metnet.metaomgraph.ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.MenuElement;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

//import edu.iastate.metnet.metaomgraph.ui.ClosableTabbedPane.CloseButtonListener;

public class TaskbarPanel extends JPanel{

	private LinkedHashMap<String,List<TaskbarInternalFrame>> taskbarData;
	private JMenuBar menuBar;
	
	public TaskbarPanel() {
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
		Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
		setSize((int)rect.getMaxX(), 30);
		setLocation(0, (int)rect.getMaxY()-30);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		taskbarData = new LinkedHashMap<String,List<TaskbarInternalFrame>>();
		menuBar = new JMenuBar();
		add(menuBar);
	}
	
	public void addToTaskbar(TaskbarInternalFrame currentFrame) {
		
		if(taskbarData.get(currentFrame.getModel().getFrameType()) != null) {
			ArrayList<TaskbarInternalFrame> listOfFrames = (ArrayList<TaskbarInternalFrame>)taskbarData.get(currentFrame.getModel().getFrameType());
			listOfFrames.add(currentFrame);
		}
		else {
			ArrayList<TaskbarInternalFrame> listOfFrames = new ArrayList<TaskbarInternalFrame>();
			listOfFrames.add(currentFrame);
			taskbarData.put(currentFrame.getModel().getFrameType(), listOfFrames);
		}
		
		reloadTaskbar();
		
	}
	
	public void removeFromTaskbar(TaskbarInternalFrame currentFrame) {
		
		if(taskbarData.get(currentFrame.getModel().getFrameType()) != null) {
			ArrayList<TaskbarInternalFrame> listOfFrames = (ArrayList<TaskbarInternalFrame>)taskbarData.get(currentFrame.getModel().getFrameType());
			listOfFrames.remove(currentFrame);
			
			if(listOfFrames.size()==0) {
				taskbarData.remove(currentFrame.getModel().getFrameType());
			}
		}
		else {
			taskbarData.remove(currentFrame.getModel().getFrameType());
		}
		
		reloadTaskbar();
		
	}
	
	public void reloadTaskbar() {
		
		menuBar.removeAll();
		
		if(taskbarData != null) {
			
			for(Map.Entry<String, List<TaskbarInternalFrame>> entryset : taskbarData.entrySet()) {
				JMenu menu = new JMenu(entryset.getKey());
				List<TaskbarInternalFrame> typeFrames = entryset.getValue();
				
				if(typeFrames != null) {
					for(TaskbarInternalFrame frame : typeFrames) {
						JMenuItem menuItem = new JMenuItem(frame.getModel().getFrameName());
						JPanel tabItemPanel = new JPanel();
						tabItemPanel.setLayout(new BorderLayout());
						JLabel tabItemLabel = new JLabel(frame.getModel().getFrameName());
						
						JButton closeButton = new JButton("<html><b>&times;</b></html>");
						closeButton.setMargin(new Insets(0,4,0,4));
						
						closeButton.setOpaque(true);
						closeButton.setBackground(Color.ORANGE);
						//closeButton.setBorderPainted(false);
					
						closeButton.addMouseListener(new MouseListener() {
							
							@Override
							public void mouseClicked(MouseEvent e) {
								// TODO Auto-generated method stub
								try {
									frame.setClosed(true);
									if(menu!=null) {
									
									javax.swing.MenuSelectionManager.defaultManager().clearSelectedPath();
									
									
									}
								} catch (PropertyVetoException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							@Override
							public void mouseEntered(MouseEvent e) {}
							@Override
							public void mouseExited(MouseEvent e) {}

							@Override
							public void mousePressed(MouseEvent e) {}

							@Override
							public void mouseReleased(MouseEvent e) {}
						});
						
						tabItemPanel.add(tabItemLabel);
						
						if(frame.isClosable()) {
						tabItemPanel.add(closeButton,BorderLayout.EAST);
						}
						UIManager.put("MenuItem.selectionForeground", Color.BLUE);
						menuItem.add(tabItemPanel);
						menu.add(new JSeparator());
						menu.add(menuItem);
						menu.setMargin(new Insets(2, 8, 2, 8));
						
						menuItem.addMouseListener(new MouseListener() {
							
							@Override
							public void mouseReleased(MouseEvent e) {
								// TODO Auto-generated method stub
								tabItemLabel.setForeground(Color.BLACK);
								
							}
							
							@Override
							public void mousePressed(MouseEvent e) {
								tabItemLabel.setForeground(Color.BLACK);
								
							}
							
							@Override
							public void mouseExited(MouseEvent e) {
								tabItemLabel.setForeground(Color.BLACK);
							}
							
							@Override
							public void mouseEntered(MouseEvent e) {
								tabItemLabel.setForeground(Color.BLUE);
							}
							
							@Override
							public void mouseClicked(MouseEvent e) {
								tabItemLabel.setForeground(Color.BLACK);
								
							}
						});
						
						menuItem.addActionListener(new java.awt.event.ActionListener() {
						    public void actionPerformed(java.awt.event.ActionEvent evt) {
						    	try {
									frame.setSelected(true);
								} catch (PropertyVetoException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
						    	frame.getDesktopPane().getDesktopManager().deiconifyFrame(frame);
						    	frame.getDesktopPane().getDesktopManager().maximizeFrame(frame);
						    	frame.getDesktopPane().getDesktopManager().minimizeFrame(frame);
						    	frame.moveToFront();
						    }
						});
					}
				}
				
				JSeparator menuSeparator = new JSeparator();
				menuBar.add(menuSeparator);
				menuBar.add(menu);
				
			}
		}
	
	}
	
	
	public void printDialog(String dialog) {
		JDialog jd = new JDialog();
		JTextPane jt = new JTextPane();
		jt.setText(dialog);
		jt.setBounds(10, 10, 300, 100);
		jd.getContentPane().add(jt);
		jd.setBounds(100, 100, 500, 200);
		jd.setVisible(true);
	}
}