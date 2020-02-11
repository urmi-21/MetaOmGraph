package edu.iastate.metnet.metaomgraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JOptionPane;

/**
 * Class to store replicate group objects based on old RepAveragedData class
 * 
 * @author urmi
 *
 */
public class ReplicateGroups {
	private String[] repGroupNames;
	private double[] values;
	private double[] stdDevs;
	private int[] repCounts;
	private double[] data;
	private int[] sortOrder;
	private TreeMap<String, List<Integer>> repsMap;
	private boolean[] excluded;
	private String delimChar="::-::";
	private boolean errorFlag=false;

	// create rep groups for current project
	public ReplicateGroups(TreeMap<String, List<Integer>> repsMapRaw, int row) {
		this.excluded = MetaOmAnalyzer.getExclude();
		// repsmap raw is reps map which contains all the data columns in group even
		// excluded ones.
		if (excluded == null) {
			this.repsMap = new TreeMap<>();
			this.repsMap.putAll(repsMapRaw);
		} else {
			this.repsMap = removeExcluded(repsMapRaw, excluded);
		}
		// repsMap maps repgroup name to its data columns
		repGroupNames = new String[repsMap.size()];
		values = new double[repsMap.size()];
		stdDevs = new double[repsMap.size()];
		// values = new double[MetaOmGraph.getActiveProject().getDataColumnCount()];
		// stdDevs = new double[MetaOmGraph.getActiveProject().getDataColumnCount()];
		// repCounts = new int[MetaOmGraph.getActiveProject().getDataColumnCount()];
		repCounts = new int[repsMap.size()];
		sortOrder = new int[repsMap.size()];
		try {
			data = MetaOmGraph.getActiveProject().getAllData(row);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// add
		int index = 0;

		Set<String> allKeys = repsMap.keySet();
		//JOptionPane.showMessageDialog(null, "all keys2:"+allKeys.toString());
		String[] allKeys_sorted = sortKeys(allKeys);
		//JOptionPane.showMessageDialog(null, "all keys3:"+Arrays.toString(allKeys_sorted));
		for (String key : allKeys_sorted) {
			repGroupNames[index] = key;
			double ave = 0.0D;
			int thisRepscount = 0;
			List<Integer> colList = repsMap.get(key);
			if(colList ==null) {
				//JOptionPane.showMessageDialog(null, "key:"+key+" "+repsMap.get(key));
				JOptionPane.showMessageDialog(null, "Please check if the metadata columns contains "+delimChar+", which is special sequence in MOG. Please remove this from the metadata and try again.", "Error", JOptionPane.ERROR_MESSAGE);
				JOptionPane.showMessageDialog(null, "Groups could not be made", "Error", JOptionPane.ERROR_MESSAGE);
				errorFlag=true;
				return;
			}
			
			for (Integer col : colList) {
				// skip values which are not present in data file
				if (col < 0) {
					//JOptionPane.showMessageDialog(null, "key"+key+" val:"+col);
					continue;
				}
				try {
					ave += data[col.intValue()];
					thisRepscount++;
				} catch (ArrayIndexOutOfBoundsException exception) {
					JOptionPane.showMessageDialog(null, "Error...:" + col);
					JOptionPane.showMessageDialog(null, "data col:" + col);
					JOptionPane.showMessageDialog(null, "data col name:" + MetaOmGraph.getActiveProject().getMetadata()
							.getNodeForCol(col).getAttributeValue("name"));
					JOptionPane.showMessageDialog(null, "data col int val:" + col.intValue());
				}

			}

			/**
			 * add values n times where n is number of datacols in a group
			 */
			repCounts[index] = thisRepscount;
			ave /= repCounts[index];
			/*
			 * for (int t=0;t<thisRepscount;t++) { repCounts[indexRepcount++] =
			 * thisRepscount; } ave /= repCounts[indexRepcount-1];
			 */
			// JOptionPane.showMessageDialog(null,"this avg bef:"+ave);
			// JOptionPane.showMessageDialog(null,"repind:"+repCounts[index]);
			// ave /= repCounts[index];
			// JOptionPane.showMessageDialog(null,"this avg:"+ave);
			/**
			 * add values n times where n is number of datacols in a group
			 */
			values[index] = ave;
			/*
			 * values[index2++]=ave; for (int t=1;t<thisRepscount;t++) { values[index2++] =
			 * -839.23183; }
			 */

			double diffSum = 0.0D;
			for (Integer col : colList) {
				// skip values which are not present in data file
				if (col < 0) {
					continue;
				}
				diffSum += (data[col.intValue()] - ave) * (data[col.intValue()] - ave);
			}
			diffSum /= repCounts[index];
			stdDevs[index] = Math.sqrt(diffSum);
			/**
			 * add values n times where n is number of datacols in a group
			 */
			/*
			 * for (int t=0;t<thisRepscount;t++) { stdDevs[index3++] = Math.sqrt(diffSum); }
			 */

			index++;
			// JOptionPane.showMessageDialog(null, "Repname:"+key);
			// JOptionPane.showMessageDialog(null, "repcnt:"+thisRepscount);
			// JOptionPane.showMessageDialog(null, "avg:"+ave);

		}
	}

	/**
	 * @author urmi
	 * @param repsMapRaw
	 *            treemap original with all datacolumns
	 * @param excluded2
	 *            excluded array
	 * @return
	 */
	private TreeMap<String, List<Integer>> removeExcluded(TreeMap<String, List<Integer>> repsMapRaw,
			boolean[] excluded2) {
		TreeMap<String, List<Integer>> temp = new TreeMap<>();
		Set<String> allKeys = repsMapRaw.keySet();
		for (String k : allKeys) {
			List<Integer> thisCols = repsMapRaw.get(k);
			List<Integer> toKeep = new ArrayList<>();
			for (Integer i : thisCols) {
				if (i >= 0 && !excluded2[i]) {
					toKeep.add(i);
				}
			}

			// only add those groups which have atleast one data non-excluded column present
			if (toKeep.size() > 0) {
				// JOptionPane.showMessageDialog(null, "add:" + toKeep.toString() + " to:" + k);
				temp.put(k, toKeep);
			}
		}
		return temp;
	}

	/**
	 * Sort the keys so groups are sorted according to the column in data file
	 * 
	 * @param allKeys
	 * @return
	 */
	private String[] sortKeys(Set<String> allKeys) {
		
		String[] res = new String[allKeys.size()];
		String datacolNames;
		int datacolIndex;
		int i = 0;
		for (String key : allKeys) {
			datacolNames = MetaOmGraph.getActiveProject().getMetadataHybrid().getXColumnNameRep(key, 0, this.repsMap);
			datacolIndex = MetaOmGraph.getActiveProject().findDataColumnHeader(datacolNames);
			res[i] = String.valueOf(datacolIndex) + delimChar + key;
			i++;
		}
		Arrays.sort(res);
		for (int j = 0; j < res.length; j++) {
			res[j] = res[j].split(delimChar)[1];
		}
		return res;
	}
	
	
	/**
	 * Remove the keys which have only -1 as columns
	 * @param allKeys
	 * @return
	 */
	private String[] removeEmptyKeys(Set<String> allKeys) {
		String[] res = new String[allKeys.size()];
		
		return res;
	}
	
	

	public String[] getGroupnames() {
		return this.repGroupNames;
	}

	/**
	 * 
	 * @return Return name of the first sample in each group
	 */
	public String[] getSampnames() {
		String[] gnames = getGroupnames();
		String[] sampnames = new String[gnames.length];
		for (int i = 0; i < sampnames.length; i++) {
			sampnames[i] = MetaOmGraph.getActiveProject().getMetadataHybrid().getXColumnNameRep(gnames[i], 0,
					this.repsMap);
		}
		return sampnames;
	}

	public double[] getValues() {
		return this.values;
	}

	public double[] getStdDev() {
		return this.stdDevs;
	}

	public int[] getRepCounts() {
		return this.repCounts;
	}
	
	public boolean getErrorStatus() {
		return this.errorFlag;
	}
}
