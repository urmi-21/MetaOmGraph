package edu.iastate.metnet.metaomgraph.logging;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.message.Message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * @author Harsha
 *
 * Class to format the logging string as JSON
 */
public class JSONMessage implements Message {

	private static final long serialVersionUID = 1L;
	private String messageString;
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public JSONMessage(){
        this(null);
    }

    public JSONMessage(Object msgObj){
        parseMessageAsJson(msgObj);
    }

    public JSONMessage(String msgStr){
        Map<String,String> msgObj = new HashMap<String,String>();
        msgObj.put("message", msgStr);
        parseMessageAsJson(msgObj);
    }

    private void parseMessageAsJson(Object msgObj){
        messageString = GSON.toJson(msgObj);
    }

    public String getFormattedMessage() {
    	return messageString.replace("\n", "%n");
  
    }

    public String getFormat() {
        return messageString.replace("\n", "%n");
    }

    public Object[] getParameters() {
        return null;
    }

    public Throwable getThrowable() {
        return null;
    }

}
