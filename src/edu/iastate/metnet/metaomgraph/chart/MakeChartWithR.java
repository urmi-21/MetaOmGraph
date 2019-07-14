package edu.iastate.metnet.metaomgraph.chart;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;


public class MakeChartWithR {

	private String pathtoRscripts = MetaOmGraph.getpathtoRscrips();
	private String pathtoR = MetaOmGraph.getRPath();

	public MakeChartWithR() {

	}

	/**
	 * save file and return full path of saved file
	 * 
	 * @param dataRows
	 * @param rowNames
	 * @param colNames
	 * @param fname
	 * @return
	 * @throws IOException
	 */
	public String saveDatatoFile(List<double[]> dataRows, String[] rowNames, String[] colNames, String fname)
			throws IOException {
		return saveDatatoFile(dataRows, rowNames, colNames, "", fname);
	}

	public String saveDatatoFile(List<double[]> dataRows, String[] rowNames, String[] colNames, String subDir,
			String fname) throws IOException {
		// directory same as where the project source files are
		String directory = MetaOmGraph.getActiveProject().getSourceFile().getParent();
		if (!subDir.equals("")) {
			directory = directory + System.getProperty("file.separator") + subDir;
		}
		// JOptionPane.showMessageDialog(null, "this dir:" + directory);
		String tempFilename = fname;
		tempFilename += "_R_Data.txt";
		// create dir if doesnt exist
		File dirFile = new File(directory);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		final File file = new File(directory + System.getProperty("file.separator") + tempFilename);
		
		new AnimatedSwingWorker("Working...", true) {
			@Override
			public Object construct() {
				EventQueue.invokeLater(new Runnable() {
					public void run() {

						// save to file as tab delimited and
						FileWriter fw;
						try {
							fw = new FileWriter(file);

							String header = "Name" + "\t" + String.join("\t", colNames);
							fw.write(header);
							for (int i = 0; i < dataRows.size(); i++) {
								String thisLine = "\n" + rowNames[i];
								double[] thisData = dataRows.get(i);
								for (int j = 0; j < thisData.length; j++) {
									thisLine += "\t" + thisData[j];

								}
								fw.write(thisLine);
							}
							fw.close();
							
							
							JOptionPane.showMessageDialog(null, "File saved:" + file.getAbsolutePath(), "File saved",
									JOptionPane.INFORMATION_MESSAGE);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, "Error while saving:" + file.getAbsolutePath(), "Error",
									JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
				});
				return null; 
			}
		}.start();

		return file.getAbsolutePath();
	}

	/**
	 * Make heat map using rscript.
	 * 
	 * @param datafilepath
	 *            data to plot
	 * @param chartSavename
	 *            filename to save as .png
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void makeHeatmap(String datafilepath, String chartSavename) throws IOException, InterruptedException {

		// JOptionPane.showMessageDialog(null, "This OS:"+MetaOmGraph.getOsName());
		String fileToSave = MetaOmGraph.getActiveProject().getSourceFile().getParent()
				+ System.getProperty("file.separator") + chartSavename + ".png";
		if (pathtoR == "") {
			JOptionPane.showMessageDialog(null, "Please set the path to \"Rscript\" in the project properties panel",
					"Rscript not found", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (pathtoRscripts == "") {
			JOptionPane.showMessageDialog(null,
					"Please set the path to the folder containin the R scripts to create the plot, in the project properties panel",
					"R files not found", JOptionPane.WARNING_MESSAGE);
			return;
		}
		// call rscript

		new AnimatedSwingWorker("Executing R script...", true) {
			@Override
			public Object construct() {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						Process pr = null;
						try {
							// copy scripts outside
							// JOptionPane.showMessageDialog(null, "rsc:" +
							// getClass().getResource("/resource/rscripts/makeHeatmap.R").toString().split("file:/")[1]);
							// JOptionPane.showMessageDialog(null, "rsc:"
							// +getClass().getResource("/resource/MetaOmicon.png").toString());

							pr = Runtime.getRuntime().exec(new String[] { pathtoR, pathtoRscripts + "/heatmap.R",
									datafilepath, fileToSave });
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (java.lang.NullPointerException npe) {
							JOptionPane.showMessageDialog(null, "1 Error while executing Rscript", "Error",
									JOptionPane.ERROR_MESSAGE);
							JOptionPane.showMessageDialog(null,
									"Please check the paths to R and R files in the project properties panel",
									"R files not found", JOptionPane.WARNING_MESSAGE);
						}
						int code = 0;
						try {
							code = pr.waitFor();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, "InterruptedException");
							e.printStackTrace();
						}
						switch (code) {
						case 0:
							// normal termination
							JOptionPane.showMessageDialog(null, "File saved:" + fileToSave, "File saved",
									JOptionPane.INFORMATION_MESSAGE);
							break;
						case 1:
							// error
							JOptionPane.showMessageDialog(null, "1 Error while executing Rscript", "Error",
									JOptionPane.ERROR_MESSAGE);
							JOptionPane.showMessageDialog(null,
									"Please check the paths to R and R files in the project properties panel",
									"R files not found", JOptionPane.WARNING_MESSAGE);
							return;
						default:
							JOptionPane.showMessageDialog(null, "Error while executing Rscript", "Error",
									JOptionPane.ERROR_MESSAGE);
							JOptionPane.showMessageDialog(null,
									"Please check the paths to R and R files in the project properties panel",
									"R files not found", JOptionPane.WARNING_MESSAGE);
							return;
						}
					}
				});
				return null;
			}
		}.start();

		return;
	}

	/**
	 * Function to execute a custom R script of the user
	 * 
	 * @param rScriptpath
	 *            path to R script to execute
	 * @param datafilepath
	 *            first argument to r script must be the datafile just created by
	 *            MOG. This is a matrix with features as rows and samples as columns
	 * @param arglist
	 *            Additional argument list to pass to the R script
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void runUserR(String rScriptpath, String datafilepath, String metadatafilepath, String outFileDir)
			throws IOException, InterruptedException {
		
		
		// out dir shoud be second argument to user script
		if (rScriptpath == "" || rScriptpath == null) {
			JOptionPane.showMessageDialog(null, "Invalid path to R script", "File not found",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		// call rscript
		new AnimatedSwingWorker("Executing R script...", true) {
			@Override
			public Object construct() {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						Process pr = null;
						try {
							pr = Runtime.getRuntime().exec(new String[] { pathtoR, rScriptpath, datafilepath, metadatafilepath, outFileDir });
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (java.lang.NullPointerException npe) {
							JOptionPane.showMessageDialog(null, "Error while executing Rscript", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
						int code = 0;
						try {
							code = pr.waitFor();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, "InterruptedException");
							// e.printStackTrace();
						}
						switch (code) {
						case 0:
							// normal termination
							JOptionPane.showMessageDialog(null, "File executed successfully", "Success",JOptionPane.INFORMATION_MESSAGE);
							//JOptionPane.showMessageDialog(null, "Arguments passed:"+pathtoR+" "+rScriptpath+" "+datafilepath+" "+metadatafilepath+ " "+outFileDir, "Error",JOptionPane.INFORMATION_MESSAGE);
							break;
						case 1:
							// error
							JOptionPane.showMessageDialog(null, "Error while executing Rscript.\nArguments passed:"+pathtoR+" "+rScriptpath+" "+datafilepath+" "+metadatafilepath+ " "+outFileDir, "Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
				});
				return null;
			}
		}.start();

		return;

	}

}
