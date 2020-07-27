package edu.iastate.metnet.metaomgraph.ui;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class TaskbarPanel extends JPanel{

	private TreeMap<String,List<TaskbarInternalFrame>> taskbarData;
	private JMenuBar menuBar;
	
	public TaskbarPanel() {
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
		Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
		setSize((int)rect.getMaxX(), 30);
		setLocation(0, (int)rect.getMaxY()-30);
		
		taskbarData = new TreeMap<String,List<TaskbarInternalFrame>>();
		menuBar = new JMenuBar();
		add(menuBar);
	}
	
	public void addToTaskbar(TaskbarInternalFrame currentFrame) {
		
		if(taskbarData.get(currentFrame.getModel().getType()) != null) {
			ArrayList<TaskbarInternalFrame> listOfFrames = (ArrayList<TaskbarInternalFrame>)taskbarData.get(currentFrame.getModel().getType());
			listOfFrames.add(currentFrame);
		}
		else {
			ArrayList<TaskbarInternalFrame> listOfFrames = new ArrayList<TaskbarInternalFrame>();
			listOfFrames.add(currentFrame);
			taskbarData.put(currentFrame.getModel().getType(), listOfFrames);
		}
		
		reloadTaskbar();
		
	}
	
	public void removeFromTaskbar(TaskbarInternalFrame currentFrame) {
		
		if(taskbarData.get(currentFrame.getModel().getType()) != null) {
			ArrayList<TaskbarInternalFrame> listOfFrames = (ArrayList<TaskbarInternalFrame>)taskbarData.get(currentFrame.getModel().getType());
			listOfFrames.remove(currentFrame);
			
			if(listOfFrames.size()==0) {
				taskbarData.remove(currentFrame.getModel().getType());
			}
		}
		else {
			taskbarData.remove(currentFrame.getModel().getType());
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
						JMenuItem menuItem = new JMenuItem(frame.getTitle());
						menu.add(menuItem);
					}
				}
				
				menuBar.add(menu);
				
			}
		}
		
	}
}


class VerticalMenuBar extends JMenuBar {
	  private static final LayoutManager grid = new GridLayout(0,1);
	  public VerticalMenuBar() {
	    setLayout(grid);
	  }
	}