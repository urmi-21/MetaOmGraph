/**
 * 
 */
package edu.iastate.metnet.metaomgraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.script.*;
import org.renjin.script.*;

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


	private static List<Double> getRecordFromLine(String line) {
		List<Double> values = new ArrayList<Double>();
		try (Scanner rowScanner = new Scanner(line)) {
			rowScanner.useDelimiter(",");
			while (rowScanner.hasNext()) {
				values.add(Double.parseDouble(rowScanner.next()));
			}
		}
		
		
		return values;
	}


	public static void main(String [] args) {

//		List<List<Double>> records = new ArrayList<>();
//		try (Scanner scanner = new Scanner(new File("/Users/harshavk/Documents/Harsha/Fall2021/Thesis/Human-Cancer-Prediction/X.csv"));) {
//			while (scanner.hasNextLine()) {
//				records.add(getRecordFromLine(scanner.nextLine()));
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		int rows = records.size();
//		int cols = records.get(0).size();
//
//		double[][] data = new double[rows][cols];
//
//		for(int i=0; i<rows; i++) {
//			for(int j=0; j<cols; j++) {
//				data[i][j] = records.get(i).get(j);
//			}
//		}
//
//		System.out.println("rows : "+rows +" , cols: "+cols);
//		ComputePCA cp = new ComputePCA(data);
//
//		double [][] output = cp.projectData(2, true);
//
//		try {
//			BufferedWriter br = new BufferedWriter(new FileWriter("/Users/harshavk/Documents/Harsha/Fall2021/Thesis/Human-Cancer-Prediction/pca-op.csv"));
//			StringBuilder sb = new StringBuilder();
//
//
//			for(int i=0; i<rows; i++) {
//				for(int j=0; j<2; j++) {
//					System.out.print(output[i][j]+ " ");
//					sb.append(output[i][j]);
//					if(j != cols-1)
//						sb.append(",");
//				}
//				sb.append("\n");
//				System.out.println();
//			}
//
//
//
//			br.write(sb.toString());
//			br.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}


		RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();
	    // create a Renjin engine:
	    ScriptEngine engine = factory.getScriptEngine();

	    try {
			engine.eval("df <- data.frame(x=1:10, y=(1:10)+rnorm(n=10))");
			engine.eval("print(df)");
			engine.eval("print(lm(y ~ x, df))");
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   

	}
}
