package edu.iastate.metnet.metaomgraph;

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