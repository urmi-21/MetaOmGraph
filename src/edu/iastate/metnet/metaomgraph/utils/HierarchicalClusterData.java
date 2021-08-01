/**
 * 
 */
package edu.iastate.metnet.metaomgraph.utils;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.chart.DendrogramChart;

/**
 * @author sumanth
 * Class to cluster the data
 */
public class HierarchicalClusterData {
	private List<String> clusteredDataNames;
	private Cluster parentCluster;
	
	/**
	 * Constructor
	 * @param names name/id for each row
	 * @param data the data that needs to be clustered
	 */
	public HierarchicalClusterData(String[] names, double[][] data) {
		this.clusteredDataNames = new ArrayList<String>();
		doClustering(data, names);
	}
	
	// do the heirarchical clustering
	private void doClustering(double[][] data, String[] names) {
		try {
			ClusteringAlgorithm algorithm = new DefaultClusteringAlgorithm();
			Cluster cluster = algorithm.performClustering(data, names, new AverageLinkageStrategy());
			parentCluster = cluster;
			fillClusteredOrderedDataFromChildren(cluster);
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Duplicate identifiers found, the feature names should be unique", "Clustering error", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	// Fill clustered data recursively
	private void fillClusteredOrderedDataFromChildren(Cluster cluster) {
		if(cluster.isLeaf()) {
			clusteredDataNames.add(cluster.getName());
		}
		for(Cluster child : cluster.getChildren()) {
			fillClusteredOrderedDataFromChildren(child);
		}
	}
	
	public void DisplayDendrogram() {
		DendrogramPanel dendrogramPanel = new DendrogramPanel();
		dendrogramPanel.setModel(parentCluster);
		dendrogramPanel.setShowDistances(false);
		dendrogramPanel.setShowScale(false);
		
		DendrogramChart dendrogramPlotFrame = new DendrogramChart(dendrogramPanel);
		MetaOmGraph.getDesktop().add(dendrogramPlotFrame);
		dendrogramPlotFrame.setDefaultCloseOperation(2);
		dendrogramPlotFrame.setClosable(true);
		dendrogramPlotFrame.setResizable(true);
		dendrogramPlotFrame.pack();
		dendrogramPlotFrame.setSize(1000, 700);
		dendrogramPlotFrame.setVisible(true);
		dendrogramPlotFrame.toFront();
	}
			
	/**
	 * Get the clustered data name/id in the order of clustering
	 * @return clusteredData
	 */
	public List<String> getClusteredOrderedData(){
		return clusteredDataNames;
	}

}
