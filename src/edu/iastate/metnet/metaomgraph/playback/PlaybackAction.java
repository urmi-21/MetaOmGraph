package edu.iastate.metnet.metaomgraph.playback;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.MetadataCollection;
import edu.iastate.metnet.metaomgraph.MetadataHybrid;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import edu.iastate.metnet.metaomgraph.ui.MetaOmTablePanel;

/**
 * 
 * @author Harsha
 * <br/>
 * <p>
 * Wrapper class containing all the methods required to play actions in the Playback Panel 
 * </p>
 *
 */
public class PlaybackAction {
	
	private static final String LINE_CHART_COMMAND = "line-chart";
	private static final String SCATTER_PLOT_COMMAND = "scatter-plot";
	private static final String BOX_PLOT_COMMAND = "box-plot";
	private static final String HISTOGRAM_COMMAND = "histogram";
	private static final String LINE_CHART_DEFAULT_GROUPING_COMMAND = "line-chart-default-grouping";
	private static final String LINE_CHART_CHOOSE_GROUPING_COMMAND = "line-chart-choose-grouping";
	private static final String BAR_CHART_COMMAND = "bar-chart";
	private static final String CORRELATION_HISTOGRAM_COMMAND = "correlation-histogram";
	private static final String SAMPLE_ACTION_PROPERTY = "Sample Action";
	private static final String SELECTED_FEATURES_PROPERTY = "Selected Features";
	private static final String INCLUDED_SAMPLES_PROPERTY = "Included Samples";
	private static final String EXCLUDED_SAMPLES_PROPERTY = "Excluded Samples";
	private static final String GROUPING_ATTRIBUTE_PROPERTY = "Grouping Attribute";
	private static final String SELECTED_COLUMN_PROPERTY = "Selected Column";
	private static final String CORRELATION_COLUMN_PROPERTY = "Correlation Column";
	private static final String PLAYABLE_PROPERTY = "Playable";
	
	
	/**
	 * <p>
	 * This method is the first method called when the play button is clicked. It takes the list of actions selected (allPaths) as input, 
	 * collects the included and exlcluded samples from the properties of the actions, and calls the respective play method based on the action 
	 * type. For eg: if a line-chart action is played, then the playChart method is called.
	 * <br/>
	 * Before playing the action, the method checks whether its "Playable" property is set to true or not. If not, the method returns without
	 * playing.
	 * </p>
	 * 
	 */
	public void playActions(int tabNo, JTree selectedTree, TreePath[] allPaths, HashMap<Integer,PlaybackTabData> allTabsInfo) {
		
		for (TreePath path : allPaths) {
			DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) path.getLastPathComponent();
			Object nodeObj = node2.getUserObject();
			LoggingTreeNode ltn = (LoggingTreeNode) nodeObj;

			ActionProperties playedAction = allTabsInfo.get(tabNo).getActionObjects().get(ltn.getNodeNumber());


			if(playedAction.getOtherParameters().get(PLAYABLE_PROPERTY)!=null) {

				String isPlayable = (String)playedAction.getOtherParameters().get(PLAYABLE_PROPERTY);
				if(isPlayable.equals("true"))
				{
					int samplesActionId = 1;
					if(playedAction.getOtherParameters().get(SAMPLE_ACTION_PROPERTY) instanceof Double) {
						double temp = (double) playedAction.getOtherParameters().get(SAMPLE_ACTION_PROPERTY);
						samplesActionId = (int)temp;
					}
					else {
						samplesActionId = (int)playedAction.getOtherParameters().get(SAMPLE_ACTION_PROPERTY);
					}

					HashSet<String> includedSamples = new HashSet<String>();
					HashSet<String> excludedSamples = new HashSet<String>();

					for(int i=0;i<allTabsInfo.get(tabNo).getActionObjects().size();i++) {

						if(allTabsInfo.get(tabNo).getActionObjects().get(i).getActionNumber() == samplesActionId) {

							ActionProperties sampleAction = allTabsInfo.get(tabNo).getActionObjects().get(i);

							if(sampleAction.getOtherParameters().get(INCLUDED_SAMPLES_PROPERTY) instanceof List<?>) {
								includedSamples = new HashSet<String>((List<String>)sampleAction.getOtherParameters().get(INCLUDED_SAMPLES_PROPERTY));
							}
							else if(sampleAction.getOtherParameters().get(INCLUDED_SAMPLES_PROPERTY) instanceof HashSet<?>) {
								includedSamples = (HashSet<String>)sampleAction.getOtherParameters().get(INCLUDED_SAMPLES_PROPERTY);
							}

							if(sampleAction.getOtherParameters().get(EXCLUDED_SAMPLES_PROPERTY) instanceof List<?>) {
								excludedSamples = new HashSet<String>((List<String>)sampleAction.getOtherParameters().get(EXCLUDED_SAMPLES_PROPERTY));
							}
							else if(sampleAction.getOtherParameters().get(EXCLUDED_SAMPLES_PROPERTY) instanceof HashSet<?>) {
								excludedSamples = (HashSet<String>)sampleAction.getOtherParameters().get(EXCLUDED_SAMPLES_PROPERTY);
							}
							//urmi
							break;
						}
					}


					
					
					if (ltn.getCommandName().equalsIgnoreCase(LINE_CHART_COMMAND)) {
						playChart(playedAction, LINE_CHART_COMMAND, includedSamples, excludedSamples);
					} else if (ltn.getCommandName().equalsIgnoreCase(SCATTER_PLOT_COMMAND)) {
						playChart(playedAction, SCATTER_PLOT_COMMAND, includedSamples, excludedSamples);
					} else if (ltn.getCommandName().equalsIgnoreCase(BOX_PLOT_COMMAND)) {
						playChart(playedAction, BOX_PLOT_COMMAND, includedSamples, excludedSamples);
					} else if (ltn.getCommandName().equalsIgnoreCase(HISTOGRAM_COMMAND)) {
						playChart(playedAction, HISTOGRAM_COMMAND, includedSamples, excludedSamples);
					} else if (ltn.getCommandName().equalsIgnoreCase(LINE_CHART_DEFAULT_GROUPING_COMMAND)) {
						playChart(playedAction, LINE_CHART_DEFAULT_GROUPING_COMMAND, includedSamples, excludedSamples);
					} else if (ltn.getCommandName().equalsIgnoreCase(LINE_CHART_CHOOSE_GROUPING_COMMAND)) {
						playChart(playedAction, LINE_CHART_CHOOSE_GROUPING_COMMAND, includedSamples, excludedSamples);
					} else if (ltn.getCommandName().equalsIgnoreCase(BAR_CHART_COMMAND)) {
						playChart(playedAction, BAR_CHART_COMMAND, includedSamples, excludedSamples);
					} else if (ltn.getCommandName().equalsIgnoreCase(CORRELATION_HISTOGRAM_COMMAND)) {
						playChart(playedAction, CORRELATION_HISTOGRAM_COMMAND, includedSamples, excludedSamples);
					}
					
					
					

				}
			}
		}
	}

	
	/**
	 * This method plays the chart-type action by calling the respective chart's MetaOmTablePanel function that generates and displays the chart
	 * based on the selected features and samples.
	 * 
	 * Before calling the MetaOmTablePanel functions, the selected features' ids are collected into an integer array, to be passed to them.
	 */
	public void playChart(ActionProperties chartAction, String chartName, HashSet<String> includedSamples, HashSet<String> excludedSamples) {

		int val2[] = null;
		Map<Object,Object> genes = null;

		try {
			
			if(chartAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY) != null) {
				genes = (Map<Object,Object>)chartAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY);

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
				
				//urmi add in reverse order
				for (int i=val.length-1;i>=0;i--) {
					System.out.println(i);
					System.out.println(val.length-i);
					val2[val.length-1-i] = val[i];
				}
				
			}
			
			
			//urmi manage samples
			MetadataHybrid mhyb = MetaOmGraph.getActiveProject().getMetadataHybrid();
			if(mhyb==null) {
				JOptionPane.showMessageDialog(null, "Error in playback. MOG metadata NULL!!!", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			MetadataCollection mcol = mhyb.getMetadataCollection();
			Set<String> currentProjectIncludedSamples = mcol.getIncluded();
			Set<String> currentProjectExcludedSamples = mcol.getExcluded();
			
			//convert excluded samples to boolean excluded list and pass to chart
			String[] dataCols = MetaOmGraph.getActiveProject().getDataColumnHeaders();
			boolean [] toExcludeSamples=new boolean[dataCols.length]; 
			for (int j = 0; j < dataCols.length; j++) {
				if (excludedSamples.contains(dataCols[j])) {
					toExcludeSamples[j] = true;
				}
			}
			


			MetaOmTablePanel mp = MetaOmGraph.getActiveTablePanel();
			
			if(chartName == LINE_CHART_COMMAND) {
				//for line chart
				mcol.setIncluded(includedSamples);
				mcol.setExcluded(excludedSamples);
				//update the excluded samples
				MetaOmAnalyzer.updateExcluded(excludedSamples);
				
				//make chart
				mp.graphSelectedRows(val2);
				
				//reset samples to current
				//JOptionPane.showMessageDialog(null, "Excluded cols:"+String.valueOf(MetaOmAnalyzer.getExcludeCount()));
				//TimeUnit.SECONDS.sleep(3);
				mcol.setIncluded(currentProjectIncludedSamples);
				mcol.setExcluded(currentProjectExcludedSamples);					
				//JOptionPane.showMessageDialog(null, "updating exclude"+String.valueOf(currentProjectExcludedSamples.size()));
				MetaOmAnalyzer.updateExcluded(currentProjectExcludedSamples);
			}
			else if(chartName == BOX_PLOT_COMMAND) {
				mp.makeBoxPlot(val2,toExcludeSamples);
			}
			else if(chartName == SCATTER_PLOT_COMMAND) {
				mp.graphPairs(val2,toExcludeSamples);
			}
			else if(chartName == HISTOGRAM_COMMAND) {
								
				mp.createHistogram(val2,toExcludeSamples);
			}
			else if(chartName == LINE_CHART_DEFAULT_GROUPING_COMMAND) {
				//for line chart
				mcol.setIncluded(includedSamples);
				mcol.setExcluded(excludedSamples);
				//update the excluded samples
				MetaOmAnalyzer.updateExcluded(excludedSamples);
				
				mp.plotLineChartDefaultGrouping(val2);
				
				//reset samples to current
				//JOptionPane.showMessageDialog(null, "Excluded cols:"+String.valueOf(MetaOmAnalyzer.getExcludeCount()));
				//TimeUnit.SECONDS.sleep(3);
				mcol.setIncluded(currentProjectIncludedSamples);
				mcol.setExcluded(currentProjectExcludedSamples);					
				//JOptionPane.showMessageDialog(null, "updating exclude"+String.valueOf(currentProjectExcludedSamples.size()));
				MetaOmAnalyzer.updateExcluded(currentProjectExcludedSamples);
			}
			else if(chartName == LINE_CHART_CHOOSE_GROUPING_COMMAND) {
				//for line chart
				mcol.setIncluded(includedSamples);
				mcol.setExcluded(excludedSamples);
				//update the excluded samples
				MetaOmAnalyzer.updateExcluded(excludedSamples);
				
				String groupChosen = (String)chartAction.getDataParameters().get(GROUPING_ATTRIBUTE_PROPERTY);
				mp.plotLineChartChooseGrouping(val2, groupChosen);
				
				//reset samples to current
				//JOptionPane.showMessageDialog(null, "Excluded cols:"+String.valueOf(MetaOmAnalyzer.getExcludeCount()));
				//TimeUnit.SECONDS.sleep(3);
				mcol.setIncluded(currentProjectIncludedSamples);
				mcol.setExcluded(currentProjectExcludedSamples);					
				//JOptionPane.showMessageDialog(null, "updating exclude"+String.valueOf(currentProjectExcludedSamples.size()));
				MetaOmAnalyzer.updateExcluded(currentProjectExcludedSamples);
				
			}
			else if(chartName == BAR_CHART_COMMAND) {
				String columnChosen = (String)chartAction.getDataParameters().get(SELECTED_COLUMN_PROPERTY);
				mp.plotBarChart(columnChosen, true);
			}
			else if(chartName == CORRELATION_HISTOGRAM_COMMAND) {
				String correlationCol = (String)chartAction.getDataParameters().get(CORRELATION_COLUMN_PROPERTY);
				mp.plotCorrHist(correlationCol, true);
			}
			
			
			
			

		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Failed to playback!!!" + e, "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();

		}
	}

}
