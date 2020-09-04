package edu.iastate.metnet.metaomgraph;

/**
 * 
 * @author Harsha
 * 
 * This class stores the frame type, frame name and the frame number of
 * an internal frame. The TaskbarInternalFrame class has an attribute of
 * FrameModel type, that saves the details of the frame to be used later
 * when populating the taskbar menu items.
 *
 */
public class FrameModel{
	
	private String frameType;
	private String frameName;
	private int frameNo;
	
	
	public FrameModel(String frameType, String frameName, int frameNo) {
		super();
		this.frameType = frameType;
		this.frameName = frameName;
		this.frameNo = frameNo;
	}
	
	public int getFrameNo() {
		return frameNo;
	}
	public void setFrameNo(int frameNo) {
		this.frameNo = frameNo;
	}

	public String getFrameType() {
		return frameType;
	}

	public void setFrameType(String frameType) {
		this.frameType = frameType;
	}

	public String getFrameName() {
		return frameName;
	}

	public void setFrameName(String frameName) {
		this.frameName = frameName;
	}
	
		
}