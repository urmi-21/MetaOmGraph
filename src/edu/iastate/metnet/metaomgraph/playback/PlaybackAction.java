package edu.iastate.metnet.metaomgraph.playback;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.MetadataCollection;
import edu.iastate.metnet.metaomgraph.MetadataHybrid;
import edu.iastate.metnet.metaomgraph.chart.HistogramChart;
import edu.iastate.metnet.metaomgraph.chart.VolcanoPlot;
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
	private static final String COLUMN_HISTOGRAM_COMMAND = "column-histogram";
	private static final String VOLCANO_COMMAND = "volcano-plot";
	private static final String ADJ_VOLCANO_COMMAND = "adj-volcano-plot";
	private static final String LINE_CHART_DEFAULT_GROUPING_COMMAND = "line-chart-default-grouping";
	private static final String LINE_CHART_CHOOSE_GROUPING_COMMAND = "line-chart-choose-grouping";
	private static final String BAR_CHART_COMMAND = "bar-chart";
	private static final String CORRELATION_HISTOGRAM_COMMAND = "correlation-histogram";
	private static final String FILTER_COMMAND = "filter";
	private static final String SAMPLE_ACTION_PROPERTY = "Sample Action";
	private static final String SELECTED_FEATURES_PROPERTY = "Selected Features";
	private static final String INCLUDED_SAMPLES_PROPERTY = "Included Samples";
	private static final String EXCLUDED_SAMPLES_PROPERTY = "Excluded Samples";
	private static final String GROUPING_ATTRIBUTE_PROPERTY = "Grouping Attribute";
	private static final String SELECTED_COLUMN_PROPERTY = "Selected Column";
	private static final String CORRELATION_COLUMN_PROPERTY = "Correlation Column";
	private static final String PLAYABLE_PROPERTY = "Playable";
	private static final String TRANSFORMATION_PROPERTY = "Data Transformation";
	private static final String COMPUTE_PCA = "PCA";
	private static final String COMPUTE_TSNE = "t-SNE";
	private static final String HEAT_MAP = "heat map";


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

		try {
			for (TreePath path : allPaths) {
				DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) path.getLastPathComponent();
				Object nodeObj = node2.getUserObject();
				LoggingTreeNode ltn = (LoggingTreeNode) nodeObj;

				ActionProperties playedAction = allTabsInfo.get(tabNo).getActionObjects().get(ltn.getNodeNumber());
				
				if(ltn.getCommandName().equalsIgnoreCase(COMPUTE_PCA)) {
					Map<String, Object> dataMap = playedAction.getDataParameters();
					List<String> dataCol = (List<String>) dataMap.get("Selected samples");
					String selectedGeneList = (String)dataMap.get("Selected feature list");
					Boolean normalizeData = (Boolean)dataMap.get("Normalization");
					String[] selectedDataCols = new String[dataCol.size()];
					selectedDataCols = dataCol.toArray(selectedDataCols);
					MetaOmGraph.getActiveTablePanel().getMetadataTableDisplay().computePCA(selectedDataCols, selectedGeneList, normalizeData);
				}
				
				if(ltn.getCommandName().equalsIgnoreCase(COMPUTE_TSNE)) {
					Map<String, Object> dataMap = playedAction.getDataParameters();
					List<String> dataCol = (List<String>) dataMap.get("Selected samples");
					String selectedGeneList = (String)dataMap.get("Selected feature list");
					double perplexity = (Double)dataMap.get("Perplexity");
					double maxIter = (Double)dataMap.get("MaxIter");
					double theta = (Double)dataMap.get("Theta");
					Boolean usePCA = (Boolean)dataMap.get("UsePCA");
					Boolean parallel = (Boolean)dataMap.get("Parallel");
					String[] selectedDataCols = new String[dataCol.size()];
					selectedDataCols = dataCol.toArray(selectedDataCols);
					MetaOmGraph.getActiveTablePanel().getMetadataTableDisplay().computeTSNE(selectedDataCols, 
							selectedGeneList, perplexity, (int)maxIter, theta, usePCA, parallel);
				}

				if (ltn.getCommandName().equalsIgnoreCase(FILTER_COMMAND)) {
					Map<String, Object> dataMap = playedAction.getDataParameters();
					String[] filterStrings = (String[]) dataMap.get("Filter Strings");
					String filterText = "";
					for (String curr : filterStrings) {
						filterText += (curr + ";");
					}
					MetaOmGraph.getActiveTable().getFilterField().setText(filterText);
				}

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

								String [] samples = MetaOmGraph.getActiveProject().getDataColumnHeaders();
								LinkedList<String> allSamplesList = new LinkedList<String>(Arrays.asList(samples));
								LinkedList<String> copyAllSamplesList = new LinkedList<String>(Arrays.asList(samples));
								Object [] exclSamples = null;
								
								if(sampleAction.getOtherParameters().get(EXCLUDED_SAMPLES_PROPERTY) instanceof List<?> ) {
									List<Double> exclSamplesList2 = (List<Double>)sampleAction.getOtherParameters().get(EXCLUDED_SAMPLES_PROPERTY);
									exclSamples = exclSamplesList2.toArray();
								}
								else if(sampleAction.getOtherParameters().get(EXCLUDED_SAMPLES_PROPERTY) instanceof HashSet<?>) {
									HashSet<Double> exclSamplesList2 = (HashSet<Double>)sampleAction.getOtherParameters().get(EXCLUDED_SAMPLES_PROPERTY);
									exclSamples = exclSamplesList2.toArray();
								}
								else if(sampleAction.getOtherParameters().get(EXCLUDED_SAMPLES_PROPERTY) instanceof Integer[]) {
									exclSamples = (Object[])sampleAction.getOtherParameters().get(EXCLUDED_SAMPLES_PROPERTY);
								}
								
								
								for(int eindex = 0; eindex < exclSamples.length; eindex++) {
									
									int excludedindex = 0;
									if(exclSamples[eindex] instanceof Double) {
										Double a = (Double)exclSamples[eindex];
										excludedindex = a.intValue();
									}
									else if(exclSamples[eindex] instanceof Integer) {
										excludedindex = (int)exclSamples[eindex];
									}
									
									
									excludedSamples.add(copyAllSamplesList.get(excludedindex));
									try {
									allSamplesList.remove(copyAllSamplesList.get(excludedindex));
									}
									catch(Exception e) {
										StackTraceElement[] ste = e.getStackTrace();
									}
								}
								
								for(int iindex = 0; iindex < allSamplesList.size(); iindex++) {
									includedSamples.add(allSamplesList.get(iindex));
								}
								
								
								//urmi
								//break;

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
							} else if (ltn.getCommandName().equalsIgnoreCase(COLUMN_HISTOGRAM_COMMAND)) {
								playChart(playedAction, COLUMN_HISTOGRAM_COMMAND, includedSamples, excludedSamples);
							} else if (ltn.getCommandName().equalsIgnoreCase(LINE_CHART_DEFAULT_GROUPING_COMMAND)) {
								playChart(playedAction, LINE_CHART_DEFAULT_GROUPING_COMMAND, includedSamples, excludedSamples);
							} else if (ltn.getCommandName().equalsIgnoreCase(LINE_CHART_CHOOSE_GROUPING_COMMAND)) {
								playChart(playedAction, LINE_CHART_CHOOSE_GROUPING_COMMAND, includedSamples, excludedSamples);
							} else if (ltn.getCommandName().equalsIgnoreCase(BAR_CHART_COMMAND)) {
								playChart(playedAction, BAR_CHART_COMMAND, includedSamples, excludedSamples);
							} else if (ltn.getCommandName().equalsIgnoreCase(CORRELATION_HISTOGRAM_COMMAND)) {
								playChart(playedAction, CORRELATION_HISTOGRAM_COMMAND, includedSamples, excludedSamples);
							} else if (ltn.getCommandName().equalsIgnoreCase(HEAT_MAP)) {
								playChart(playedAction, HEAT_MAP, includedSamples, excludedSamples);
							} else if (ltn.getCommandName().equalsIgnoreCase(VOLCANO_COMMAND)) {
								playChart(playedAction, VOLCANO_COMMAND, includedSamples, excludedSamples);
							} else if (ltn.getCommandName().equalsIgnoreCase(ADJ_VOLCANO_COMMAND)) {
								playChart(playedAction, ADJ_VOLCANO_COMMAND, includedSamples, excludedSamples);
							}
						
					}
				}
			}
		}
		catch(Exception e) {

		}
	}


	/**
	 * This method plays the chart-type action by calling the respective chart's MetaOmTablePanel function that generates and displays the chart
	 * based on the selected features, the transformation used, and samples.
	 * 
	 * Sets the transformation variable of the system to the one in the log before resetting it.
	 * Before calling the MetaOmTablePanel functions, the selected features' ids are collected into an integer array, to be passed to them.
	 *
	 */
	public void playChart(ActionProperties chartAction, String chartName, HashSet<String> includedSamples, HashSet<String> excludedSamples) {

		
		Map<Object,Object> genes = null;
		String currentTransformation = MetaOmGraph.getTransform();
		int val2[] = null;
		try {

			//getting the selected features in the right format
			if(chartAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY) != null) {
				
				if(chartAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY) instanceof List<?> ) {
					List<Double> selFeaturesList = (List<Double>)chartAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY);
					Double[] selFeaturesDouble = new Double[selFeaturesList.size()];
					
					for( int fno = 0 ; fno < selFeaturesList.size(); fno++ ) {
						selFeaturesDouble[fno] = (Double)selFeaturesList.get(fno);
					}
					
					val2 = new int[selFeaturesDouble.length];
					
					for( int i =0; i< selFeaturesDouble.length; i++ ) {
						val2[i] = selFeaturesDouble[i].intValue();
					}
				}
				else if(chartAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY) instanceof HashSet<?>) {
					HashSet<Double> selFeaturesHashSet = (HashSet<Double>)chartAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY);
					Double[] selFeaturesDouble = new Double[selFeaturesHashSet.size()];
					
					selFeaturesDouble = selFeaturesHashSet.toArray(selFeaturesDouble);
					val2 = new int[selFeaturesDouble.length];
					
					for( int i =0; i< selFeaturesDouble.length; i++ ) {
						val2[i] = selFeaturesDouble[i].intValue();
					}
					
				}
				else if(chartAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY) instanceof Integer[]) {
					Integer[] selFeaturesIntArray = (Integer[])chartAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY);
					
					val2 = new int[selFeaturesIntArray.length];
					
					for( int i =0; i< selFeaturesIntArray.length; i++ ) {
						val2[i] = (int)selFeaturesIntArray[i];
					}
				}
				else if(chartAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY) instanceof Double[]) {
					Double[] selFeaturesDoubleArray = (Double[])chartAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY);
					
					val2 = new int[selFeaturesDoubleArray.length];
					
					for( int i =0; i< selFeaturesDoubleArray.length; i++ ) {
						val2[i] = selFeaturesDoubleArray[i].intValue();
					}
				}
				else {
					val2 = (int[])chartAction.getDataParameters().get(SELECTED_FEATURES_PROPERTY);
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

			//@Harsha set data transformation

			
			String historicalTransformation = (String) chartAction.getDataParameters().get(TRANSFORMATION_PROPERTY);
			MetaOmGraph.setTransform(historicalTransformation);

			
			//Run the chart
			MetaOmTablePanel mp = MetaOmGraph.getActiveTablePanel();

			if(chartName == LINE_CHART_COMMAND) {
				//for line chart
				mcol.setIncluded(includedSamples);
				mcol.setExcluded(excludedSamples);
				//update the excluded samples
				MetaOmAnalyzer.updateExcluded(excludedSamples, false);

				//make chart
				mp.graphSelectedRows(val2);

				//reset samples to current
				mcol.setIncluded(currentProjectIncludedSamples);
				mcol.setExcluded(currentProjectExcludedSamples);					
				MetaOmAnalyzer.updateExcluded(currentProjectExcludedSamples, false);
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
			else if (chartName == COLUMN_HISTOGRAM_COMMAND) {
				int nBins = 10;
				ColorUIResource oldActiveTitleBackground = (ColorUIResource) UIManager.get("InternalFrame.activeTitleBackground");
				ColorUIResource oldInactiveTitleBackground = (ColorUIResource) UIManager.get("InternalFrame.inactiveTitleBackground");
				Font oldFont = UIManager.getFont("InternalFrame.titleFont");
				UIManager.put("InternalFrame.activeTitleBackground", new ColorUIResource(new Color(240,128,128)));
				UIManager.put("InternalFrame.inactiveTitleBackground", new ColorUIResource(new Color(240,128,128)));
				UIManager.put("InternalFrame.titleFont", new Font("SansSerif", Font.BOLD,12));
				HistogramChart f = new HistogramChart(null, nBins, null, 2, (double[]) chartAction.getDataParameters().get("Data"), true);
				f.setTitle(chartAction.getDataParameters().get("Name") + " histogram");
				MetaOmGraph.getDesktop().add(f);
				f.setDefaultCloseOperation(2);
				f.setClosable(true);
				f.setResizable(true);
				f.pack();
				f.setSize(1000, 700);
				f.setVisible(true);
				f.toFront();
				UIManager.put("InternalFrame.activeTitleBackground", oldActiveTitleBackground);
				UIManager.put("InternalFrame.inactiveTitleBackground", oldInactiveTitleBackground);
				UIManager.put("InternalFrame.titleFont", oldFont);
			}
			else if (chartName == VOLCANO_COMMAND) {
				Map<String, Object> dataMap = chartAction.getDataParameters();
				ColorUIResource oldActiveTitleBackground = (ColorUIResource) UIManager.get("InternalFrame.activeTitleBackground");
				ColorUIResource oldInactiveTitleBackground = (ColorUIResource) UIManager.get("InternalFrame.inactiveTitleBackground");
				Font oldFont = UIManager.getFont("InternalFrame.titleFont");
				UIManager.put("InternalFrame.activeTitleBackground", new ColorUIResource(new Color(240,128,128)));
				UIManager.put("InternalFrame.inactiveTitleBackground", new ColorUIResource(new Color(240,128,128)));
				UIManager.put("InternalFrame.titleFont", new Font("SansSerif", Font.BOLD,12));
				VolcanoPlot f = new VolcanoPlot((List<String>) dataMap.get("Feature Names"), (List<Double>) dataMap.get("FC"), (List<Double>) dataMap.get("PV"), (String) dataMap.get("Name 1"), (String) dataMap.get("Name 2"), false);
				MetaOmGraph.getDesktop().add(f);
				f.setDefaultCloseOperation(2);
				f.setClosable(true);
				f.setResizable(true);
				f.pack();
				f.setSize(1000, 700);
				f.setVisible(true);
				f.toFront();
				UIManager.put("InternalFrame.activeTitleBackground", oldActiveTitleBackground);
				UIManager.put("InternalFrame.inactiveTitleBackground", oldInactiveTitleBackground);
				UIManager.put("InternalFrame.titleFont", oldFont);
			}
			else if (chartName == ADJ_VOLCANO_COMMAND) {
				Map<String, Object> dataMap = chartAction.getDataParameters();
				ColorUIResource oldActiveTitleBackground = (ColorUIResource) UIManager.get("InternalFrame.activeTitleBackground");
				ColorUIResource oldInactiveTitleBackground = (ColorUIResource) UIManager.get("InternalFrame.inactiveTitleBackground");
				Font oldFont = UIManager.getFont("InternalFrame.titleFont");
				UIManager.put("InternalFrame.activeTitleBackground", new ColorUIResource(new Color(240,128,128)));
				UIManager.put("InternalFrame.inactiveTitleBackground", new ColorUIResource(new Color(240,128,128)));
				UIManager.put("InternalFrame.titleFont", new Font("SansSerif", Font.BOLD,12));
				VolcanoPlot f = new VolcanoPlot((List<String>) dataMap.get("Feature Names"), (List<Double>) dataMap.get("FC"), (List<Double>) dataMap.get("APV"), (String) dataMap.get("Name 1"), (String) dataMap.get("Name 2"), true);
				MetaOmGraph.getDesktop().add(f);
				f.setDefaultCloseOperation(2);
				f.setClosable(true);
				f.setResizable(true);
				f.pack();
				f.setSize(1000, 700);
				f.setVisible(true);
				f.toFront();
				UIManager.put("InternalFrame.activeTitleBackground", oldActiveTitleBackground);
				UIManager.put("InternalFrame.inactiveTitleBackground", oldInactiveTitleBackground);
				UIManager.put("InternalFrame.titleFont", oldFont);
			}
			else if(chartName == LINE_CHART_DEFAULT_GROUPING_COMMAND) {
				//for line chart
				mcol.setIncluded(includedSamples);
				mcol.setExcluded(excludedSamples);
				//update the excluded samples
				MetaOmAnalyzer.updateExcluded(excludedSamples, false);

				mp.plotLineChartDefaultGrouping(val2);

				//reset samples to current
				mcol.setIncluded(currentProjectIncludedSamples);
				mcol.setExcluded(currentProjectExcludedSamples);
				MetaOmAnalyzer.updateExcluded(currentProjectExcludedSamples, false);
			}
			else if(chartName == LINE_CHART_CHOOSE_GROUPING_COMMAND) {
				//for line chart
				mcol.setIncluded(includedSamples);
				mcol.setExcluded(excludedSamples);
				//update the excluded samples
				MetaOmAnalyzer.updateExcluded(excludedSamples, false);

				String groupChosen = (String)chartAction.getDataParameters().get(GROUPING_ATTRIBUTE_PROPERTY);
				mp.plotLineChartChooseGrouping(val2, groupChosen);

				//reset samples to current;
				mcol.setIncluded(currentProjectIncludedSamples);
				mcol.setExcluded(currentProjectExcludedSamples);					
				MetaOmAnalyzer.updateExcluded(currentProjectExcludedSamples, false);

			}
			else if(chartName == BAR_CHART_COMMAND) {
				String columnChosen = (String)chartAction.getDataParameters().get(SELECTED_COLUMN_PROPERTY);
				mp.plotBarChart(columnChosen, true);
			}
			else if(chartName == CORRELATION_HISTOGRAM_COMMAND) {
				String correlationCol = (String)chartAction.getDataParameters().get(CORRELATION_COLUMN_PROPERTY);
				mp.plotCorrHist(correlationCol, true);
			}
			else if(chartName == HEAT_MAP) {
				//for line chart
				mcol.setIncluded(includedSamples);
				mcol.setExcluded(excludedSamples);
				//update the excluded samples
				MetaOmAnalyzer.updateExcluded(excludedSamples, false);

				mp.plotHeatMap(val2);

				//reset samples to current
				mcol.setIncluded(currentProjectIncludedSamples);
				mcol.setExcluded(currentProjectExcludedSamples);
				MetaOmAnalyzer.updateExcluded(currentProjectExcludedSamples, false);
			}


			//Setting back transformation to current transformation
			MetaOmGraph.setTransform(currentTransformation);

		}
		catch(Exception e) {
			MetaOmGraph.setTransform(currentTransformation);
			JOptionPane.showMessageDialog(null, "Failed to playback!!!" + e, "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();

		}
	}

}
