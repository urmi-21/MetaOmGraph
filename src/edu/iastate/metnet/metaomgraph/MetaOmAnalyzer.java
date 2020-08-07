package edu.iastate.metnet.metaomgraph;

import edu.iastate.metnet.metaomgraph.ui.BlockingProgressDialog;
import edu.iastate.metnet.metaomgraph.ui.ClearableTextField;
import edu.iastate.metnet.metaomgraph.ui.DualTablePanel;
import edu.iastate.metnet.metaomgraph.ui.TreeSearchQueryConstructionPanel;
import edu.iastate.metnet.metaomgraph.utils.Utils;
import edu.iastate.metnet.metaomgraph.utils.qdxml.SimpleXMLElement;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.TableModel;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jdom.Element;

public class MetaOmAnalyzer {
	public static final int PEARSON = 1;
	public static final int SPEARMAN = 2;
	public static final int CANBERRA = 3;
	public static final int EUCLIDEAN = 4;
	public static final int MANHATTAN = 5;
	public static final int WEIGHTED_EUCLIDEAN = 6;
	public static final int WEIGHTED_MANHATTAN = 7;
	private static boolean[] exclude;
	private static int excludeCount = 0;

	private static String excludeName;

	public MetaOmAnalyzer() {
	}

	/**
	 * @author urmi Update the exclude list to remove excluded data cols from charts
	 * @param excluded
	 *            list of excluded data cols
	 */
	public static void updateExcluded(Set<String> excluded) {
		if (excluded.size() == 0) {
			MetaOmAnalyzer.exclude = null;
			MetaOmAnalyzer.excludeCount = 0;
			MetaOmGraph.fixTitle();
			return;
		} else {

			if (MetaOmAnalyzer.exclude == null) {
				MetaOmAnalyzer.exclude = new boolean[MetaOmGraph.getActiveProject().getDataColumnCount()];

			}
			for (int i = 0; i < MetaOmAnalyzer.exclude.length; i++) {
				MetaOmAnalyzer.exclude[i] = false;
			}
		}
		int excount = 0;
		String[] dataCols = MetaOmGraph.getActiveProject().getDataColumnHeaders();
		for (int j = 0; j < dataCols.length; j++) {
			if (excluded.contains(dataCols[j])) {
				exclude[j] = true;
				excount++;
			}
		}
		// JOptionPane.showMessageDialog(null, "datacols"+Arrays.toString(dataCols));
		// JOptionPane.showMessageDialog(null, "exclude
		// datacols"+Arrays.toString(exclude));

		MetaOmAnalyzer.excludeCount = excount;
		MetaOmGraph.fixTitle();

		return;
	}

