package edu.iastate.metnet.metaomgraph.logging;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class ActionProperties {

	private static int actionNumber;
	private String actionCommand;
	private String selectedRows;
	private List<Parameters> parameters;
	private String result;
	private String timestamp;
	
	public ActionProperties(String actionCommand, String selectedRows, List<Parameters> parameters,String result, String timestamp) {
		super();
		this.actionCommand = actionCommand;
		this.selectedRows = selectedRows;
		this.parameters = parameters;
		this.result = result;
		this.timestamp = timestamp;
	}
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public static int getActionNumber() {
		return actionNumber;
	}
	public static void setActionNumber(int actionNumber) {
		ActionProperties.actionNumber = actionNumber;
	}
	public String getActionCommand() {
		return actionCommand;
	}
	public void setActionCommand(String actionCommand) {
		this.actionCommand = actionCommand;
	}
	public String getSelectedRows() {
		return selectedRows;
	}
	public void setSelectedRows(String selectedRows) {
		this.selectedRows = selectedRows;
	}
	public List<Parameters> getParameters() {
		return parameters;
	}
	public void setParameters(List<Parameters> parameters) {
		this.parameters = parameters;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public void logActionProperties(Logger logger) {
		actionNumber++;
		logger.info(",{");
		logger.info("\"actionNumber\":\""+actionNumber+"\",");
		logger.info("\"actionCommand\":\""+actionCommand+"\",");
		logger.info("\"selectedRows\":\""+selectedRows+"\",");
		logger.info("\"parameters\":\""+parameters+"\",");
		logger.info("\"result\":\""+result+"\",");
		logger.info("\"timestamp\":\""+timestamp+"\"");
		logger.info("}");
	}
	
}
