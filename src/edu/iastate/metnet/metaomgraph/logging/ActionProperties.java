package edu.iastate.metnet.metaomgraph.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.ui.ReproducibilityDashboardPanel;

/**
 * @author Harsha
 * 
 *	<p>This is a bean class that represents a single action element, which is written to the reproducibility log file.</p>
 *	
 *	<h2>Description:</h2>
 *	<p>The reproducibility log contains a sequence of actions performed by the user on the MOG software. These are important actions 
 *	that have to be logged in real-time, so that the log file could be used as a document by the end-user or other researchers to 
 *	build upon it or be used for future reference. In order to have a robust documentation, the log file will be written in a 
 *	structured format (JSON format). This will allow programs to parse it programmatically and iterate through each action element.
 *	One such program is the playback feature (ReproducibilityDashboardPanel.java) provided along with this MOG release, which 
 *	allows users to see the logging happening in real-time on a dashboard and open previously logged session. </p>
 *
 *	<p>The current class (ActionProperties.java) represents the JSON structure of the action element written to the log. 
 *	The JSON Structure for all actions is similar to the below example:</p>
<br/>
{<br/>
    "actionNumber": 3,<br/>
    "actionCommand": "line-chart",<br/>
    "actionParameters": {<br/>
      "parent": 2,<br/>
      "section": "Feature Metadata"<br/>
    },<br/>
    "dataParameters": {<br/>
      "Data Transformation": "NONE",<br/>
      "Selected List": "Complete List",<br/>
      "Chart Title": "",<br/>
      "Selected Features": {<br/>
        "3927": "ABHD12",<br/>
        "3356": "ABHD13"<br/>
      },<br/>
      "XAxis": "portions_analytes_aliquots_submitter_id",<br/>
      "YAxis": "Expression level"<br/>
    },<br/>
    "otherParameters": {<br/>
      "result": "OK",<br/>
      "Sample Action": 1,<br/>
      "Color 2": {<br/>
        "value": -1,<br/>
        "falpha": 0.0<br/>
      },<br/>
      "Color 1": {<br/>
        "value": -11484592,<br/>
        "falpha": 0.0<br/>
      },<br/>
      "Playable": "true"<br/>
    },<br/>
    "timestamp": "2020-07-20 15:41:03.367 CDT"<br/>
  }<br/>
  <br/>
 *	<p>Each action element will contain the following properties:</p>
 *	<br/>
 *	<p><b>1. actionNumber :</b> This is the variable representing a unique action number assigned to each action in the session. When a new 
 *	action object is created at any point, the next action number to be assigned to this variable is obtained from the static variable
 *	"counter", which increments after each ActionProperties object creation.</p>
 *
 *	<p><b>2. actionCommand :</b> This variable contains the command name for the action taken. The command name is user assigned, but must be
 *	unique for every action. (For eg: line-chart, box-plot etc.)</p>
 *
 *	<p><b>3. actionParameters :</b> This is a Map<String,Object> variable. It has been provided so that any number of properties related to the
 *	action itself could be stored in it. Since the variable is of type Map<String,Object>, it acts like a dictionary where we can store
 *	key value pairs. The key should always be a string, where as the value could be any variable (Integer value, String value, List, 
 *	Set, Map, User defined object etc ).  In the above sample JSON, we can see that "parent" and "section" parameters have been added
 *	to the actionParameters, since these parameters are related to the action. The action parameters will not be displyed to the 
 *	Reproducibility Dashboard Panel.</p>
 *
 *	<p><b>4. dataParameters :</b> This is again a Map<String,Object> variable. Similar to the actionParameters, we can store any number of key 
 *	value parameters in this, but the only difference is that, whatever variables are written in this variable will be displayed on the
 *	Reproducibility Dashboard Panel as it is. Hence, extra care must be taken to format the variable names properly.</p>
 *
 *	<p><b>5. otherParameters:</b> Similar variable to actionParameters. It is provided to store miscellaneous variables not related to the action
 *	which should not be displayed in the Reproducibility Dashboard Panel. It could be called an extra variable. </p>
 *
 *	<p><b>6. timestamp :</b> This variable stores the timestamp at which the action was performed.</p>

 *	<br/>
 *	<p>To log any action anywhere in this project, just create a new ActionProperties object using the parameterized constructor provided,
 *	add all the parameters you want to print in the log, and then call the logActionProperties() method.</p>
 */
public class ActionProperties {
	
	private static Logger logger = MetaOmGraph.logger;

	private static int counter;
	private int actionNumber;
	private String actionCommand;
	private Map<String,Object> actionParameters;
	private Map<String,Object> dataParameters;
	private Map<String,Object> otherParameters;
	private String timestamp;
	
	
	

	/**
	 * 
	 * This is the parameterized constructor that takes actionCommand, actionParameters, dataParameters, otherParameters and
	 * timestamp as inputs to create a new ActionProperties object.
	 * 
	 * The actionNumber is not present in this parameterized constructor because it will be assigned when we call the 
	 * logActionProperties method.
	 * 
	 */
	
	public ActionProperties(String actionCommand, Map<String, Object> actionParameters,
			Map<String, Object> dataParameters, Map<String, Object> otherParameters, String timestamp) {
		super();
		this.actionCommand = actionCommand;
		this.actionParameters = actionParameters;
		this.dataParameters = dataParameters;
		this.otherParameters = otherParameters;
		this.timestamp = timestamp;
	}

	
	/**
	 * Getter and Setter methods for the variables
	 * 
	 */
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

	
	/**<p>
	 * This method logs the actions to the log file in a JSON Format and also populates the current session tree of the
	 * Reproducibility Dashboard Panel if it is open (i.e if ReproducibilityDashboardPanel object is not null ). For each action logged,
	 * a fresh action number is assigned by incrementing the counter static variable.
	 * </p>
	 * <p>
	 * It is important to note that when the logger.printf() is used to log to the file, the current object is wrapped in JSONMessage
	 * object and passed to the logger.printf(). The JSONMessage wrapper ensures that the ActionProperties object is converted to a
	 * JSON format message before being written to the file.
	 * </p>
	 * <p>
	 * Apart from writing the current session's actions to the log file, this method also updates the Reproducibility 
	 * Dashboard Panel's play tree with the current session's actions in real-time. This is acheived using the Reproducibility Dashboard
	 * Panel's populateCurrentSessionTree() method.
	 * </p>
	 */
	public void logActionProperties() {

		if(MetaOmGraph.getLoggingRequired()) {
			try {
				counter++;
				this.actionNumber=counter;
				logger = MetaOmGraph.getLogger();
				if(!this.getActionCommand().equalsIgnoreCase("general-properties")) {
					logger.info(",");
				}

				logger.printf(Level.INFO,new JSONMessage(this).getFormattedMessage());

				ReproducibilityDashboardPanel rdp = null;
				try {
					rdp = MetaOmGraph.getReproducibilityDashboardPanel();

					if(rdp != null) {
						rdp.populateCurrentSessionTree(this);
					}
				}
				catch(Exception e) {

				}
			}
			catch(Exception ex) {

			}
		}
	}


}