	/**
	 * @author Urmi execute computations using multi-threaded class
	 * @param project
	 * @param geneList
	 * @param row
	 * @param name
	 * @param method
	 * @throws IOException
	 */
	public static void doComputation(final MetaOmProject project, String geneList, final int row, final String name,
			final int method) throws IOException {
		int[] entries = project.getGeneListRowNumbers(geneList);
		Number[] result = null;
		double[] sourceData = project.getIncludedData(entries[row]);
		if (MetaOmAnalyzer.exclude != null) {
			for (int i = 0; i < sourceData.length; i++) {
				if (MetaOmAnalyzer.exclude[i]) {
					sourceData[i] = Double.NaN;
				}
			}
		}

		JOptionPane.showMessageDialog(null, "making list");
		List<double[]> fullData = new ArrayList<>();
		fullData = project.getAllData();
		/*
		 * for (int i = 0; i < entries.length; i++) { double[] data =
		 * project.getIncludedData(entries[i]); fullData.add(data); }
		 */

		JOptionPane.showMessageDialog(null, "making list Done");

		CorrelationMultiThreaded ob = new CorrelationMultiThreaded(fullData, sourceData, 0);
		// JOptionPane.showMessageDialog(null, "running corr");
		// Number [] result = null;
		try {
			result = ob.compute();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// JOptionPane.showMessageDialog(null, "Done running corr");
		project.setLastCorrelation(result, name);

	}

	public static void doAnalysis(final MetaOmProject project, String geneList, final int row, final String name,
			final int method) throws IOException {
		if ((method < 1) || (method > 8)) {
			throw new IllegalArgumentException("Invalid method");
		}
		final int[] entries = project.getGeneListRowNumbers(geneList);
		final Number[] result = new Number[project.getRowCount()];
		// final Number[] result;
		String message = project.getRowName(entries[row])[project.getDefaultColumn()] + "";
		
		if (method == 1) {
			message = message + " - Pearson Correlation";
		} else if (method == 2) {
			message = message + " - Spearman Correlation";
		} else if (method == 3) {
			message = message + " - Canberra Distance";
		} else if (method == 4) {
			message = message + " - Euclidean Distance";
		} else if (method == 5) {
			message = message + " - Manhattan Distance";
		} else if (method == 6) {
			message = message + " - Weighted Euclidean Distance";
		} else if (method == 7) {
			message = message + " - Weighted Manhattan Distance";
		} else if (method == 8) {
			message = message + " - Pearson Correlation2";
		}
		final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(), "Analyzing...",
				message, 0L, entries.length, true);
		SwingWorker analyzeWorker = new SwingWorker() {
			boolean errored = false;

			@Override
			public Object construct() {
				try {
					// source data is selected data row
					double[] sourceData = project.getIncludedData(entries[row]);
					/*
					 * double[] sourceData = project.getAllData(entries[row]); if
					 * (MetaOmAnalyzer.exclude != null) { for (int i = 0; i < sourceData.length;
					 * i++) { if (MetaOmAnalyzer.exclude[i]) { sourceData[i] = Double.NaN; } } }
					 */

					CorrelationCalc calcy = new CorrelationCalc(sourceData, MetaOmAnalyzer.exclude);

					int i = 0;
					do {
						progress.setProgress(i);
						// double[] data = project.getAllData(entries[i]);
						double[] data = project.getIncludedData(entries[i]);
						// JOptionPane.showMessageDialog(null, "data:"+Arrays.toString(data));
						if (method == 2) {
							// urmi now using apache maths' SpearmansCorrelation
							result[entries[i]] = new CorrelationValue(calcy.newSpearmanCorrelation(data));
						} else if (method == 1) {

							result[entries[i]] = new CorrelationValue(calcy.pearsonCorrelation(data,
									project.mayContainBlankValues(), project.getBlankValue()));
						} else if (method == 3) {
							CorrelationValue addMe = new CorrelationValue(calcy.canberraDistance(data,
									project.mayContainBlankValues(), project.getBlankValue()));
							addMe.setAsPercent(false);
							result[entries[i]] = addMe;
						} else if (method == 4) {
							CorrelationValue addMe = new CorrelationValue(calcy.euclideanDistance(data,
									project.mayContainBlankValues(), project.getBlankValue()));
							addMe.setAsPercent(false);
							result[entries[i]] = addMe;
						} else if (method == 5) {
							CorrelationValue addMe = new CorrelationValue(calcy.manhattanDistance(data,
									project.mayContainBlankValues(), project.getBlankValue()));
							addMe.setAsPercent(false);
							result[entries[i]] = addMe;
						} else if (method == 6) {
							CorrelationValue addMe = new CorrelationValue(calcy.weightedEuclideanDistance(data,
									project.mayContainBlankValues(), project.getBlankValue()));
							addMe.setAsPercent(false);
							result[entries[i]] = addMe;
						} else if (method == 7) {
							CorrelationValue addMe = new CorrelationValue(calcy.weightedManhattanDistance(data,
									project.mayContainBlankValues(), project.getBlankValue()));
							addMe.setAsPercent(false);
							result[entries[i]] = addMe;

						}
						i++;
						if (i >= entries.length)
							break;
					} while (!progress.isCanceled());

				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Error reading project data",
							"IOException", 0);
					ioe.printStackTrace();
					progress.dispose();
					errored = true;
					return null;
				} catch (ArrayIndexOutOfBoundsException oob) {
					progress.dispose();
					errored = true;
					return null;
				}
				return null;
			}

			@Override
			public void finished() {
				if ((!progress.isCanceled()) && (!errored)) {
					project.setLastCorrelation(result, name);
				}
				progress.dispose();
			}
		};
		analyzeWorker.start();
		progress.setVisible(true);
	}

	public static void pairwiseMI(final MetaOmProject project, String geneList, final int nameCol, int bins, int kFinal,
			double[] knotVec, boolean relatedness) throws IOException {
		// get destination file name
		File dest = Utils.chooseFileToSave(Utils.createFileFilter("txt", "Tab-delimited text files"), "txt",
				MetaOmGraph.getMainWindow(), true);
		if (dest == null) {
			return;
		}
		final int[] entries = project.getGeneListRowNumbers(geneList);

		String message = "<html>0/" + entries.length + "<br>Preparing...</html>";
		final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(), "Analyzing...",
				message, 0L, entries.length, true);

		final Object[][] rowNames = project.getRowNames(entries);
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		BufferedWriter out2 = null;

		try {
			// out = new BufferedWriter(new FileWriter(dest));
			if (nameCol >= 0) {
				out.write(project.getInfoColumnNames()[nameCol] + "\t" + project.getInfoColumnNames()[nameCol]
						+ "\tMutual Information\r\n");
			} else {
				out.write("Row Number\tRow Number\tMutual Information\r\n");

			}
		} catch (IOException ioe) {

			ioe.printStackTrace();
			return;
		}
		long sTime = System.nanoTime();
		SwingWorker analyzeWorker = new SwingWorker() {
			boolean errored = false;
			ArrayList<double[]> dataBuffer = new ArrayList();

			@Override
			public Object construct() {
				try {
					// to save relatedness
					// double[][] relatednessMat = null;
					double[][] miMat = null;
					if (relatedness) {
						// relatednessMat = new double[entries.length][entries.length];
						miMat = new double[entries.length][entries.length];
					}
					DecimalFormat format = new DecimalFormat("0.0000");
					long aveTime = -1L;
					long correlationTime = 0L;
					long aveCorrTime = -1L;
					long aveTimeLeft = -1L;
					long timeStarted = System.nanoTime();
					String timeString = "Preparing...";
					for (int row = 0; (row < entries.length) && (!progress.isCanceled()); row++) {
						double[] sourceData = project.getIncludedData(entries[row]);
						// calculate entropy for this outer row
						ComputeDensityFromSpline cdOb1 = new ComputeDensityFromSpline(sourceData, bins, kFinal,
								knotVec);
						double[][] targetwtMat = cdOb1.getWeightMatrix();
						double targetH = cdOb1.getEntropy(targetwtMat);

						/*
						 * urmi not required funtion getIncludedData returns only included data if
						 * (MetaOmAnalyzer.exclude != null) { for (int i = 0; i < sourceData.length;
						 * i++) { if (MetaOmAnalyzer.exclude[i]) { sourceData[i] = Double.NaN; } } }
						 */

						// CorrelationCalc calcy = new CorrelationCalc(sourceData,
						// MetaOmAnalyzer.exclude);

						if (row == 1) {
							timeString = "Estimating time remaining...";
						}
						if ((row == 5) || ((row % 10 == 0) && (row != 0))) {
							int rowsLeft = entries.length - row;
							int corrLoss = (int) ((rowsLeft + 1) * (rowsLeft / 2.0D));

							long newAveTime = (System.nanoTime() - timeStarted) / row;
							if (aveTime < 0L) {
								aveTime = newAveTime;
							} else {
								aveTime = (aveTime + newAveTime) / 2L;
							}

							long timeLeft = aveTime * rowsLeft - corrLoss * aveCorrTime;
							long corrsLeft = entries.length * rowsLeft - corrLoss;
							long corrTimeLeft = corrsLeft * aveCorrTime;

							if (aveTimeLeft < 0L) {
								aveTimeLeft = corrTimeLeft;
							} else {
								aveTimeLeft = (aveTimeLeft + corrTimeLeft) / 2L;
							}
							timeString = Utils.convertNanoTimeToWords(aveTimeLeft) + " remaining";
						}
						progress.setMessage("<html>" + row + "/" + entries.length + "<br>" + timeString + "</html>");
						progress.setProgress(row);
						long bufferSize = -1L;
						long corrStart = System.nanoTime();
						// inner loop
						for (int i = row; (i < entries.length) && (!progress.isCanceled()); i++) {
							double[] data;

							if ((row > 0) && (i >= entries.length - dataBuffer.size())) {
								data = dataBuffer.get(i - (entries.length - dataBuffer.size()));
							} else {
								data = project.getIncludedData(entries[i]);
							}

							if ((row == 0) && (bufferSize < 0L)) {
								//System.gc();
								
								long freemem = Runtime.getRuntime().freeMemory();
								long maxmem = Runtime.getRuntime().maxMemory();
								long totmem = Runtime.getRuntime().totalMemory();
								// System.out.println("Max memory: " + maxmem);
								// System.out.println("Tot memory: " + totmem);
								// System.out.println("Free memory: " + freemem);
								long usablemem = maxmem - totmem - freemem;
								// System.out.println("Usable mem: " + usablemem);
								long arraySize = Utils.calcArraySize(data);
								// System.out.println("Array size: " + arraySize);
								bufferSize = usablemem / (Utils.calcArraySize(data) + 1024L);
								if (bufferSize > entries.length) {
									bufferSize = entries.length;
								}
								System.out.println("Buffering " + bufferSize + " rows");
							}

							if ((row == 0) && (i >= entries.length - bufferSize)) {
								dataBuffer.add(data);
							}
							if (nameCol >= 0) {
								out.write(rowNames[row][nameCol] + "\t" + rowNames[i][nameCol]);
							} else {
								out.write(row + "\t" + i);
							}
							double val = 0;
							// calculate entropy for this outer row
							ComputeDensityFromSpline cdOb2 = new ComputeDensityFromSpline(data, bins, kFinal, knotVec);
							double[][] thiswtMat = cdOb2.getWeightMatrix();
							double thisH = cdOb2.getEntropy(thiswtMat);
							double thisJointH = cdOb2.getJointEntropy(targetwtMat, thiswtMat);
							val = targetH + thisH - thisJointH;

							if (relatedness) {
								miMat[row][i] = val;
								miMat[i][row] = val;
							}

							out.write("\t" + format.format(val));
							out.write("\r\n");
							if ((i == entries.length - 1) && (row != 0)) {
								correlationTime = (System.nanoTime() - corrStart) / entries.length;
								if (aveCorrTime < 0L) {
									aveCorrTime = correlationTime;
								} else {
									aveCorrTime = (aveCorrTime + correlationTime) / 2L;
								}
							}
						}

						if (row == 0) {
							System.out.println(dataBuffer.size() + " rows buffered.");
							timeStarted = System.nanoTime();
						}
					}

					long timeTaken = System.nanoTime() - timeStarted;
					System.out.println("Took " + Utils.convertNanoTimeToWords(timeTaken, true));

					if (relatedness) {
						double[] mean = new double[entries.length];
						double[] sd = new double[entries.length];

						// Add the data from the array
						for (int i = 0; i < entries.length; i++) {
							DescriptiveStatistics temp = new DescriptiveStatistics();
							for (int j = 0; j < entries.length; j++) {
								temp.addValue(miMat[i][j]);
							}
							mean[i] = temp.getMean();
							sd[i] = temp.getStandardDeviation();
						}

						// calculate only upper matrix
						/*
						 * for (int i = 0; i < entries.length; i++) { for (int j = i; j <
						 * entries.length; j++) { double zi = (miMat[i][j] - mean[i]) / sd[i]; double zj
						 * = (miMat[i][j] - mean[j]) / sd[j]; relatednessMat[i][j] = Math.sqrt((zi * zi)
						 * + (zj * zj)); relatednessMat[j][i] = relatednessMat[i][j]; }
						 * 
						 * }
						 */

						// save relatedness
						File dest2 = new File(dest.getAbsolutePath().split(".txt")[0] + "_relatedness.txt");
						if (dest2 == null) {
							return null;
						}
						BufferedWriter out2 = new BufferedWriter(new FileWriter(dest2));
						try {
							// out = new BufferedWriter(new FileWriter(dest));
							if (nameCol >= 0) {
								out2.write(project.getInfoColumnNames()[nameCol] + "\t"
										+ project.getInfoColumnNames()[nameCol] + "\tRelatedness\r\n");
							} else {
								out2.write("Row Number\tRow Number\tRelatedness\r\n");

							}

							// calculate only upper matrix
							for (int i = 0; i < entries.length; i++) {
								for (int j = i; j < entries.length; j++) {
									if (nameCol >= 0) {
										out2.write(rowNames[i][nameCol] + "\t" + rowNames[j][nameCol]);
									} else {
										out2.write(i + "\t" + i);
									}

									double zi = (miMat[i][j] - mean[i]) / sd[i];
									double zj = (miMat[i][j] - mean[j]) / sd[j];
									if (zi < 0) {
										zi = 0;
									}
									if (zj < 0) {
										zj = 0;
									}
									double val = Math.sqrt((zi * zi) + (zj * zj));
									out2.write("\t" + format.format(val));
									out2.write("\r\n");
								}

							}
							out2.close();

						} catch (IOException ioe) {

							ioe.printStackTrace();
							return null;
						}
					}
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Error reading project data",
							"IOException", 0);
					ioe.printStackTrace();
					progress.dispose();
					errored = true;
					return null;
				} catch (ArrayIndexOutOfBoundsException oob) {
					progress.dispose();
					errored = true;
					return null;
				}
				return null;
			}

			@Override
			public void finished() {
				try {
					out.close();
					JOptionPane.showMessageDialog(null, "File " + dest.getAbsolutePath() + " saved!",
							"Results saved to file", JOptionPane.INFORMATION_MESSAGE);

				} catch (IOException e) {
					e.printStackTrace();
				}
				progress.dispose();
				// JOptionPane.showMessageDialog(null, "File:"+dest.getAbsolutePath()+"
				// saved.");
			}
		};
		analyzeWorker.start();
		progress.setVisible(true);
		// long endTime = System.nanoTime();
		// long duration = (endTime - sTime);
		// JOptionPane.showMessageDialog(null, "Time:"+duration/1000000000);

	}

	public static void pairwise(final MetaOmProject project, String geneList, final int nameCol, final int method)
			throws IOException {
		if ((method != 1) && (method != 2)) {
			throw new IllegalArgumentException("Invalid method");
		}
		File dest = Utils.chooseFileToSave(Utils.createFileFilter("txt", "Tab-delimited text files"), "txt",
				MetaOmGraph.getMainWindow(), true);
		if (dest == null) {
			return;
		}
		final int[] entries = project.getGeneListRowNumbers(geneList);
		String message = "<html>0/" + entries.length + "<br>Preparing...</html>";
		final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(), "Analyzing...",
				message, 0L, entries.length, true);

		final Object[][] rowNames = project.getRowNames(entries);
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		try {
			// out = new BufferedWriter(new FileWriter(dest));
			if (nameCol >= 0) {
				out.write(project.getInfoColumnNames()[nameCol] + "\t" + project.getInfoColumnNames()[nameCol]
						+ "\tCorrelation\r\n");
			} else {
				out.write("Row Number\tRow Number\tCorrelation\r\n");

			}
		} catch (IOException ioe) {

			ioe.printStackTrace();
			return;
		}

		SwingWorker analyzeWorker = new SwingWorker() {
			boolean errored = false;
			ArrayList<double[]> dataBuffer = new ArrayList();

			@Override
			public Object construct() {
				try {
					DecimalFormat format = new DecimalFormat("0.0000");
					long aveTime = -1L;
					long correlationTime = 0L;
					long aveCorrTime = -1L;
					long aveTimeLeft = -1L;
					long timeStarted = System.nanoTime();
					String timeString = "Preparing...";
					for (int row = 0; (row < entries.length) && (!progress.isCanceled()); row++) {
						
						double[] sourceData = project.getIncludedData(entries[row]);
						

						CorrelationCalc calcy = new CorrelationCalc(sourceData, MetaOmAnalyzer.exclude);

						if (row == 1) {
							timeString = "Estimating time remaining...";
						}
						if ((row == 5) || ((row % 10 == 0) && (row != 0))) {

							int rowsLeft = entries.length - row;
							int corrLoss = (int) ((rowsLeft + 1) * (rowsLeft / 2.0D));

							long newAveTime = (System.nanoTime() - timeStarted) / row;
							if (aveTime < 0L) {
								aveTime = newAveTime;
							} else {
								aveTime = (aveTime + newAveTime) / 2L;
							}

							long timeLeft = aveTime * rowsLeft - corrLoss * aveCorrTime;
							long corrsLeft = entries.length * rowsLeft - corrLoss;
							long corrTimeLeft = corrsLeft * aveCorrTime;

							if (aveTimeLeft < 0L) {
								aveTimeLeft = corrTimeLeft;
							} else {
								aveTimeLeft = (aveTimeLeft + corrTimeLeft) / 2L;
							}
							timeString = Utils.convertNanoTimeToWords(aveTimeLeft) + " remaining";
						}
						progress.setMessage("<html>" + row + "/" + entries.length + "<br>" + timeString + "</html>");
						progress.setProgress(row);
						long bufferSize = -1L;
						long corrStart = System.nanoTime();
						for (int i = row; (i < entries.length) && (!progress.isCanceled()); i++) {
							double[] data;

							if ((row > 0) && (i >= entries.length - dataBuffer.size())) {
								data = dataBuffer.get(i - (entries.length - dataBuffer.size()));
								//JOptionPane.showMessageDialog(null, "buff size:"+dataBuffer.size());
							} else {
								//long st = System.currentTimeMillis();
								
								data = project.getIncludedData(entries[i]);
								//long spt = System.currentTimeMillis();
								//long elapsedTime = spt - st;
								//JOptionPane.showMessageDialog(null, "Time taken:"+elapsedTime);
							}

							if ((row == 0) && (bufferSize < 0L)) {
								long freemem = Runtime.getRuntime().freeMemory();
								long maxmem = Runtime.getRuntime().maxMemory();
								long totmem = Runtime.getRuntime().totalMemory();
								System.out.println("Max memory:  " + maxmem);
								System.out.println("Tot memory:  " + totmem);
								System.out.println("Free memory: " + freemem);
								long usablemem = maxmem - totmem - freemem;
								System.out.println("Usable mem:  " + usablemem);
								long arraySize = Utils.calcArraySize(data);
								System.out.println("Array size:  " + arraySize);
								bufferSize = usablemem / (Utils.calcArraySize(data) + 1024L);
								if (bufferSize > entries.length) {
									bufferSize = entries.length;
								}
								System.out.println("Buffering " + bufferSize + " rows");
							}

							if ((row == 0) && (i >= entries.length - bufferSize)) {
								dataBuffer.add(data);
							}
							
							if (nameCol >= 0) {
								out.write(rowNames[row][nameCol] + "\t" + rowNames[i][nameCol]);
							} else {
								out.write(row + "\t" + i);
							}
							double val;

							if (method == 1) {
								val = calcy.pearsonCorrelation(data, project.mayContainBlankValues(),
										project.getBlankValue());
							} else if (method == 2) {
								val = calcy.newSpearmanCorrelation(data);
							} else {
								val = 0;
							}
							out.write("\t" + format.format(val));
							out.write("\r\n");
							if ((i == entries.length - 1) && (row != 0)) {
								correlationTime = (System.nanoTime() - corrStart) / entries.length;
								if (aveCorrTime < 0L) {
									aveCorrTime = correlationTime;
								} else {
									aveCorrTime = (aveCorrTime + correlationTime) / 2L;
								}
							}
						}

						if (row == 0) {
							System.out.println(dataBuffer.size() + " rows buffered.");
							timeStarted = System.nanoTime();
						}
					}

					long timeTaken = System.nanoTime() - timeStarted;
					System.out.println("Took " + Utils.convertNanoTimeToWords(timeTaken, true));
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Error reading project data",
							"IOException", 0);
					ioe.printStackTrace();
					progress.dispose();
					errored = true;
					return null;
				} catch (ArrayIndexOutOfBoundsException oob) {
					progress.dispose();
					errored = true;
					return null;
				}
				return null;
			}

			@Override
			public void finished() {
				try {
					out.close();
					JOptionPane.showMessageDialog(null, "File " + dest.getAbsolutePath() + " saved!",
							"Results saved to file", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e) {
					e.printStackTrace();
				}
				progress.dispose();
			}
		};
		analyzeWorker.start();
		progress.setVisible(true);
	}

	private static void transposeMatrix(File source, File dest, MetaOmProject project, int[] entries) {
		try {
			ArrayList<Long> rowStarts = new ArrayList();
			ArrayList<Long> colOffsets = new ArrayList();
			RandomAccessFile in = new RandomAccessFile(source, "r");
			int cols = in.readAndSplitLine('\t', false).length;
			in.seek(0L);
			in.readString('\t');
			for (int i = 1; i < cols; i++) {
				colOffsets.add(Long.valueOf(in.getFilePointer()));
			}
			in.seek(0L);
			do {
				rowStarts.add(Long.valueOf(in.getFilePointer()));
			} while (in.nextLine());
			in.seek(0L);
			BufferedWriter out = new BufferedWriter(new FileWriter(dest));
			Object[][] rowNames = project.getRowNames(entries);
			out.write(rowNames[0][project.getDefaultColumn()] + "");
			for (int i = 1; i < rowNames.length; i++) {
				out.write("\t" + rowNames[i][project.getDefaultColumn()]);
			}
			out.write("\r\n");
			for (int row = 0; row < cols - 1; row++) {
				out.write(rowNames[row][project.getDefaultColumn()] + "");
				for (int i = 0; i < row; i++) {
					in.seek(rowStarts.get(i).longValue() + colOffsets.get(row).longValue());
					out.write("\t" + in.readString('\t'));
				}
				in.seek(rowStarts.get(row).longValue() + colOffsets.get(row).longValue());
				out.write("\t" + in.readLine() + "\r\n");
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return;
		}
		BufferedWriter out;
		RandomAccessFile in;
	}

	public static void showExcludeDialog(MetaOmProject myProject, Frame parent) {
		ExcludeDialog excludeDialog = new ExcludeDialog(parent, "Exclude Samples from Correlation", true, myProject);
		excludeDialog.setLocationRelativeTo(parent);
		excludeDialog.setVisible(true);
	}

	private static class ExcludeDialog extends JDialog
			implements ActionListener, HashLoadable<MetaOmAnalyzer.ExcludeData> {
		private MetaOmProject myProject;

		private DualTablePanel dtp;

		private static final String OK_COMMAND = "ok";

		private static final String CANCEL_COMMAND = "cancel";
		private static final String METADATA_EXCLUDE_COMMAND = "Metadata exclude";
		private static final String METADATA_INCLUDE_COMMAND = "Metadata include";
		private static final String CREATE_FILE_COMMAND = "Create new file";

		public ExcludeDialog(Frame parent, String title, boolean modal, MetaOmProject myProject) {

			super(parent, title, modal);
			this.myProject = myProject;
			String[] headers = { "Number", "Sample Name" };
			if (MetaOmAnalyzer.excludeCount == 0) {
				Object[][] includedData = new Object[myProject.getDataColumnCount()][2];
				for (int i = 0; i < includedData.length; i++) {
					includedData[i][0] = new Integer(i);
					includedData[i][1] = myProject.getDataColumnHeader(i);
				}
				dtp = new DualTablePanel(includedData, headers, true);
			} else {
				Object[][] includedData = new Object[myProject.getDataColumnCount() - MetaOmAnalyzer.excludeCount][2];
				Object[][] excludedData = new Object[MetaOmAnalyzer.excludeCount][2];
				int excludeIndex = 0;
				int includeIndex = 0;
				for (int i = 0; i < myProject.getDataColumnCount(); i++) {
					if (MetaOmAnalyzer.exclude[i]) {
						excludedData[excludeIndex][0] = new Integer(i);
						excludedData[excludeIndex][1] = myProject.getDataColumnHeader(i);
						excludeIndex++;
					} else {
						includedData[includeIndex][0] = new Integer(i);
						includedData[includeIndex][1] = myProject.getDataColumnHeader(i);
						includeIndex++;
					}
				}
				dtp = new DualTablePanel(includedData, excludedData, headers, true);
			}
			dtp.hideColumn(0);

			ClearableTextField activeFilterField = new ClearableTextField();
			ClearableTextField inactiveFilterField = new ClearableTextField();
			activeFilterField.getDocument().addDocumentListener(dtp.getActiveTable().getFilterModel());
			inactiveFilterField.getDocument().addDocumentListener(dtp.getInactiveTable().getFilterModel());
			dtp.setActiveDecoration(activeFilterField);
			dtp.setInactiveDecoration(inactiveFilterField);
			dtp.setActiveLabel("Excluded");
			dtp.setInactiveLabel("Included");
			dtp.setAddButtonText("Exclude");
			dtp.setRemoveButtonText("Include");
			dtp.getActiveTable().setAutoResizeMode(2);
			dtp.getInactiveTable().setAutoResizeMode(2);

			dtp.setResetButtonText("<<< Include All");
			dtp.setAddAllButtonText("Exclude All");
			dtp.setResetButtonBehavior(0);
			dtp.setDefaultSortColumn(0);
			JPanel buttonPanel = new JPanel();

			JButton fileButton = new JButton("Create Data File");
			JButton okButton = new JButton("OK");
			JButton cancelButton = new JButton("Cancel");
			JButton metadataExcludeButton = new JButton("Metadata>>");
			JButton metadataIncludeButton = new JButton("<<Metadata");
			Dimension maxSize = metadataExcludeButton.getPreferredSize();
			maxSize.width = 10000;
			metadataExcludeButton.setMaximumSize(maxSize);
			metadataIncludeButton.setMaximumSize(maxSize);
			fileButton.setActionCommand("Create new file");
			fileButton.addActionListener(this);
			cancelButton.setActionCommand("cancel");
			cancelButton.addActionListener(this);
			okButton.setActionCommand("ok");
			okButton.addActionListener(this);
			metadataExcludeButton.setActionCommand("Metadata exclude");
			metadataExcludeButton.addActionListener(this);
			metadataIncludeButton.setActionCommand("Metadata include");
			metadataIncludeButton.addActionListener(this);
			buttonPanel.add(fileButton);
			buttonPanel.add(okButton);
			buttonPanel.add(cancelButton);

			dtp.getButtonPanel()
					.add(Box.createRigidArea(new Dimension(0, metadataExcludeButton.getPreferredSize().height)));
			dtp.getButtonPanel().add(metadataExcludeButton);
			dtp.getButtonPanel().add(metadataIncludeButton);
			HashtableSavePanel savePanel = new HashtableSavePanel(myProject.getSavedExcludes(), this);
			getContentPane().add(savePanel, "West");
			getContentPane().add(dtp, "Center");
			getContentPane().add(buttonPanel, "South");
			pack();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if ("cancel".equals(e.getActionCommand())) {
				dispose();
				return;
			}
			if ("ok".equals(e.getActionCommand())) {
				Object[][] excludedValues = dtp.getActiveValues();
				if (excludedValues.length == 0) {
					MetaOmAnalyzer.exclude = null;
					MetaOmAnalyzer.excludeCount = 0;
				} else {
					if (MetaOmAnalyzer.exclude == null) {
						MetaOmAnalyzer.exclude = new boolean[myProject.getDataColumnCount()];
					}
					for (int i = 0; i < MetaOmAnalyzer.exclude.length; i++) {
						MetaOmAnalyzer.exclude[i] = false;
					}
				}
				for (int i = 0; i < excludedValues.length; i++) {
					int thisRow = ((Integer) excludedValues[i][0]).intValue();
					MetaOmAnalyzer.exclude[thisRow] = true;
				}
				MetaOmAnalyzer.excludeCount = excludedValues.length;
				MetaOmGraph.fixTitle();
				dispose();
				return;
			}
			if ("Create new file".equals(e.getActionCommand())) {
				Object[][] excludedValues = dtp.getActiveValues();
				final boolean[] myExclude = new boolean[myProject.getDataColumnCount()];
				Arrays.fill(myExclude, false);
				for (int i = 0; i < excludedValues.length; i++) {
					int thisRow = ((Integer) excludedValues[i][0]).intValue();
					myExclude[thisRow] = true;
				}
				final File dest = Utils.chooseFileToSave(Utils.createFileFilter("txt", "Text files"), "txt", null,
						true);
				if (dest == null) {
					return;
				}
				final BlockingProgressDialog progress = new BlockingProgressDialog(this, "Saving",
						"Creating " + dest.getName(), 0L, myProject.getRowCount(), true);
				new Thread() {
					@Override
					public void run() {
						try {
							BufferedWriter out = new BufferedWriter(new FileWriter(dest));
							Object[] names = myProject.getInfoColumnNames();
							TreeMap<Integer, Integer> colMap = new TreeMap();
							for (int i = 0; i < names.length; i++) {
								if (i != 0) {
									out.write("\t");
								}
								if (names[i] != null) {
									out.write(names[i] + "");
								}
							}
							names = myProject.getDataColumnHeaders();
							Integer colIndex = Integer.valueOf(0);
							for (int i = 0; i < names.length; i++) {
								if (myExclude[i] == false) {
									out.write("\t" + names[i]);
									Integer tmp157_155 = colIndex;
									colIndex = Integer.valueOf(tmp157_155.intValue() + 1);
									colMap.put(Integer.valueOf(i), tmp157_155);
								}
							}
							out.newLine();
							for (int i = 0; i < myProject.getRowCount(); i++) {
								progress.setProgress(i);
								names = myProject.getRowName(i);
								for (int j = 0; j < names.length; j++) {
									if (j != 0) {
										out.write("\t");
									}
									if (names[j] != null) {
										out.write(Utils.stripHTML(names[j] + ""));
									}
								}
								double[] values = myProject.getAllData(i);
								for (int j = 0; j < values.length; j++) {
									if (myExclude[j] == false) {
										out.write("\t");
										if (!Double.isNaN(values[j])) {
											out.write(values[j] + "");
										}
									}
								}
								out.newLine();
							}
							out.close();
							File metadataDest = new File(dest.getAbsolutePath() + " - metadata.xml");
							out = new BufferedWriter(new FileWriter(metadataDest));
							SimpleXMLElement newMetadataRoot = myProject.getMetadata().splitMetadata(colMap);
							out.write(newMetadataRoot.toFullString());
							out.close();
						} catch (IOException ioe) {
							ioe.printStackTrace();
							JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(),
									"Unable to write to " + dest.getName(), "Error", 0);
						}
						progress.dispose();
					}
				}.start();
				progress.setVisible(true);
				return;
			}
			if (("Metadata include".equals(e.getActionCommand()))
					|| ("Metadata exclude".equals(e.getActionCommand()))) {
				final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(myProject, false);

				final Metadata.MetadataQuery[] queries = tsp.showSearchDialog();
				if (tsp.getQueryCount() <= 0) {
					return;
				}
				final Vector<Integer> hitColumns = new Vector();
				new AnimatedSwingWorker("Searching...", true) {
					@Override
					public Object construct() {
						Utils.startWatch();
						Integer[] hitCols = myProject.getMetadata().search(queries, tsp.matchAll());
						for (Integer col : hitCols) {
							hitColumns.add(col);
						}
						System.out.println("Metadata search took " + Utils.stopWatch() + "ms");
						return null;
					}

				}.start();
				boolean excluding = "Metadata exclude".equals(e.getActionCommand());
				TableModel model;
				if (excluding) {
					model = dtp.getInactiveModel();
				} else {
					model = dtp.getActiveModel();
				}
				Vector<Integer> rows = new Vector();

				for (Integer thisColumn : hitColumns) {
					for (int i = 0; i < model.getRowCount(); i++) {
						if (thisColumn.equals(model.getValueAt(i, 0))) {
							rows.add(i);
						}
					}
				}

				if (rows.size() <= 0) {
					return;
				}
				int[] swapUs = new int[rows.size()];
				int index = 0;
				for (Iterator localIterator2 = rows.iterator(); localIterator2.hasNext();) {
					int thisRow = ((Integer) localIterator2.next()).intValue();
					swapUs[(index++)] = thisRow;
				}
				if (excluding) {
					dtp.makeActive(swapUs);
				} else {
					dtp.makeInactive(swapUs);
				}
				return;
			}
		}

		@Override
		public MetaOmAnalyzer.ExcludeData getSaveData() {
			Object[][] excluded = dtp.getActiveValues();
			boolean[] excludeUs = new boolean[myProject.getDataColumnCount()];
			MetaOmAnalyzer.ExcludeData data = new MetaOmAnalyzer.ExcludeData();
			for (int i = 0; i < excludeUs.length; i++) {
				excludeUs[i] = false;
			}
			if (excluded.length <= 0) {
				exclude = excludeUs;
				excludeCount = 0;
				return data;
			}
			for (int i = 0; i < excluded.length; i++) {
				excludeUs[((Integer) excluded[i][0]).intValue()] = true;
			}
			exclude = excludeUs;
			excludeCount = excluded.length;
			return data;
		}

		@Override
		public void loadData(MetaOmAnalyzer.ExcludeData data) {
			Object[][] includedData = new Object[myProject.getDataColumnCount() - excludeCount][2];
			Object[][] excludedData = new Object[excludeCount][2];
			int excludeIndex = 0;
			int includeIndex = 0;
			for (int i = 0; i < myProject.getDataColumnCount(); i++) {
				if (exclude[i]) {
					excludedData[excludeIndex][0] = new Integer(i);
					excludedData[excludeIndex][1] = myProject.getDataColumnHeader(i);
					excludeIndex++;
				} else {
					includedData[includeIndex][0] = new Integer(i);
					includedData[includeIndex][1] = myProject.getDataColumnHeader(i);
					includeIndex++;
				}
			}
			dtp.setValues(includedData, excludedData, new String[] { "Number", "Sample Name" });
		}

		@Override
		public String getNoun() {
			return "exclude set";
		}
	}

	public static boolean[] getExclude() {
		return exclude;
	}

	public static int getExcludeCount() {
		return excludeCount;
	}

	public static void reset() {
		exclude = null;
		excludeCount = 0;
	}

	public static class ExcludeData implements XMLizable {
		boolean[] exclude;
		int excludeCount;

		public ExcludeData() {
		}

		@Override
		public void fromXML(Element source) {
			String[] excludeRows = source.getText().split(",");
			excludeCount = excludeRows.length;
			int sampleCount = Integer.parseInt(source.getAttributeValue("sampleCount"));
			exclude = new boolean[sampleCount];
			for (int i = 0; i < sampleCount; i++) {
				exclude[i] = false;
			}
			for (int i = 0; i < excludeCount; i++) {
				exclude[Integer.parseInt(excludeRows[i])] = true;
			}
		}

		@Override
		public Element toXML() {
			Element myElement = new Element(getXMLElementName());
			String excludeString = null;
			for (int i = 0; i < exclude.length; i++) {
				if (exclude[i]) {
					if (excludeString == null) {
						excludeString = i + "";
					} else {
						excludeString = excludeString + "," + i;
					}
				}
			}
			myElement.setText(excludeString);
			myElement.setAttribute("sampleCount", exclude.length + "");
			return myElement;
		}

		public static String getXMLElementName() {
			return "excludeList";
		}
	}

	public static double[] log2(double[] data) {
		double[] result = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			if (!Double.isNaN(data[i])) {
				result[i] = (Math.log(data[i]) / Math.log(2.0D));
			} else {
				result[i] = Double.NaN;
			}
		}
		return result;
	}
}
