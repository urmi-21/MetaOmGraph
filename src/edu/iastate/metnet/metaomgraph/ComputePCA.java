/**
 * 
 */
package edu.iastate.metnet.metaomgraph;

import java.util.HashMap;
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
	INDArray dataArray;
	/**
	 * Constructor
	 * @param dataMap dataMap with key as the sample datacol index
	 */
	public ComputePCA(HashMap<Integer, double[]> dataMap) {
		convertData(dataMap);
	}
	
	// convert the data into INDArray format, which is an off-heap memory
	private void convertData(HashMap<Integer, double[]> dataMap) {
		double[][] data = getDataFromMap(dataMap);
		dataArray = Nd4j.createFromArray(data);
	}
	
	// get the data from map as 2Dimensional double array.
	private double[][] getDataFromMap(HashMap<Integer, double[]> dataMap){
		double[][] data = new double[dataMap.size()][];
		int rowIndex = 0;
		for(double[] rowData : dataMap.values()) {
			data[rowIndex++] =rowData;
		}
		return data;
	}
	
	/**
     * Calculates pca reduced value of a matrix, for a given variance. A larger variance (99%)
     * will result in a higher order feature set.
     *
     * The returned matrix is a projection of A onto principal components
     *
     * @param variance the amount of variance to preserve as a float 0 - 1
     * @param normalize whether to normalize (set features to have zero mean)
     * @return the matrix representing  a reduced feature set
     */
	public double[][] projectData(double variance, boolean normalize){
		INDArray pcaFactors = PCA.pca(dataArray, variance, normalize);
		double[][] plotData = pcaFactors.toDoubleMatrix();
		return plotData;
	}
	
	/**
     * Calculates pca vectors of a matrix, for a flags number of reduced features
     * returns the reduced feature set
     * The return is a projection of A onto principal nDims components
     *
     * @param numOfDims the number of components on which to project the features 
     * @param normalize whether to normalize (set features to have zero mean)
     * @return the reduced parameters of the data
     */
	public double[][] projectData(int numOfDims, boolean normalize){
		INDArray pcaFactors = PCA.pca(dataArray, numOfDims, normalize);
		double[][] plotData = pcaFactors.toDoubleMatrix();
		return plotData;
	}
}
