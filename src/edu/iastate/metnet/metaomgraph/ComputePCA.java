/**
 * 
 */
package edu.iastate.metnet.metaomgraph;

import org.renjin.primitives.matrix.Matrix;
import org.renjin.script.*;
import org.renjin.sexp.Vector;

import edu.iastate.metnet.metaomgraph.utils.RenjinUtils;

import javax.script.*;

/**
 * @author sumanth
 *	
 * Class to compute the PCA
 * uses the R packages to compute the PCA
 */
public class ComputePCA {
	private double[][] data;
	/**
	 * Constructor
	 * @param data 2d array with m rows(selected samples) and n columns(selected feature/gene list)
	 */
	public ComputePCA(double[][] data) {
		this.data = data;
	}
	
	/**
     * Calculates pca vectors of the given matrix by reducing it into the number of dimensions.
     * @param numOfDims the number of components on which to project the features 
     * @param normalize whether to normalize (set features to have zero mean)
     * @return the reduced parameters of the data
     */
	public double[][] projectData(int numOfDims, boolean normalize){
		ScriptEngine engine = RenjinUtils.getScriptEngine();
		
		String dataRMatrix = RenjinUtils.fillRMatrixFrom2DArray("dataRMatrix", data, engine);
		String center = "TRUE";
		if(!normalize) {
			center = "FALSE";
		}
		Vector pcaRVector = null;
		try {
			engine.eval("pca <- prcomp(" + dataRMatrix + ", scale. = TRUE, center = " + center + ")");
			pcaRVector = (Vector)engine.eval("pca$x");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		
		double[][] plotData = RenjinUtils.get2DArrayFromRenjinVector(pcaRVector, data.length, numOfDims);

		return plotData;
	}
}
