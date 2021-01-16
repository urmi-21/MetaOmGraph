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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
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

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Harsha
 * 
 * This is the UI class for the taskbar panel present at the bottom of MOG.
 * The taskbar looks similar to "Windows OS" taskbar, with windows of the 
 * same type stacked into groups. It uses a JPanel which is fixed at the 
 * bottom of the JDesktopPane. It hasa JMenuBar to store the tabs of the 
 * currently opened TaskbarInternalFrame objects. The JMenu objects act as
 * the tabs which are visible on the bottom panel (holding the "types") and
 * JMenuItems are the actual tabs relating to an Internal Frame.
 * 
 * The master data of all the currently opened frames is stored in the object
 * LinkedHashMap<String,List<TaskbarInternalFrame>> taskbarData, the key
 * being the "type" of the Internal Frame (LineChart, DEA etc) and the value
 * being the list of TaskbarInternalFrame objects in each "type". For details
 * on how to create a TaskbarInternalFrame, please check the documentation of
 * that class.
 * 
 * Below are the utility methods that are defined in this class:
 * 
 * 1. addToTaskbar(TaskbarInternalFrame currentFrame) : This method takes a
 * TaskbarInternalFrame object as a parameter and adds it to the respective
 * "type" tab given in its model ( currentFrame.getModel().getFrameType() )
 * 
 * 2. removeFromTaskbar(TaskbarInternalFrame currentFrame) : This method 
 * removes a given TaskbarInternalFrame object from the taskbar "type" tab. 
 * If this object is the last one in the "type", then the "type" tab is 
 * removed as well.
 *
 * 3. removeAllTabsFromTaskbar() : Removes all the tabs from the taskbar and
 * clears the master data object.
 * 
 * 4. reloadTaskbar() :  This method reloads the taskbar ( removes all tabs
 * and adds them again ). It is called from the addToTaskbar and 
 * removeFromTaskbar methods (i.e, whenever the master data gets changed)
 * 
 */

public class TaskbarPanel extends JPanel{

	private LinkedHashMap<String,List<TaskbarInternalFrame>> taskbarData;
	private JMenuBar menuBar;

	/**
	 * Constructor to initialize the masterdata (taskbarData) and the JPanel
	 */
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

	/**
	 * This method adds a tab to the taskbar
	 * 
	 * If the given TaskbarInternalFrame object has a model object with FrameType
	 * initialized, then the object is added to the list of objects present in that
	 * type.
	 * 
	 * Else if the TaskbarInternalFrame object does not have the FrameType initialized
	 * in its model, then the object is directly added to the taskbar with its name.
	 * 
	 * At the end, the TaskbarPanel is reloaded to reflect the new addition
	 * 
	 */
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



	/**
	 * This method removes the given frame from the taskbar.
	 * 
	 * If the TaskbarInternalFrame object has the Frame Type property initialized, then
	 * we remove the current frame from the list of frames of that frame type.
	 * 
	 * Else if TaskbarInternalFrame object doesn't have the Frame Type property, then
	 * we remove the cureent frame from the master data ( as it is added directly to the
	 * panel. )
	 * 
	 * At the end, the taskbar is reloaded to reflect the removal.
	 */
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


	/**
	 * Clears the tab data from the taskbarData master data, and removes all the
	 * JMenu objects from the menubar.
	 */
	public void removeAllTabsFromTaskbar() {

		taskbarData.clear();
		menuBar.removeAll();
	}


	/**
	 * This method first removes all the JMenus from the menuBar object.
	 * 
	 * Then, it iterates over the taskbarData map, and creates the JMenus (tabs) and 
	 * the JMenuItems (individual tabs that popup when the tabs are pressed) according
	 * to the type and frame hierarchy.
	 * 
	 * An actionlistener is added to each of the JMenuItems. When any JMenuItem
	 * is pressed, it is maximized (if minimized) and moved to front.
	 *
	 */
	public void reloadTaskbar() {

		menuBar.removeAll();

		if(taskbarData != null) {

			for(Map.Entry<String, List<TaskbarInternalFrame>> entryset : taskbarData.entrySet()) {
				JMenu menu = new JMenu(entryset.getKey());
				List<TaskbarInternalFrame> typeFrames = entryset.getValue();

				if(typeFrames != null) {
					for(TaskbarInternalFrame frame : typeFrames) {
						String menuItemName = frame.getModel().getFrameName();
						menuItemName = StringUtils.abbreviate(menuItemName, 80);

						JMenuItem menuItem = new JMenuItem(menuItemName);
						JPanel tabItemPanel = new JPanel();
						tabItemPanel.setLayout(new BorderLayout());
						JLabel tabItemLabel = new JLabel(menuItemName);

						tabItemPanel.add(tabItemLabel);

						UIManager.put("MenuItem.selectionForeground", Color.BLUE);
						menuItem.setText(menuItemName);;
						menu.add(new JSeparator());
						menu.add(menuItem);
						menu.setMargin(new Insets(2, 8, 2, 8));

						menuItem.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(java.awt.event.ActionEvent evt) {

								try {
									if(frame != null) {
										frame.getDesktopPane().getDesktopManager().deiconifyFrame(frame);
										frame.getDesktopPane().getDesktopManager().maximizeFrame(frame);
										frame.getDesktopPane().getDesktopManager().minimizeFrame(frame);
										frame.getDesktopPane().getDesktopManager().deiconifyFrame(frame);
										
										frame.moveToFront();
									}
								}
								catch(Exception e) {

								}
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

}