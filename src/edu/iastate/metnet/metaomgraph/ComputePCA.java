/**
 * 
 */
package edu.iastate.metnet.metaomgraph;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dimensionalityreduction.PCA;
import org.nd4j.linalg.factory.Nd4j;

/**
 * @author sumanth
 *	
 * Class to compute the PCA
 * uses the library nd4j to compute the PCA
 */
public class ComputePCA {
	private INDArray dataArray;
	/**
	 * Constructor
	 * @param data 2d array with m rows(selected samples) and n columns(selected feature/gene list)
	 */
	public ComputePCA(double[][] data) {
		dataArray = Nd4j.createFromArray(data);
	}
	
	/**
     * Calculates pca reduced value of a matrix, for a given variance. A larger variance (99%)
     * will result in a higher order feature set.
     * The returned matrix is a projection of A onto principal components
     * 
     * If PCA is computed directly, the library is somehow modifying the original data, hence
     * compute the pca factors first and multiply the factors with the data to get the
     * reduced dimensions.
     *
     * @param variance the amount of variance to preserve as a float 0 - 1
     * @param normalize whether to normalize (set features to have zero mean)
     * @return the matrix representing  a reduced feature set
     */
	public double[][] projectData(double variance, boolean normalize){
		INDArray tempArr = dataArray.dup();
		INDArray pcaFactors = PCA.pca_factor(dataArray, variance, normalize);
		INDArray reducedDimensions = tempArr.mmul(pcaFactors);
		double[][] plotData = reducedDimensions.toDoubleMatrix();
		return plotData;
	}
	
	/**
     * Calculates pca vectors of a matrix, for a flags number of reduced features
     * returns the reduced feature set
     * The return is a projection of A onto principal nDims components
     *
     * If PCA is computed directly, the library is somehow modifying the original data, hence
     * compute the pca factors first and multiply the factors with the data to get the
     * reduced dimensions.
     *  
     * @param numOfDims the number of components on which to project the features 
     * @param normalize whether to normalize (set features to have zero mean)
     * @return the reduced parameters of the data
     */
	public double[][] projectData(int numOfDims, boolean normalize){
		INDArray tempArr = dataArray.dup();
		INDArray pcaFactors = PCA.pca_factor(dataArray, numOfDims, normalize);
		INDArray reducedDimensions = tempArr.mmul(pcaFactors);
		double[][] plotData = reducedDimensions.toDoubleMatrix();
		return plotData;
	}
}
