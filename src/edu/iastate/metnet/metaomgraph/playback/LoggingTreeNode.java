package edu.iastate.metnet.metaomgraph.playback;

/**
 * 
 * @author Harsha
 *
 * This is the bean class for play tree node
 */
public class LoggingTreeNode {

	private String nodeName;
	private String commandName;
	private int nodeNumber;
	
	public LoggingTreeNode(String nodeName, String commandName, int nodeNumber) {
		super();
		this.nodeName = nodeName;
		this.commandName = commandName;
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

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	@Override
	public String toString() {
		return nodeName;
	}
	
	
	
	
	
}
