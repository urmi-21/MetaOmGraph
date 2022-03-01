package edu.iastate.metnet.metaomgraph.ui;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;

/**
 * 
 * @author Harsha
 * 
 * This is the custom Internal Frame UI class which extends a JInternalFrame,
 * and allows the frames to be minimized to a taskbar (TaskbarPanel).
 * 
 * In addition to the JInternalFrame features, this class offers a feature to
 * save properties along with the frame (FrameModel). This is used to store 
 * the frame type (Line Chart, DEA, etc.) to organize the taskbar into tab 
 * types.
 * 
 * When any object of this class is iconified (minimized), it doesn't create
 * the default icon at the bottom, which looks weird, but just minimizes to the
 * taskbar.
 * 
 * The activities of adding to the taskbar, removing from the taskbar etc, are
 * handled using the Internal Frame Listener. This allows the taskbar addition
 * and removal methods to be triggered on the corresponding Internal Frame action
 * like open, close and iconify.
 *
 */
public class TaskbarInternalFrame extends JInternalFrame{
	
	private FrameModel model;

	public TaskbarInternalFrame() {
		this("");
	}

	/**
	 * This constructor initializes the frame with a title, and then adds
	 * an InternalFrameListener to the frame, that calls the appropriate
	 * taskbar operations like addToTaskbar, removeFromTaskbar on the events
	 * of internalFrameOpened and internalFrameClosed respectively.
	 * 
	 * On iconify, the default desktop minimize icon is disabled.
	 * 
	 */
	public TaskbarInternalFrame(String title) {
		
		super(title);
		
		TaskbarInternalFrame currentFrame = this;
		addInternalFrameListener(new InternalFrameAdapter(){
			
			public void internalFrameOpened(InternalFrameEvent e) {
                MetaOmGraph.getTaskBar().addToTaskbar(currentFrame);
            }
			
            public void internalFrameClosing(InternalFrameEvent e) {
            	MetaOmGraph.getTaskBar().removeFromTaskbar(currentFrame);
            }
            
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                getDesktopIcon().setVisible(false);
            }
            
        });
	
	}
	
	public FrameModel getModel() {
		return model;
	}

	public void setModel(FrameModel model) {
		this.model = model;
	}

	
	
	
}

