package edu.iastate.metnet.metaomgraph.logging;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Harsha
 *
 *This class is a bean class for actions taken by the user that are to be logged to the reproducibility log
 *
 */
public class ActionProperties {

	private static int counter;
	private int actionNumber;
	private String actionCommand;
	private Map<String,Object> actionParameters;
	private Map<String,Object> dataParameters;
	private Map<String,Object> otherParameters;
	private String timestamp;

	public ActionProperties(String actionCommand, Map<String, Object> actionParameters,
			Map<String, Object> dataParameters, Map<String, Object> otherParameters, String timestamp) {
		super();
		this.actionCommand = actionCommand;
		this.actionParameters = actionParameters;
		this.dataParameters = dataParameters;
		this.otherParameters = otherParameters;
		this.timestamp = timestamp;
	}

	public static int getCounter() {
		return counter;
	}

	public static void setCounter(int counter) {
		ActionProperties.counter = counter;
	}
	
	public int getActionNumber() {
		return actionNumber;
	}

	public void setActionNumber(int actionNumber) {
		this.actionNumber = actionNumber;
	}

	public String getActionCommand() {
		return actionCommand;
	}

	public void setActionCommand(String actionCommand) {
		this.actionCommand = actionCommand;
	}

	public Map<String, Object> getActionParameters() {
		return actionParameters;
	}

	public void setActionParameters(Map<String, Object> actionParameters) {
		this.actionParameters = actionParameters;
	}

	public Map<String, Object> getDataParameters() {
		return dataParameters;
	}

	public void setDataParameters(Map<String, Object> dataParameters) {
		this.dataParameters = dataParameters;
	}

	public Map<String, Object> getOtherParameters() {
		return otherParameters;
	}

	public void setOtherParameters(Map<String, Object> otherParameters) {
		this.otherParameters = otherParameters;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void logActionProperties(Logger logger) {
		counter++;
		this.actionNumber=counter;
		logger.info(",");
		logger.printf(Level.INFO,new JSONMessage(this).getFormattedMessage());
	}

}
