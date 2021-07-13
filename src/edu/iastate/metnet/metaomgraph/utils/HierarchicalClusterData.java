/**
 * 
 */
package edu.iastate.metnet.metaomgraph.utils;

import java.util.ArrayList;
import java.util.List;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;

/**
 * @author sumanth
 * Class to cluster the data
 */
public class HierarchicalClusterData {
	private List<String> clusteredDataNames;
	
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
		ClusteringAlgorithm algorithm = new DefaultClusteringAlgorithm();
		Cluster cluster = algorithm.performClustering(data, names, new AverageLinkageStrategy());
		fillClusteredOrderedDataFromChildren(cluster);
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
	
	/**
	 * Get the clustered data name/id in the order of clustering
	 * @return clusteredData
	 */
	public List<String> getClusteredOrderedData(){
		return clusteredDataNames;
	}

}
