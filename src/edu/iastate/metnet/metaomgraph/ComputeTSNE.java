/**
 * 
 */
package edu.iastate.metnet.metaomgraph;

import javax.swing.JOptionPane;

import com.jujutsu.tsne.TSne;
import com.jujutsu.tsne.TSneConfiguration;
import com.jujutsu.tsne.barneshut.BHTSne;
import com.jujutsu.tsne.barneshut.BarnesHutTSne;
import com.jujutsu.tsne.barneshut.ParallelBHTsne;
import com.jujutsu.utils.TSneUtils;

/**
 * @author sumanth
 * Singleton class 
 * get the object by calling getTsneInstance()
 */
public class ComputeTSNE {
	private static BarnesHutTSne tsne;
	private static TSneConfiguration tsneConfig;
	
	private static ComputeTSNE tsneInstance = null;
	
	// make it singleton
	private ComputeTSNE() {}
	
	// Singleton class
	private ComputeTSNE(double[][] data, double perplexity, int maxIter, double theta,
			int numDims, boolean usePCA, boolean parallel) {
		if(parallel) {
			tsne = new ParallelBHTsne();
		} else {
			tsne = new BHTSne();
		}

		tsneConfig = 
				TSneUtils.buildConfig(data, numDims, data.length, perplexity, maxIter, usePCA, theta, true);
	}
	
	/**
	 * returns the singleton instance of the ComputeTSNE class
	 * @param data 2d array with m rows(selected samples) and n columns(selected feature/gene list)
	 * @param perplexity denotes number of nearest neighbors
	 * @param maxIter maximum number of iterations
	 * @param theta barneshut tsne tradeoff parameter, 0-1, 0 represents normal tsne
	 * @param numDims number of output dimensions
	 * @param usePCA use pca
	 * @param parallel use parallel version or non parallel version.
	 * @return ComputeTSNE object
	 */
	public static ComputeTSNE getTsneInstance(double[][] data, double perplexity, int maxIter, double theta,
			int numDims, boolean usePCA, boolean parallel) {
		if(tsneInstance == null) {
			tsneInstance = new ComputeTSNE(data, perplexity, maxIter, theta, numDims, usePCA, parallel);
		}
		return tsneInstance;
	}
	
	/**
	 * public method to garbage collect tsne
	 * 
	 */
	public static void abortTsne() {
		if(tsneInstance != null) {
			tsne.abort();
			tsne = null;
			tsneConfig = null;
			tsneInstance = null;
		}
	}
	
	/**
	 * returns 2d array with m rows and 2 columns
	 * @return the reduced parameters of the data 
	*/
	public double[][] projectData(){
		try {
			return tsne.tsne(tsneConfig);
		}
		catch(IllegalArgumentException badArguments) {
			abortTsne();
			String error = badArguments.getMessage();
			if(error.contains("Perplexity too large")) {
				String errorMessage = error;
				errorMessage += "Please set it low, check the user guide for optimal values";
				JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), errorMessage, "Tsne error", JOptionPane.ERROR_MESSAGE);
			}
			else if(error.contains("theta")) {
				String errorMessage = "Please set theta more than 0.0";
				JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), errorMessage, "Tsne error", JOptionPane.ERROR_MESSAGE);
			}
		}
		catch(Exception e) {
			abortTsne();
			e.printStackTrace();
		}
		return null;
	}
}
