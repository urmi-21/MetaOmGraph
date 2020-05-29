/**
 * 
 */
package edu.iastate.metnet.metaomgraph.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author sumanth
 *
 */
public class PropertyFileReader {
	private Properties prop;
	
	public PropertyFileReader(String propFileName) throws IOException {
		prop = new Properties();
		InputStream inputStream = null;
		inputStream = PropertyFileReader.class.getResourceAsStream(propFileName);
		if(inputStream != null) {
			prop.load(inputStream);
			inputStream.close();
		}
		else
			throw new FileNotFoundException("Cannot find properties file");
	}
	
	public String getProperty(String name) {
		return prop.getProperty(name, "");
	}

}
