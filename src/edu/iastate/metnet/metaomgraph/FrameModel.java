package edu.iastate.metnet.metaomgraph;

public class FrameModel{
	
	private String type;
	private int frameNo;
	
	
	public FrameModel(String type, int frameNo) {
		super();
		this.type = type;
		this.frameNo = frameNo;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getFrameNo() {
		return frameNo;
	}
	public void setFrameNo(int frameNo) {
		this.frameNo = frameNo;
	}
		
}