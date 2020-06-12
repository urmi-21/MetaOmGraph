package edu.iastate.metnet.metaomgraph.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class UserPreferences {

	public Object recentProjects;
	public Object theme;
	public String showTips;
	
	public Object getRecentProjects() {
		return recentProjects;
	}
	public void setRecentProjects(String recentProjects) {
		this.recentProjects = recentProjects;
	}
	public Object getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	public String getShowTips() {
		return showTips;
	}
	public void setShowTips(String showTips) {
		this.showTips = showTips;
	}
	public UserPreferences(String recentProjects, String theme, String showTips) {
		super();
		this.recentProjects = recentProjects;
		this.theme = theme;
		this.showTips = showTips;
	}
	
	public void logUserPreferences(Logger logger) {
		
		logger.printf(Level.INFO,new JSONMessage(this).getFormattedMessage());
		
		
	}
}
