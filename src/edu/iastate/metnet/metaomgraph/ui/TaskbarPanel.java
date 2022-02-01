package edu.iastate.metnet.metaomgraph.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.sun.management.OperatingSystemMXBean;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jfree.ui.Align;

/**
 * @author Harsha
 * <p>
 * This is the UI class for the taskbar panel present at the bottom of MOG.
 * The taskbar looks similar to "Windows OS" taskbar, with windows of the
 * same type stacked into groups. It uses a JPanel which is fixed at the
 * bottom of the JDesktopPane. It hasa JMenuBar to store the tabs of the
 * currently opened TaskbarInternalFrame objects. The JMenu objects act as
 * the tabs which are visible on the bottom panel (holding the "types") and
 * JMenuItems are the actual tabs relating to an Internal Frame.
 * <p>
 * The master data of all the currently opened frames is stored in the object
 * LinkedHashMap<String,List<TaskbarInternalFrame>> taskbarData, the key
 * being the "type" of the Internal Frame (LineChart, DEA etc) and the value
 * being the list of TaskbarInternalFrame objects in each "type". For details
 * on how to create a TaskbarInternalFrame, please check the documentation of
 * that class.
 * <p>
 * Below are the utility methods that are defined in this class:
 * <p>
 * 1. addToTaskbar(TaskbarInternalFrame currentFrame) : This method takes a
 * TaskbarInternalFrame object as a parameter and adds it to the respective
 * "type" tab given in its model ( currentFrame.getModel().getFrameType() )
 * <p>
 * 2. removeFromTaskbar(TaskbarInternalFrame currentFrame) : This method
 * removes a given TaskbarInternalFrame object from the taskbar "type" tab.
 * If this object is the last one in the "type", then the "type" tab is
 * removed as well.
 * <p>
 * 3. removeAllTabsFromTaskbar() : Removes all the tabs from the taskbar and
 * clears the master data object.
 * <p>
 * 4. reloadTaskbar() :  This method reloads the taskbar ( removes all tabs
 * and adds them again ). It is called from the addToTaskbar and
 * removeFromTaskbar methods (i.e, whenever the master data gets changed)
 */

public class TaskbarPanel extends JPanel {

    private LinkedHashMap<String, List<TaskbarInternalFrame>> taskbarData;
    private JMenuBar menuBar;
    private JLabel selectedFeatures;
    private JLabel cpuLabel;
    private JLabel memoryLabel;
    private JLabel playbackLabel;
    private JPanel taskbarPanel;
    private int numFeatures;
    private int numSamples;

