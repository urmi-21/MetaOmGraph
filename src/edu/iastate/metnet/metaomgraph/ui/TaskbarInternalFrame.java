package edu.iastate.metnet.metaomgraph.ui;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;

public class TaskbarInternalFrame extends JInternalFrame{
	
	private FrameModel model;

	public TaskbarInternalFrame() {
		this("");
	}
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
                getDesktopIcon().setVisible(false);;
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

