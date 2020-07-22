package edu.iastate.metnet.metaomgraph.playback;

/**
 * 
 * @author Harsha
 * <br/>
 * <p>
 * This is the bean class for play tree node. This kind of object is required to maintain more data than just the node's name in a node.
 * </p>
 * <br/>
 * <h3>Variable names:</h3>
 * <p>
 * 1. nodeName - the name of the node which will appear as it is on the tree
 * 2. commandName - the name of the action command for the particular action
 * 3. nodeNumber - the number of the node
 * </p>
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