    /**
     * Constructor to initialize the masterdata (taskbarData) and the JPanel
     */
    public TaskbarPanel() {

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        setLayout(new BorderLayout());
        taskbarPanel = new JPanel();
        taskbarPanel.setSize((int) rect.getMaxX(), 30);
        taskbarPanel.setLocation(0, (int) rect.getMaxY() - 30);
        taskbarPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        taskbarData = new LinkedHashMap<String, List<TaskbarInternalFrame>>();
        menuBar = new JMenuBar();
        taskbarPanel.add(menuBar);
        add(taskbarPanel, BorderLayout.WEST);


        
        JPanel statisticsPanel = new JPanel();
        statisticsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        
        if(MetaOmGraph.getActiveTable() != null) {
        	numFeatures = MetaOmGraph.getActiveTable().getNumberofFeaturesSelected();
        }
        
        
        selectedFeatures = new JLabel();
        selectedFeatures.setForeground(Color.BLACK);
        selectedFeatures.setBackground(Color.white);
        selectedFeatures.setOpaque(true);
        selectedFeatures.setIcon(new ImageIcon(getClass().getResource("/resource/silkicons/Cursor-Select-icon.png")));
        selectedFeatures.setBorder(new EmptyBorder(3, 5, 3, 5));
        selectedFeatures.setLocation(0, (int) rect.getMaxY() - 100);
        statisticsPanel.add(selectedFeatures);
        statisticsPanel.setToolTipText("# Features or Samples selected in the currently displayed table");
        
        statisticsPanel.add(new JLabel(" | "));
        // Add memory Usage
        memoryLabel = new JLabel(getMemoryUsage());
        memoryLabel.setOpaque(true);
        memoryLabel.setIcon(new ImageIcon(getClass().getResource("/resource/silkicons/Ram-icon.png")));
        memoryLabel.setBorder(new EmptyBorder(3, 5, 3, 5));
        memoryLabel.setLocation(0, (int) rect.getMaxY() - 100);
        statisticsPanel.add(memoryLabel);

        cpuLabel = new JLabel(getCPUUsage());
        cpuLabel.setOpaque(true);
        cpuLabel.setIcon(new ImageIcon(getClass().getResource("/resource/silkicons/CPU-icon.png")));
        cpuLabel.setBorder(new EmptyBorder(3, 5, 3, 5));
        cpuLabel.setLocation(0, (int) rect.getMaxY() - 100);
        statisticsPanel.add(cpuLabel);
        add(statisticsPanel);
        
        statisticsPanel.add(new JLabel(" | "));

        playbackLabel = new JLabel("");
        playbackLabel.setOpaque(true);
        playbackLabel.setIcon(new ImageIcon(getClass().getResource("/resource/loggingicons/logging16.png")));
        playbackLabel.setBorder(new EmptyBorder(3, 5, 3, 5));
        playbackLabel.setLocation(0, (int) rect.getMaxY() - 100);
        playbackLabel.setToolTipText("Open playback menu");
        statisticsPanel.add(playbackLabel);
        add(statisticsPanel, BorderLayout.EAST);

        playbackLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {

				if ((MetaOmGraph.getReproducibilityDashboardFrame() != null) && (MetaOmGraph.getReproducibilityDashboardFrame().isVisible())) {
					//frame.toFront();
					MetaOmGraph.getReproducibilityDashboardFrame().setVisible(false);
					return;
				}
				else if ((MetaOmGraph.getReproducibilityDashboardFrame() != null) && (!MetaOmGraph.getReproducibilityDashboardFrame().isVisible()) && (!MetaOmGraph.getReproducibilityDashboardFrame().isClosed())) {

					MetaOmGraph.getReproducibilityDashboardFrame().setVisible(true);
					return;
				}

            	MetaOmGraph.createReproducibilityLoggingFrame();
				
            }
          });
        
        
        // WIP: CPU usage
        OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double v = bean.getProcessCpuLoad();

        StatisticsThread thread = new StatisticsThread();
        thread.start();

    }

    // return memory usage in GB
    public String getMemoryUsage() {
        return FileUtils.byteCountToDisplaySize(
                Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()
        );
    }

    // Returns % of CPU being used by MetaOMgraph
    public String getCPUUsage() {
        OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double v = bean.getProcessCpuLoad() * 100.0;
        return String.format("%.2f", v) + "%";
    }
    
    public void setPlaybackIconBlue() {
    	
    	playbackLabel.setIcon(new ImageIcon(getClass().getResource("/resource/loggingicons/logging16_blue.png")));
    	
    }
    
    public void setPlaybackIconGray() {
    	
    	playbackLabel.setIcon(new ImageIcon(getClass().getResource("/resource/loggingicons/logging16.png")));
    	
    }

    /**
     * This method adds a tab to the taskbar
     * <p>
     * If the given TaskbarInternalFrame object has a model object with FrameType
     * initialized, then the object is added to the list of objects present in that
     * type.
     * <p>
     * Else if the TaskbarInternalFrame object does not have the FrameType initialized
     * in its model, then the object is directly added to the taskbar with its name.
     * <p>
     * At the end, the TaskbarPanel is reloaded to reflect the new addition
     */
    public void addToTaskbar(TaskbarInternalFrame currentFrame) {

        if (taskbarData.get(currentFrame.getModel().getFrameType()) != null) {
            ArrayList<TaskbarInternalFrame> listOfFrames = (ArrayList<TaskbarInternalFrame>) taskbarData.get(currentFrame.getModel().getFrameType());
            listOfFrames.add(currentFrame);
        } else {
            ArrayList<TaskbarInternalFrame> listOfFrames = new ArrayList<TaskbarInternalFrame>();
            listOfFrames.add(currentFrame);
            taskbarData.put(currentFrame.getModel().getFrameType(), listOfFrames);
        }

        reloadTaskbar();

    }

    
    public void setNumFeatures(int numFeatures) {
    	this.numFeatures = numFeatures;
    	
    	if ( selectedFeatures != null ) {
    		selectedFeatures.setText(this.numFeatures+" features");
    		selectedFeatures.revalidate();
    		}

    	
    }
    
    public void setNumSamples(int numSamples) {
    	this.numSamples = numSamples;
    	
    	if ( selectedFeatures != null ) {
    		selectedFeatures.setText(this.numSamples+" samples");
    		selectedFeatures.revalidate();
    		}

    	
    }

    /**
     * This method removes the given frame from the taskbar.
     * <p>
     * If the TaskbarInternalFrame object has the Frame Type property initialized, then
     * we remove the current frame from the list of frames of that frame type.
     * <p>
     * Else if TaskbarInternalFrame object doesn't have the Frame Type property, then
     * we remove the cureent frame from the master data ( as it is added directly to the
     * panel. )
     * <p>
     * At the end, the taskbar is reloaded to reflect the removal.
     */
    public void removeFromTaskbar(TaskbarInternalFrame currentFrame) {

        if (taskbarData.get(currentFrame.getModel().getFrameType()) != null) {
            ArrayList<TaskbarInternalFrame> listOfFrames = (ArrayList<TaskbarInternalFrame>) taskbarData.get(currentFrame.getModel().getFrameType());
            listOfFrames.remove(currentFrame);

            if (listOfFrames.size() == 0) {
                taskbarData.remove(currentFrame.getModel().getFrameType());
            }
        } else {
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
     * <p>
     * Then, it iterates over the taskbarData map, and creates the JMenus (tabs) and
     * the JMenuItems (individual tabs that popup when the tabs are pressed) according
     * to the type and frame hierarchy.
     * <p>
     * An actionlistener is added to each of the JMenuItems. When any JMenuItem
     * is pressed, it is maximized (if minimized) and moved to front.
     */
    public void reloadTaskbar() {

        menuBar.removeAll();

        if (taskbarData != null) {

            for (Map.Entry<String, List<TaskbarInternalFrame>> entryset : taskbarData.entrySet()) {
                JMenu menu = new JMenu(entryset.getKey());
                List<TaskbarInternalFrame> typeFrames = entryset.getValue();

                if (typeFrames != null) {
                    for (TaskbarInternalFrame frame : typeFrames) {
                        String menuItemName = frame.getModel().getFrameName();
                        menuItemName = StringUtils.abbreviate(menuItemName, 80);

                        JMenuItem menuItem = new JMenuItem(menuItemName);
                        JPanel tabItemPanel = new JPanel();
                        tabItemPanel.setLayout(new BorderLayout());
                        JLabel tabItemLabel = new JLabel(menuItemName);

                        tabItemPanel.add(tabItemLabel);

                        UIManager.put("MenuItem.selectionForeground", Color.BLUE);
                        menuItem.setText(menuItemName);
                        ;
                        menu.add(new JSeparator());
                        menu.add(menuItem);
                        menu.setMargin(new Insets(2, 8, 2, 8));

                        menuItem.addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent evt) {

                                try {
                                    if (frame != null) {
                                        frame.getDesktopPane().getDesktopManager().deiconifyFrame(frame);
                                        frame.getDesktopPane().getDesktopManager().maximizeFrame(frame);
                                        frame.getDesktopPane().getDesktopManager().minimizeFrame(frame);
                                        frame.getDesktopPane().getDesktopManager().deiconifyFrame(frame);

                                        frame.moveToFront();
                                    }
                                } catch (Exception e) {

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

    private class StatisticsThread extends Thread {
        public StatisticsThread() {
            super();
        }

        public void run() {
            String memoryUsage = "", cpuUsage = "";
            while (true) {
                try {
                    sleep(5000);
                    memoryUsage = getMemoryUsage();
                    cpuUsage = getCPUUsage();

                    cpuLabel.setText(cpuUsage);
                    memoryLabel.setText(memoryUsage);

                    /* for accessibility */
                    cpuLabel.setToolTipText("CPU usage: " + cpuUsage);
                    memoryLabel.setToolTipText("Memory allocated: " + memoryUsage);
                    

                } catch (InterruptedException e) {

                }
            }
        }
    }
}
