/**
 * 
 */
package edu.iastate.metnet.metaomgraph.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import java.util.TreeMap;

/**
 * BoxPlotSorter class to sort the boxplot in many categories.
 * @author sumanth
 *
 */
public class BoxPlotSorter {
	
	public BoxPlotSorter() {
	}
	
	// get row name.
	private String getRowName(String row) {
		return row.split("\\(")[0];
	}
	
	// get sample count from row name.
	private int getSampleCount(String row) {
		String cntVal = row.substring(row.indexOf("=") + 1, row.indexOf(")"));
		return Integer.parseInt(cntVal);
	}
	
	private Map<String, TreeMap<Double, ArrayList<String>>> getMedianColRowMap(DefaultBoxAndWhiskerCategoryDataset bawDataSet){
		
		Map<String, TreeMap<Double, ArrayList<String>>> medianColRowMap = 
				new HashMap<String, TreeMap<Double, ArrayList<String>>>();
		List<String> colKeys = bawDataSet.getColumnKeys();
		List<String> rowKeys = bawDataSet.getRowKeys();
		for(String col : colKeys) {
			TreeMap<Double, ArrayList<String>> medianRowMap = new TreeMap<Double, ArrayList<String>>();
			for(String row : rowKeys) {
				double median = bawDataSet.getMedianValue(row, col).doubleValue();
				String rowName = getRowName(row);
				if(medianRowMap.containsKey(median)) {
					medianRowMap.get(median).add(rowName);
				}
				else {
					ArrayList<String> rowList = new ArrayList<String>();
					rowList.add(rowName);
					medianRowMap.put(median, rowList);
				}
			}
			medianColRowMap.put(col, medianRowMap);
		}
		return medianColRowMap;
	}
	
	private Map<String, TreeMap<Double, ArrayList<String>>> getMeanColRowMap(DefaultBoxAndWhiskerCategoryDataset bawDataSet){
		
		Map<String, TreeMap<Double, ArrayList<String>>> meanColRowMap = 
				new HashMap<String, TreeMap<Double, ArrayList<String>>>();
		List<String> colKeys = bawDataSet.getColumnKeys();
		List<String> rowKeys = bawDataSet.getRowKeys();
		for(String col : colKeys) {
			TreeMap<Double, ArrayList<String>> meanRowMap = new TreeMap<Double, ArrayList<String>>();
			for(String row : rowKeys) {
				double mean = bawDataSet.getMeanValue(row, col).doubleValue();
				String rowName = getRowName(row);
				if(meanRowMap.containsKey(mean)) {
					meanRowMap.get(mean).add(rowName);
				}
				else {
					ArrayList<String> rowList = new ArrayList<String>();
					rowList.add(rowName);
					meanRowMap.put(mean, rowList);
				}
			}
			meanColRowMap.put(col, meanRowMap);
		}
		return meanColRowMap;
	}
	
	private Map<String, TreeMap<Integer, ArrayList<String>>> getSampleCntColRowMap(DefaultBoxAndWhiskerCategoryDataset bawDataSet){
		
		Map<String, TreeMap<Integer, ArrayList<String>>> sampleCntColRowMap = 
				new HashMap<String, TreeMap<Integer, ArrayList<String>>>();
		List<String> colKeys = bawDataSet.getColumnKeys();
		List<String> rowKeys = bawDataSet.getRowKeys();
		for(String col : colKeys) {
			TreeMap<Integer, ArrayList<String>> sampleCntRowMap = new TreeMap<Integer, ArrayList<String>>();
			for(String row : rowKeys) {
				int sampleCnt = getSampleCount(row);
				String rowName = getRowName(row);
				if(sampleCntRowMap.containsKey(sampleCnt)) {
					sampleCntRowMap.get(sampleCnt).add(rowName);
				}
				else {
					ArrayList<String> rowList = new ArrayList<String>();
					rowList.add(rowName);
					sampleCntRowMap.put(sampleCnt, rowList);
				}
			}
			sampleCntColRowMap.put(col, sampleCntRowMap);
		}
		return sampleCntColRowMap;
	}
	
	/**
	 * Construct a map with key as row(x-axis) name, value as another map with
	 * key as Index of the row and values as the collection of columns.
	 * @param rowNamesIndexMap.
	 * @return map that contains row names, Index of row and column names sorted by row Name.
	 */
	public Map<String, HashMap<Integer, ArrayList<String>>> sortByRowName(Map<String, HashMap<Integer, ArrayList<String>>> rowNamesIndexMap){
		Map<String, HashMap<Integer, ArrayList<String>>> treeMap = new TreeMap<String, HashMap<Integer, ArrayList<String>>>(rowNamesIndexMap);
		return treeMap;
	}
	
