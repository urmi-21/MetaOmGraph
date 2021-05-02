/**
 * 
 */
package edu.iastate.metnet.metaomgraph;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

//import com.jujutsu.tsne.TSneConfiguration;
//import com.jujutsu.tsne.barneshut.BarnesHutTSne;
//import com.jujutsu.tsne.barneshut.ParallelBHTsne;
//import com.jujutsu.utils.TSneUtils;

/**
 * @author sumanth
 *
 */
public class ComputeTSNE {
	private INDArray dataArray;
	private double[][] data;
	private double perplexity;
	private int maxIter;
	private double learningRate;
	private int numDims;
	private boolean normalize;
	private boolean usePCA;
	/**
	 * Constructor
	 * @param dataMap dataMap with key as the sample datacol index
	 */
	public ComputeTSNE(double[][] data, double perplexity, int maxIter, double learningRate,
			int numDims, boolean normalize, boolean usePCA) {
		this.data = data;
		this.dataArray = Nd4j.createFromArray(data);
		this.perplexity = perplexity;
		this.maxIter = maxIter;
		this.learningRate = learningRate;
		this.numDims = numDims;
		this.normalize = normalize;
		this.usePCA = usePCA;
	}
	
	// convert the data into INDArray format, which is an off-heap memory
	private void convertData(LinkedHashMap<Integer, double[]> dataMap) {
		this.data = getDataFromMap(dataMap);
		dataArray = Nd4j.createFromArray(data);
	}
	
	// get the data from map as 2Dimensional double array.
	private double[][] getDataFromMap(LinkedHashMap<Integer, double[]> dataMap){
		double[][] data = new double[dataMap.size()][];
		int rowIndex = 0;
		for(double[] rowData : dataMap.values()) {
			data[rowIndex++] =rowData;
		}
		return data;
	}
	private double[][] reduceDims(INDArray arr){
		return arr.getColumns(0, 1).toDoubleMatrix();
	}
	
	/**
     *  
     * @param numOfDims the number of components on which to project the features 
     * @param normalize whether to normalize (set features to have zero mean)
     * @return the reduced parameters of the data
     */
	public double[][] projectData(String[] selectedDataCols){
		
//		Tsne tsneTemp = new Tsne.Builder().build();
//		double[][] data = tsneTemp.calculate(dataArray, 2, 30).toDoubleMatrix();
//		BarnesHutTSne tsne = new ParallelBHTsne();
//		TSneConfiguration config = TSneUtils.buildConfig(data, 2, data.length, perplexity, maxIter);
//		//TSneConfiguration config = TSneUtils.buildConfig(data, 2, data.length, perplexity, maxIter, true, 0.5, true);
//		double[][] redDims = tsne.tsne(config);
		return null;
//		INDArray tempArr = dataArray.dup();
//		BarnesHutTsne tsne = new BarnesHutTsne.Builder()
//				.setMaxIter(maxIter)
//		        .useAdaGrad(false)
//		        .theta(0.5)
//		        .normalize(normalize)
//		        .staticInit(dataArray)
//		        .build();
//		
//		tsne.setPerplexity(perplexity);
//		tsne.setLearningRate(learningRate);
//		//tsne.setNumDimensions(numDims);
//		tsne.setUsePca(usePCA);
//		tsne.fit(tempArr);
//		String outputFile = "D://test.csv";
//        try {
//			tsne.plot(tempArr,2,Arrays.asList(selectedDataCols),outputFile);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		INDArray tsneConvData = tsne.getData();
//		double[][] test = reduceDims(tsneConvData);
//		double[][] data = tsne.getData().toDoubleMatrix();
//		return test;
	}
}
