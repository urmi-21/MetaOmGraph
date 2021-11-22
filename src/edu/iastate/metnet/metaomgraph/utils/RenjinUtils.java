/**
 * 
 */
package edu.iastate.metnet.metaomgraph.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.renjin.primitives.matrix.Matrix;
import org.renjin.script.RenjinScriptEngineFactory;
import org.renjin.sexp.Vector;

/**
 * @author sumanth
 * This class contains all the utilities related to Renjin (https://www.renjin.org/)
 */
public class RenjinUtils {
	
	/**
	 * Initiate and return the script engine for further usage.
	 * @return ScriptEngine
	 */
	public static ScriptEngine getScriptEngine() {
		// create a script engine manager:
	    RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();
	    // create a Renjin engine:
	    ScriptEngine engine = factory.getScriptEngine();
	    
	    return engine;
	}
	
	/**
	 * Fill the R's matrix using 2d array.
	 * this method uses and returns the same matrixVariableName passed to it
	 * @param matrixVariableName name of the matrix variable
	 * @param data 2D array data
	 * @param engine renjins scriptengine
	 * @return matrixVariableName the same variable name passed to the method
	 */
	public static String fillRMatrixFrom2DArray(final String matrixVariableName, 
			final double[][] data, final ScriptEngine engine) {
		engine.put(matrixVariableName, data[0]);
		try {
			engine.eval(matrixVariableName + " <- matrix(" + matrixVariableName + ", nr=1)");
			for(int i = 1; i <  data.length; i++) {
				engine.put("temp", data[i]);
				engine.eval(matrixVariableName + " <- rbind(" + matrixVariableName + ", matrix(temp, nr=1))"); 
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		
		return matrixVariableName;
	}
	
	/**
	 * Return the data in Vector form to 2D array.
	 * 
	 * Note: This method is exception prone, call this carefully only if you know that the vector is of matrix type,
	 * and by giving the proper number of rows and columns you require in the output data.
	 * 
	 * @param vector the vector datatype returned by r's script
	 * @param numOfRows num of rows in the return data type, should be less than or equal to the number of rows in vector
	 * @param numOfCols num of columns in the return data type, should be less than or equal to the number of columns in vector
	 * @return data 2D array
	 */
	public static double[][] get2DArrayFromRenjinVector(Vector vector, int numOfRows, int numOfCols){
		double[][] data = new double[numOfRows][numOfCols];
		Matrix rMatrix = new Matrix(vector);
		for(int i = 0; i < numOfRows; i++) {
			for(int j = 0; j < numOfCols; j++) {
				data[i][j] = rMatrix.getElementAsDouble(i, j);
			}
		}
		return data;
	}
}
