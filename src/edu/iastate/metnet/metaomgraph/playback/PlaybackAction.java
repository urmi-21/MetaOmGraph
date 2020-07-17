package edu.iastate.metnet.metaomgraph.playback;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JTextPane;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import edu.iastate.metnet.metaomgraph.ui.MetaOmTablePanel;

public class PlaybackAction {

	public void playChart(ActionProperties chartAction, String chartName, HashSet<String> includedSamples, HashSet<String> excludedSamples) {

		int val2[] = null;
		Map<Object,Object> genes = null;

		try {
			
			if(chartAction.getDataParameters().get("Selected Features") != null) {
				genes = (Map<Object,Object>)chartAction.getDataParameters().get("Selected Features");

				Integer [] val = new Integer[genes.size()];


				int j = 0;
				for (Map.Entry<Object, Object> entry : genes.entrySet()) {
					if(entry.getKey() instanceof String){
						val[j++] = Integer.parseInt((String)entry.getKey());
					}
					else if(entry.getKey() instanceof Integer) {
						val[j++] = (Integer)entry.getKey();
					}
				}

				val2 = new int[val.length];

				for (int i=0;i<val.length;i++) {
					val2[i] = val[i];
				}
			}


			MetaOmTablePanel mp = MetaOmGraph.getActiveTablePanel();

			if(chartName == "line-chart") {
				mp.graphSelectedRows(val2, includedSamples, excludedSamples);
			}
			else if(chartName == "box-plot") {
				mp.makeBoxPlot(val2, includedSamples, excludedSamples);
			}
			else if(chartName == "scatter-plot") {
				mp.graphPairs(val2, includedSamples, excludedSamples);
			}
			else if(chartName == "histogram") {
				mp.createHistogram(val2, includedSamples, excludedSamples);
			}
			else if(chartName == "line-chart-default-grouping") {
				mp.plotLineChartDefaultGrouping(val2, includedSamples, excludedSamples);
			}
			else if(chartName == "line-chart-choose-grouping") {
				String groupChosen = (String)chartAction.getDataParameters().get("Grouping Attribute");
				mp.plotLineChartChooseGrouping(val2, groupChosen, includedSamples, excludedSamples);
			}
			else if(chartName == "bar-chart") {
				String columnChosen = (String)chartAction.getDataParameters().get("Selected Column");
				mp.plotBarChart(columnChosen, true);
			}
			else if(chartName == "correlation-histogram") {
				String correlationCol = (String)chartAction.getDataParameters().get("Correlation Column");
				mp.plotCorrHist(correlationCol, true);
			}

		}
		catch(Exception e) {

		}
	}

}
