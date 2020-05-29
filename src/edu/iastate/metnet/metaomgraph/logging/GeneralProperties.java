package edu.iastate.metnet.metaomgraph.logging;

import java.util.Date;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Harsha
 *
 *This class is a bean class for general machine properties (Memory, CPU, OS etc) that are to be logged to the reproducibility log
 *
 */

public class GeneralProperties {

	private String mogVersion;
	private String javaVersion;
	private String OS;
	private String CPU;
	private String memory;
	private String sessionID;
	private String startTimestamp;
	
	public String getMogVersion() {
		return mogVersion;
	}
	public void setMogVersion(String mogVersion) {
		this.mogVersion = mogVersion;
	}
	public String getJavaVersion() {
		return javaVersion;
	}
	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}
	public String getOS() {
		return OS;
	}
	public void setOS(String oS) {
		OS = oS;
	}
	public String getCPU() {
		return CPU;
	}
	public void setCPU(String cPU) {
		CPU = cPU;
	}
	public String getMemory() {
		return memory;
	}
	public void setMemory(String memory) {
		this.memory = memory;
	}
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	public String getStartTimestamp() {
		return startTimestamp;
	}
	public void setStartTimestamp(String startTimestamp) {
		this.startTimestamp = startTimestamp;
	}
	
	public void logGeneralProperties(Logger logger) {
		
		logger.printf(Level.INFO,new JSONMessage(this).getFormattedMessage());
		
		
	}
}
