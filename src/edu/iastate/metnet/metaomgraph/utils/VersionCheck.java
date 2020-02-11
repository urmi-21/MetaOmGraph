package edu.iastate.metnet.metaomgraph.utils;

import java.io.IOException;
import java.net.URL;
import javax.swing.JOptionPane;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Class to check current version of MOG.
 * 
 * @author urmi
 *
 */
public class VersionCheck {
	private String thisVersion;// =MetaOmGraph.getVersion();
	private String latestVersion = "";
	private String xmlpath = "http://metnetweb.gdcb.iastate.edu/MetaOmGraph/meta/MOGversion.xml";

	public VersionCheck(String thisver) {
		thisVersion = thisver;
		
			try {
				latestVersion = getLatestVersionOnline();
			} catch (JDOMException | IOException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "Error while checking MOG version. MOG version could not be verified", "Error",JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		
	}

	public String getLatestVersionOnline() throws JDOMException, IOException
			{
		String res = "";
		// read xml online to get the latest version
		SAXBuilder builder = new SAXBuilder();
		URL xmlUrl = new URL(xmlpath);
		Document document = builder.build(xmlUrl);
		Element rootNode = document.getRootElement();
		//System.out.println("Ver:"+rootNode.getChildText("Version"));
		//System.out.println("ThisVer:"+thisVersion);
		res=rootNode.getChildText("Version");
		return res;
	}

	public boolean isLatestMOG() {
		//in case of error do nothing
		if(latestVersion.equals("")) {
			return true;
		}
		if (thisVersion.equals(latestVersion)) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {

		System.out.println("inmain");

		VersionCheck ob = new VersionCheck("1");
		if(ob.isLatestMOG()) {
			System.out.println("LATEST");
		}else {
			System.out.println("OLD");
		}
	}

}