	/**
	 * Construct a map with key as row(x-axis) name, value as another map with
	 * key as Index of the row and values as the collection of columns.
	 * The map is sorted based on the median value.
	 * @param bawDataSet, rowNamesIndexMap, splitByCategories.
	 * @return map that contains row names, Index of row and column names sorted by median.
	 */
	public Map<String, HashMap<Integer, ArrayList<String>>> sortByMedian(DefaultBoxAndWhiskerCategoryDataset bawDataSet, 
			Map<String, HashMap<Integer, ArrayList<String>>> rowNamesIndexMap, boolean splitByCategories){
		
		Map<String, TreeMap<Double, ArrayList<String>>> medianColRowMap = getMedianColRowMap(bawDataSet);
		Map<String, HashMap<Integer, ArrayList<String>>> linkedMap = new LinkedHashMap<String, HashMap<Integer, ArrayList<String>>>();
		List<String> listOfRows = 
				new LinkedList<String>(rowNamesIndexMap.keySet());
		
		if(!splitByCategories) {
			Comparator<String> medianComparator = new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					TreeMap<Double, ArrayList<String>> medianMap1 = medianColRowMap.get(o1);
					Double median1 = medianMap1.entrySet().iterator().next().getKey();
					TreeMap<Double, ArrayList<String>> medianMap2 = medianColRowMap.get(o2);
					Double median2 = medianMap2.entrySet().iterator().next().getKey();
					return median1.compareTo(median2);
				}
			};

			Collections.sort(listOfRows, medianComparator);
		}
		
		for(String row : listOfRows){
			HashMap<Integer, ArrayList<String>> rowIndexColsMap = new HashMap<Integer, ArrayList<String>>();

			TreeMap<Double, ArrayList<String>> medianColMap = medianColRowMap.get(row);
			ArrayList<String> cols = new ArrayList<String>();
			for(Map.Entry<Double, ArrayList<String>> medianColEntry : medianColMap.entrySet()) {
				cols.addAll(medianColEntry.getValue());
			}
			rowIndexColsMap.put(rowNamesIndexMap.get(row).entrySet().iterator().next().getKey(), cols);
			linkedMap.put(row, rowIndexColsMap);
		}

		return linkedMap;
	}
	
	/**
	 * Construct a map with key as row(x-axis) name, value as another map with
	 * key as Index of the row and values as the collection of columns.
	 * The map is sorted based on the mean value.
	 * @param bawDataSet, rowNamesIndexMap, splitByCategories.
	 * @return map that contains row names, Index of row and column names sorted by mean.
	 */
	public Map<String, HashMap<Integer, ArrayList<String>>> sortByMean(DefaultBoxAndWhiskerCategoryDataset bawDataSet,
			Map<String, HashMap<Integer, ArrayList<String>>> rowNamesIndexMap, boolean splitByCategories){
		
		Map<String, TreeMap<Double, ArrayList<String>>> meanColRowMap = getMeanColRowMap(bawDataSet);

		Map<String, HashMap<Integer, ArrayList<String>>> linkedMap = new LinkedHashMap<String, HashMap<Integer, ArrayList<String>>>();

		List<String> listOfRows = 
				new LinkedList<String>(rowNamesIndexMap.keySet());
		if(!splitByCategories) {
			Comparator<String> meanComparator = new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					TreeMap<Double, ArrayList<String>> meanMap1 = meanColRowMap.get(o1);
					Double mean1 = meanMap1.entrySet().iterator().next().getKey();
					TreeMap<Double, ArrayList<String>> meanMap2 = meanColRowMap.get(o2);
					Double mean2 = meanMap2.entrySet().iterator().next().getKey();
					return mean1.compareTo(mean2);
				}
			};

			Collections.sort(listOfRows, meanComparator);
		}
		
		for(String row : listOfRows){
			HashMap<Integer, ArrayList<String>> rowIndexColsMap = new HashMap<Integer, ArrayList<String>>();
			
			TreeMap<Double, ArrayList<String>> meanColMap = meanColRowMap.get(row);
			ArrayList<String> cols = new ArrayList<String>();
			for(Map.Entry<Double, ArrayList<String>> meanColEntry : meanColMap.entrySet()) {
				cols.addAll(meanColEntry.getValue());
			}
			rowIndexColsMap.put(rowNamesIndexMap.get(row).entrySet().iterator().next().getKey(), cols);
			linkedMap.put(row, rowIndexColsMap);
		}

		return linkedMap;
	}
	
	/**
	 * Construct a map with key as row(x-axis) name, value as another map with
	 * key as Index of the row and values as the collection of columns.
	 * This map is sorted based on the sample count.
	 * @param bawDataSet, rowNamesIndexMap.
	 * @return map that contains row names, Index of row and column names sorted by samplecount.
	 */
	public Map<String, HashMap<Integer, ArrayList<String>>> sortBySampleCount(DefaultBoxAndWhiskerCategoryDataset bawDataSet,
			Map<String, HashMap<Integer, ArrayList<String>>> rowNamesIndexMap){
		
		Map<String, TreeMap<Integer, ArrayList<String>>> rowSampleCntColMap = getSampleCntColRowMap(bawDataSet);
		
		Map<String, HashMap<Integer, ArrayList<String>>> linkedMap = new LinkedHashMap<String, HashMap<Integer, ArrayList<String>>>();
		for(String row : rowNamesIndexMap.keySet()){
			HashMap<Integer, ArrayList<String>> rowIndexColsMap = new HashMap<Integer, ArrayList<String>>();
			
			TreeMap<Integer, ArrayList<String>> sampleCntMap = rowSampleCntColMap.get(row);
			ArrayList<String> cols = new ArrayList<String>();
			for(Map.Entry<Integer, ArrayList<String>> meanColEntry : sampleCntMap.entrySet()) {
				cols.addAll(meanColEntry.getValue());
			}
			rowIndexColsMap.put(rowNamesIndexMap.get(row).entrySet().iterator().next().getKey(), cols);
			linkedMap.put(row, rowIndexColsMap);
		}
		
		return linkedMap;
	}
}
