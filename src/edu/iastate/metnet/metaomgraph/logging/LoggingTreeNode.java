package edu.iastate.metnet.metaomgraph.logging;

public class LoggingTreeNode {

	private String nodeName;
	private int nodeNumber;
	
	public LoggingTreeNode(String nodeName, int nodeNumber) {
		super();
		this.nodeName = nodeName;
		this.nodeNumber = nodeNumber;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public int getNodeNumber() {
		return nodeNumber;
	}

	public void setNodeNumber(int nodeNumber) {
		this.nodeNumber = nodeNumber;
	}

	@Override
	public String toString() {
		return nodeName;
	}
	
	
	
	
	
}
