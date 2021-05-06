/**
 * 
 */
package edu.iastate.metnet.metaomgraph;

import com.jujutsu.tsne.TSneConfiguration;
import com.jujutsu.tsne.barneshut.BHTSne;
import com.jujutsu.tsne.barneshut.BarnesHutTSne;
import com.jujutsu.tsne.barneshut.ParallelBHTsne;
import com.jujutsu.utils.TSneUtils;

/**
 * @author sumanth
 *
 */
public class ComputeTSNE {
	private BarnesHutTSne tsne;
	private TSneConfiguration tsneConfig;
	/**
	 * Constructor
	 * @param data 2d array with m rows(selected samples) and n columns(selected feature/gene list)
	 * @param perplexity denotes number of nearest neighbors
	 * @param maxIter maximum number of iterations
	 * @param theta barneshut tsne tradeoff parameter, 0-1, 0 represents normal tsne
	 * @param numDims number of output dimensions
	 * @param usePCA use pca
	 * @param parallel use parallel version or non parallel version.
	 */
	public ComputeTSNE(double[][] data, double perplexity, int maxIter, double theta,
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
	 * returns 2d array with m rows and 2 columns
	* @return the reduced parameters of the data 
	*/
	public double[][] projectData(){
		return tsne.tsne(tsneConfig);
	}
}
