package edu.iastate.metnet.metaomgraph.playback;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JTextPane;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import edu.iastate.metnet.metaomgraph.ui.MetaOmTablePanel;

public class PlaybackAction {

	public void playChart(ActionProperties chartAction, String chartName) {
		
		int val2[] = null;
		Map<Object,Object> genes = (Map<Object,Object>)chartAction.getDataParameters().get("Selected Features");

		Integer [] val = new Integer[genes.size()];
		
		try {
		int j = 0;
		for (Map.Entry<Object, Object> entry : genes.entrySet()) {
		       if(entry.getKey() instanceof String){
		            val[j++] = Integer.parseInt((String)entry.getKey());
		          }
		       else if(entry.getKey() instanceof Integer) {
		    	   val[j++] = (Integer)entry.getKey();
		       }
		 }
		
		}
		catch(Exception e) {
			
			StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            
			JDialog jd = new JDialog();
			JTextPane jt = new JTextPane();
			jt.setText(exceptionAsString);
			jt.setBounds(10, 10, 300, 100);
			jd.getContentPane().add(jt);
			jd.setBounds(100, 100, 500, 200);
			jd.setVisible(true);
		}

		val2 = new int[val.length];

		for (int i=0;i<val.length;i++) {
			val2[i] = val[i];
		}

		MetaOmTablePanel mp = MetaOmGraph.getActiveTablePanel();
		
		if(chartName == "line-chart") {
			mp.graphSelectedRows(val2);
		}
		else if(chartName == "box-plot") {
			mp.makeBoxPlot(val2);
		}
		else if(chartName == "scatter-plot") {
			mp.graphPairs(val2);
		}
		else if(chartName == "histogram") {
			mp.createHistogram(val2);
		}
		
	}
	
}
